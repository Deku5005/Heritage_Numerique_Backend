package com.heritage.service;

import com.heritage.dto.UtilisateurDTO;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des utilisateurs.
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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
                .build();
    }
}

