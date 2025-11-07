package com.heritage.service;

import com.heritage.dto.UpdateUtilisateurRequest;
import com.heritage.dto.UtilisateurAvecRoleFamilleDTO;
import com.heritage.dto.UtilisateurDTO;
import com.heritage.entite.MembreFamille;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des utilisateurs.
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                             MembreFamilleRepository membreFamilleRepository,
                             PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère un utilisateur par son ID.
     * 
     * @param id ID de l'utilisateur
     * @return DTO de l'utilisateur (sans mot de passe)
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUserById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        return convertToDTO(utilisateur);
    }

    /**
     * Récupère un utilisateur par email.
     * 
     * @param email Email de l'utilisateur
     * @return DTO de l'utilisateur (sans mot de passe)
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUserByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        return convertToDTO(utilisateur);
    }

    /**
     * Récupère un utilisateur avec son rôle dans une famille spécifique.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param familleId ID de la famille
     * @return DTO de l'utilisateur avec son rôle dans la famille spécifiée
     */
    @Transactional(readOnly = true)
    public UtilisateurAvecRoleFamilleDTO getUserWithRoleInFamille(Long utilisateurId, Long familleId) {
        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // Récupérer le rôle de l'utilisateur dans la famille
        MembreFamille membreFamille = membreFamilleRepository.findByUtilisateurIdAndFamilleId(utilisateurId, familleId)
                .orElseThrow(() -> new NotFoundException("L'utilisateur n'est pas membre de cette famille"));

        return convertToUtilisateurAvecRoleFamilleDTO(utilisateur, membreFamille);
    }

    /**
     * Met à jour les informations d'un utilisateur.
     * 
     * @param id ID de l'utilisateur à modifier
     * @param request Données de mise à jour
     * @return DTO de l'utilisateur mis à jour
     */
    @Transactional
    public UtilisateurDTO updateUtilisateur(Long id, UpdateUtilisateurRequest request) {
        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // Mise à jour des champs uniquement si fournis
        if (request.getNom() != null && !request.getNom().trim().isEmpty()) {
            utilisateur.setNom(request.getNom().trim());
        }

        if (request.getPrenom() != null && !request.getPrenom().trim().isEmpty()) {
            utilisateur.setPrenom(request.getPrenom().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Vérifier que le nouvel email n'est pas déjà utilisé par un autre utilisateur
            if (!request.getEmail().equals(utilisateur.getEmail()) && 
                utilisateurRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Cet email est déjà utilisé par un autre utilisateur");
            }
            utilisateur.setEmail(request.getEmail().trim());
        }

        if (request.getNumeroTelephone() != null) {
            utilisateur.setNumeroTelephone(request.getNumeroTelephone().trim());
        }

        if (request.getEthnie() != null) {
            utilisateur.setEthnie(request.getEthnie().trim());
        }

        // Mise à jour du mot de passe si fourni
        if (request.getMotDePasse() != null && !request.getMotDePasse().trim().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        // Sauvegarder les modifications (dateModification sera automatiquement mise à jour)
        utilisateur = utilisateurRepository.save(utilisateur);

        return convertToDTO(utilisateur);
    }

    /**
     * Convertit une entité Utilisateur en DTO.
     * IMPORTANT : Le mot de passe n'est JAMAIS inclus dans le DTO.
     * 
     * @param utilisateur Entité à convertir
     * @return DTO sans informations sensibles
     */
    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .numeroTelephone(utilisateur.getNumeroTelephone())
                .ethnie(utilisateur.getEthnie())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .dateModification(utilisateur.getDateModification())
                .build();
    }

    /**
     * Convertit un Utilisateur et son MembreFamille en UtilisateurAvecRoleFamilleDTO.
     * IMPORTANT : Le mot de passe n'est JAMAIS inclus dans le DTO.
     * 
     * @param utilisateur Entité Utilisateur
     * @param membreFamille Entité MembreFamille (relation avec la famille)
     * @return DTO avec les informations de l'utilisateur et son rôle dans la famille
     */
    private UtilisateurAvecRoleFamilleDTO convertToUtilisateurAvecRoleFamilleDTO(Utilisateur utilisateur, MembreFamille membreFamille) {
        return UtilisateurAvecRoleFamilleDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .numeroTelephone(utilisateur.getNumeroTelephone())
                .ethnie(utilisateur.getEthnie())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .idFamille(membreFamille.getFamille().getId())
                .nomFamille(membreFamille.getFamille().getNom())
                .roleFamille(membreFamille.getRoleFamille().name())
                .lienParente(membreFamille.getLienParente())
                .dateAjoutFamille(membreFamille.getDateAjout())
                .build();
    }
}

