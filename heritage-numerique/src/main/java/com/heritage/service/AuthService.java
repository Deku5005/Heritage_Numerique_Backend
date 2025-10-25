package com.heritage.service;

import com.heritage.dto.AuthResponse;
import com.heritage.dto.LoginRequest;
import com.heritage.dto.LoginWithCodeRequest;
import com.heritage.dto.RegisterRequest;
import com.heritage.entite.Invitation;
import com.heritage.entite.MembreFamille;
import com.heritage.entite.RoleFamille;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.InvitationRepository;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import com.heritage.securite.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service d'authentification.
 * 
 * Responsabilités :
 * - Inscription des nouveaux utilisateurs
 * - Validation du code d'invitation
 * - Création automatique de membre_famille lors de l'inscription avec code
 * - Connexion et génération de JWT
 * 
 * Sécurité :
 * - Hashage des mots de passe avec BCrypt (via PasswordEncoder)
 * - Validation stricte du code d'invitation (code + email + expiration)
 * - Génération de tokens JWT sécurisés
 */
@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final InvitationRepository invitationRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            InvitationRepository invitationRepository,
            MembreFamilleRepository membreFamilleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.utilisateurRepository = utilisateurRepository;
        this.invitationRepository = invitationRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     * 
     * Processus :
     * 1. Vérifier que l'email n'existe pas déjà
     * 2. Si code d'invitation fourni :
     *    a. Valider le code (existence, expiration, email correspondant)
     *    b. Créer l'utilisateur
     *    c. Lier l'utilisateur à la famille via membre_famille
     *    d. Mettre à jour le statut de l'invitation à ACCEPTEE
     * 3. Sinon : créer simplement l'utilisateur
     * 4. Hasher le mot de passe avec BCrypt
     * 5. Générer un token JWT
     * 
     * Sécurité :
     * - Le mot de passe est TOUJOURS hashé (jamais stocké en clair)
     * - Vérification que l'email d'inscription correspond à l'email invité
     *   (empêche l'utilisation frauduleuse d'un code d'invitation)
     * - Les invitations expirées sont rejetées
     * 
     * @param request Requête d'inscription
     * @return Réponse avec token JWT et infos utilisateur
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Vérifier que l'email n'existe pas déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte existe déjà avec cet email");
        }

        Invitation invitation = null;
        
        // 2. Si un code d'invitation est fourni, le valider
        if (request.getCodeInvitation() != null && !request.getCodeInvitation().isEmpty()) {
            invitation = validateInvitationCode(request.getCodeInvitation(), request.getEmail());
        }

        // 3. Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setNumeroTelephone(request.getNumeroTelephone());
        utilisateur.setEthnie(request.getEthnie());
        
        // Hasher le mot de passe avec BCrypt avant de le stocker
        // BCrypt génère automatiquement un salt unique pour chaque mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        
        utilisateur.setRole("ROLE_MEMBRE"); // Rôle par défaut
        utilisateur.setActif(true);

        // Sauvegarder l'utilisateur
        utilisateur = utilisateurRepository.save(utilisateur);

        // 4. Si invitation valide, créer le lien membre_famille
        // IMPORTANT : L'invitation reste EN_ATTENTE jusqu'à ce que l'utilisateur
        // l'accepte ou la refuse depuis son dashboard personnel
        if (invitation != null) {
            // Lier l'utilisateur à l'invitation
            invitation.setUtilisateurInvite(utilisateur);
            invitationRepository.save(invitation);

            // Créer l'appartenance à la famille avec rôle LECTEUR par défaut
            // L'utilisateur devient membre mais doit encore accepter formellement
            MembreFamille membreFamille = new MembreFamille();
            membreFamille.setUtilisateur(utilisateur);
            membreFamille.setFamille(invitation.getFamille());
            membreFamille.setRoleFamille(RoleFamille.LECTEUR); // Rôle par défaut
            membreFamilleRepository.save(membreFamille);

            // L'invitation reste EN_ATTENTE
            // L'utilisateur doit accepter/refuser depuis son dashboard
            // (l'invitation n'est PAS automatiquement acceptée)
        }

        // 5. Générer le token JWT
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(utilisateur.getRole())
                .build();

        String token = jwtService.generateToken(userDetails, utilisateur.getId(), utilisateur.getRole());

        // 6. Construire la réponse
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole())
                .build();
    }

    /**
     * Valide un code d'invitation.
     * 
     * Vérifications de sécurité :
     * - Le code existe dans la base de données
     * - L'email d'inscription correspond à l'email invité (sécurité critique)
     * - Le statut est EN_ATTENTE (pas déjà utilisée)
     * - La date d'expiration n'est pas dépassée
     * 
     * Choix de sécurité :
     * On vérifie que l'email correspond pour garantir que seule la personne
     * invitée peut utiliser le code. Si l'admin souhaite autoriser n'importe
     * quel email, il faudrait retirer cette vérification (déconseillé).
     * 
     * @param codeInvitation Code à valider
     * @param email Email de l'utilisateur qui s'inscrit
     * @return Invitation valide
     */
    private Invitation validateInvitationCode(String codeInvitation, String email) {
        // Rechercher l'invitation par code
        Optional<Invitation> invitationOpt = invitationRepository.findByCodeInvitation(codeInvitation);
        
        if (invitationOpt.isEmpty()) {
            throw new BadRequestException("Code d'invitation invalide");
        }

        Invitation invitation = invitationOpt.get();

        // Vérifier que l'email correspond (sécurité)
        if (!invitation.getEmailInvite().equalsIgnoreCase(email)) {
            throw new BadRequestException("Ce code d'invitation n'est pas associé à votre email");
        }

        // Vérifier le statut
        if (!"EN_ATTENTE".equals(invitation.getStatut())) {
            throw new BadRequestException("Ce code d'invitation a déjà été utilisé ou refusé");
        }

        // Vérifier l'expiration
        if (invitation.getDateExpiration().isBefore(LocalDateTime.now())) {
            // Mettre à jour le statut à EXPIREE
            invitation.setStatut("EXPIREE");
            invitationRepository.save(invitation);
            throw new BadRequestException("Ce code d'invitation a expiré");
        }

        return invitation;
    }

    /**
     * Connexion d'un utilisateur existant.
     * 
     * Processus :
     * 1. Authentifier avec email/mot de passe via AuthenticationManager
     * 2. Charger l'utilisateur depuis la base de données
     * 3. Générer un nouveau token JWT
     * 
     * Sécurité :
     * - AuthenticationManager utilise BCrypt pour vérifier le mot de passe
     * - Le mot de passe n'est JAMAIS renvoyé dans la réponse
     * 
     * @param request Requête de connexion
     * @return Réponse avec token JWT et infos utilisateur
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            // 1. Authentifier l'utilisateur
            // AuthenticationManager vérifie automatiquement le mot de passe hashé
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            // 2. Charger l'utilisateur complet depuis la base
            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

            // 3. Générer le token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails, utilisateur.getId(), utilisateur.getRole());

            // 4. Construire la réponse (sans le mot de passe !)
            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .userId(utilisateur.getId())
                    .email(utilisateur.getEmail())
                    .nom(utilisateur.getNom())
                    .prenom(utilisateur.getPrenom())
                    .role(utilisateur.getRole())
                    .build();

        } catch (Exception e) {
            // Ne pas divulguer d'informations sur l'existence du compte
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }
    }

    /**
     * Connexion avec code d'invitation pour utilisateur existant.
     * 
     * Processus :
     * 1. Authentifier l'utilisateur avec email/mot de passe
     * 2. Valider le code d'invitation
     * 3. Créer le lien membre_famille
     * 4. Mettre à jour le statut de l'invitation
     * 5. Générer le token JWT
     * 
     * @param request Requête avec email, mot de passe et code d'invitation
     * @return Réponse avec token JWT et infos utilisateur
     */
    public AuthResponse loginWithCode(LoginWithCodeRequest request) {
        try {
            // 1. Authentifier l'utilisateur avec email/mot de passe
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            // 2. Charger l'utilisateur complet
            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

            // 3. Valider le code d'invitation
            Invitation invitation = validateInvitationCode(request.getCodeInvitation(), request.getEmail());

            // 4. Vérifier que l'utilisateur n'est pas déjà membre de cette famille
            boolean dejaMembre = membreFamilleRepository
                    .existsByUtilisateurIdAndFamilleId(utilisateur.getId(), invitation.getFamille().getId());
            
            if (dejaMembre) {
                throw new BadRequestException("Vous êtes déjà membre de cette famille");
            }

            // 5. Créer le lien membre_famille
            MembreFamille membreFamille = new MembreFamille();
            membreFamille.setUtilisateur(utilisateur);
            membreFamille.setFamille(invitation.getFamille());
            membreFamille.setRoleFamille(RoleFamille.LECTEUR); // Rôle par défaut
            membreFamilleRepository.save(membreFamille);

            // 6. Mettre à jour le statut de l'invitation
            invitation.setStatut("ACCEPTEE");
            invitation.setUtilisateurInvite(utilisateur);
            invitationRepository.save(invitation);

            // 7. Générer le token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails, utilisateur.getId(), utilisateur.getRole());

            // 8. Construire la réponse
            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .userId(utilisateur.getId())
                    .email(utilisateur.getEmail())
                    .nom(utilisateur.getNom())
                    .prenom(utilisateur.getPrenom())
                    .role(utilisateur.getRole())
                    .build();

        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw e;
            }
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }
    }
}

