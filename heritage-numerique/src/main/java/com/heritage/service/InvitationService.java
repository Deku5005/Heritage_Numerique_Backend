package com.heritage.service;

import com.heritage.dto.InvitationDTO;
import com.heritage.dto.InvitationRequest;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.FamilleRepository;
import com.heritage.repository.InvitationRepository;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des invitations.
 * 
 * Responsabilités :
 * - Créer des invitations avec code unique
 * - Générer des codes alphanumériques sécurisés
 * - Expirer automatiquement les invitations anciennes (30 jours)
 * - Récupérer les invitations d'une famille ou d'un utilisateur
 * 
 * Sécurité :
 * - Codes d'invitation générés aléatoirement (8 caractères alphanumériques)
 * - Expiration automatique après 30 jours
 * - Vérification que l'émetteur a les droits sur la famille
 */
@Service
public class InvitationService {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int EXPIRATION_HOURS = 48; // 48 heures au lieu de 30 jours

    private final InvitationRepository invitationRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public InvitationService(
            InvitationRepository invitationRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            MembreFamilleRepository membreFamilleRepository,
            EmailService emailService) {
        this.invitationRepository = invitationRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.emailService = emailService;
    }

    /**
     * Crée une nouvelle invitation pour rejoindre une famille.
     * 
     * Processus :
     * 1. Vérifier que la famille existe
     * 2. Vérifier que l'émetteur existe
     * 3. Générer un code d'invitation unique
     * 4. Définir la date d'expiration (30 jours)
     * 5. Sauvegarder l'invitation
     * 
     * @param request Requête de création d'invitation
     * @param emetteurId ID de l'utilisateur qui envoie l'invitation
     * @return DTO de l'invitation créée
     */
    @Transactional
    public InvitationDTO createInvitation(InvitationRequest request, Long emetteurId) {
        // 1. Vérifier que la famille existe
        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 2. Vérifier que l'émetteur existe
        Utilisateur emetteur = utilisateurRepository.findById(emetteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // TODO: Vérifier que l'émetteur est admin de la famille (via membre_famille)
        // Cette vérification devrait être ajoutée en fonction des règles métier

        // 3. Générer un code d'invitation unique
        String codeInvitation = generateUniqueInvitationCode();

        // 4. Créer l'invitation
        Invitation invitation = new Invitation();
        invitation.setFamille(famille);
        invitation.setEmetteur(emetteur);
        invitation.setNomInvite(request.getNomInvite());
        invitation.setEmailInvite(request.getEmailInvite());
        invitation.setTelephoneInvite(request.getTelephoneInvite());
        invitation.setLienParente(request.getLienParente());
        invitation.setCodeInvitation(codeInvitation);
        invitation.setStatut("EN_ATTENTE");
        invitation.setDateExpiration(LocalDateTime.now().plusHours(EXPIRATION_HOURS));

        invitation = invitationRepository.save(invitation);

        // 5. Vérifier si l'utilisateur existe déjà
        boolean utilisateurExiste = utilisateurRepository.existsByEmail(request.getEmailInvite());
        
        // 6. Envoyer l'email approprié
        String nomEmetteur = emetteur.getNom() + " " + emetteur.getPrenom();
        if (utilisateurExiste) {
            // Utilisateur existant : email avec code de connexion
            emailService.envoyerCodeConnexion(
                request.getEmailInvite(),
                request.getNomInvite(),
                famille.getNom(),
                codeInvitation,
                nomEmetteur,
                request.getLienParente()
            );
        } else {
            // Nouvel utilisateur : email d'inscription
            emailService.envoyerInvitationNouvelUtilisateur(
                request.getEmailInvite(),
                request.getNomInvite(),
                famille.getNom(),
                codeInvitation,
                nomEmetteur,
                request.getLienParente()
            );
        }

        // 7. Convertir en DTO
        return convertToDTO(invitation);
    }

    /**
     * Génère un code d'invitation unique.
     * 
     * Sécurité :
     * - Utilise SecureRandom pour la génération aléatoire cryptographique
     * - Code alphanumérique de 8 caractères
     * - Vérifie l'unicité dans la base de données
     * - Réessaye jusqu'à trouver un code unique (probabilité de collision très faible)
     * 
     * @return Code d'invitation unique
     */
    private String generateUniqueInvitationCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            code = generateRandomCode();
            attempts++;
            
            if (attempts >= maxAttempts) {
                throw new RuntimeException("Impossible de générer un code d'invitation unique");
            }
        } while (invitationRepository.existsByCodeInvitation(code));

        return code;
    }

    /**
     * Génère un code alphanumérique aléatoire.
     * 
     * @return Code de 8 caractères
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHANUMERIC.length());
            code.append(ALPHANUMERIC.charAt(index));
        }
        return code.toString();
    }

    /**
     * Récupère toutes les invitations d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des invitations
     */
    @Transactional(readOnly = true)
    public List<InvitationDTO> getInvitationsByFamille(Long familleId) {
        List<Invitation> invitations = invitationRepository.findByFamilleId(familleId);
        return invitations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les invitations envoyées par un utilisateur.
     * 
     * @param emetteurId ID de l'émetteur
     * @return Liste des invitations
     */
    @Transactional(readOnly = true)
    public List<InvitationDTO> getInvitationsByEmetteur(Long emetteurId) {
        List<Invitation> invitations = invitationRepository.findByEmetteurId(emetteurId);
        return invitations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Expire automatiquement les invitations anciennes.
     * 
     * Job planifié qui s'exécute toutes les heures.
     * Met à jour le statut des invitations expirées à EXPIREE.
     * 
     * Sécurité :
     * - Les invitations expirent après 48 heures
     * - Empêche l'utilisation de codes d'invitation obsolètes
     * - Nettoie automatiquement la base de données
     */
    @Scheduled(cron = "0 0 * * * *") // Toutes les heures
    @Transactional
    public void expireOldInvitations() {
        LocalDateTime now = LocalDateTime.now();
        List<Invitation> expiredInvitations = invitationRepository
                .findExpiredInvitations(now, "EN_ATTENTE");

        for (Invitation invitation : expiredInvitations) {
            invitation.setStatut("EXPIREE");
        }

        if (!expiredInvitations.isEmpty()) {
            invitationRepository.saveAll(expiredInvitations);
            System.out.println("Expired " + expiredInvitations.size() + " invitations");
        }
    }

    /**
     * Récupère les invitations en attente pour un utilisateur.
     * Utilisé pour afficher les invitations dans le dashboard personnel.
     * 
     * @param email Email de l'utilisateur
     * @return Liste des invitations en attente
     */
    @Transactional(readOnly = true)
    public List<InvitationDTO> getInvitationsEnAttente(String email) {
        List<Invitation> invitations = invitationRepository
                .findByEmailInviteAndStatut(email, "EN_ATTENTE");
        
        return invitations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Accepte une invitation et active l'appartenance à la famille.
     * 
     * Workflow :
     * 1. Vérifier que l'invitation existe et est EN_ATTENTE
     * 2. Vérifier que l'email de l'utilisateur correspond
     * 3. Vérifier que l'utilisateur est déjà membre (créé lors de l'inscription)
     * 4. Mettre à jour le statut de l'invitation à ACCEPTEE
     * 5. Notifier l'admin de la famille
     * 
     * @param invitationId ID de l'invitation
     * @param utilisateurId ID de l'utilisateur qui accepte
     */
    @Transactional
    public void accepterInvitation(Long invitationId, Long utilisateurId) {
        // 1. Vérifier que l'invitation existe
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Invitation non trouvée"));

        // 2. Vérifier le statut
        if (!"EN_ATTENTE".equals(invitation.getStatut())) {
            throw new BadRequestException("Cette invitation a déjà été traitée");
        }

        // 3. Vérifier que l'utilisateur existe et que son email correspond
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!invitation.getEmailInvite().equalsIgnoreCase(utilisateur.getEmail())) {
            throw new UnauthorizedException("Cette invitation n'est pas pour vous");
        }

        // 4. Vérifier que l'utilisateur est déjà membre (créé lors de l'inscription avec code)
        // Si pas encore membre, on le crée maintenant
        var membreOpt = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(utilisateurId, invitation.getFamille().getId());

        if (membreOpt.isEmpty()) {
            // Créer le lien membre_famille si pas encore fait
            MembreFamille membreFamille = new MembreFamille();
            membreFamille.setUtilisateur(utilisateur);
            membreFamille.setFamille(invitation.getFamille());
            membreFamille.setRoleFamille(RoleFamille.LECTEUR);
            membreFamilleRepository.save(membreFamille);
        }

        // 5. Mettre à jour le statut de l'invitation
        invitation.setStatut("ACCEPTEE");
        invitation.setDateUtilisation(LocalDateTime.now());
        invitationRepository.save(invitation);

        // TODO: Envoyer notification à l'admin de la famille
        // notificationService.notifierAcceptationInvitation(
        //     invitation.getEmetteur().getId(),
        //     utilisateur.getNom() + " " + utilisateur.getPrenom(),
        //     invitation.getFamille().getNom()
        // );
    }

    /**
     * Refuse une invitation.
     * 
     * Workflow :
     * 1. Vérifier que l'invitation existe et est EN_ATTENTE
     * 2. Vérifier que l'email de l'utilisateur correspond
     * 3. Mettre à jour le statut à REFUSEE
     * 4. Supprimer le lien membre_famille s'il existe
     * 5. Notifier l'admin de la famille
     * 
     * @param invitationId ID de l'invitation
     * @param utilisateurId ID de l'utilisateur qui refuse
     */
    @Transactional
    public void refuserInvitation(Long invitationId, Long utilisateurId) {
        // 1. Vérifier que l'invitation existe
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Invitation non trouvée"));

        // 2. Vérifier le statut
        if (!"EN_ATTENTE".equals(invitation.getStatut())) {
            throw new BadRequestException("Cette invitation a déjà été traitée");
        }

        // 3. Vérifier que l'utilisateur existe et que son email correspond
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!invitation.getEmailInvite().equalsIgnoreCase(utilisateur.getEmail())) {
            throw new UnauthorizedException("Cette invitation n'est pas pour vous");
        }

        // 4. Supprimer le lien membre_famille si créé lors de l'inscription
        var membreOpt = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(utilisateurId, invitation.getFamille().getId());
        
        membreOpt.ifPresent(membreFamilleRepository::delete);

        // 5. Mettre à jour le statut de l'invitation
        invitation.setStatut("REFUSEE");
        invitation.setDateUtilisation(LocalDateTime.now());
        invitationRepository.save(invitation);

        // TODO: Envoyer notification à l'admin de la famille
    }

    /**
     * Convertit une entité Invitation en DTO.
     * 
     * @param invitation Entité à convertir
     * @return DTO
     */
    private InvitationDTO convertToDTO(Invitation invitation) {
        return InvitationDTO.builder()
                .id(invitation.getId())
                .idFamille(invitation.getFamille().getId())
                .nomFamille(invitation.getFamille().getNom())
                .nomInvite(invitation.getNomInvite())
                .emailInvite(invitation.getEmailInvite())
                .telephoneInvite(invitation.getTelephoneInvite())
                .lienParente(invitation.getLienParente())
                .codeInvitation(invitation.getCodeInvitation())
                .statut(invitation.getStatut())
                .dateCreation(invitation.getDateCreation())
                .dateExpiration(invitation.getDateExpiration())
                .build();
    }
}

