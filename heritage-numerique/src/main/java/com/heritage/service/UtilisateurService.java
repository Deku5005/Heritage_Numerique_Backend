package com.heritage.service;

import com.heritage.dto.FamilleUtilisateurDTO;
import com.heritage.dto.UtilisateurAvecRoleFamilleDTO;
import com.heritage.dto.UtilisateurDTO;
import com.heritage.entite.MembreFamille;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs.
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                             MembreFamilleRepository membreFamilleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.membreFamilleRepository = membreFamilleRepository;
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
     * Convertit une entité Utilisateur en DTO.
     * IMPORTANT : Le mot de passe n'est JAMAIS inclus dans le DTO.
     * Inclut les familles de l'utilisateur avec leurs rôles.
     * 
     * @param utilisateur Entité à convertir
     * @return DTO sans informations sensibles
     */
    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        // Convertir les membres de famille en DTOs
        List<FamilleUtilisateurDTO> famillesDTO = utilisateur.getMembresFamille().stream()
                .map(this::convertMembreFamilleToDTO)
                .collect(Collectors.toList());

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
                .familles(famillesDTO)
                .build();
    }

    /**
     * Convertit un MembreFamille en FamilleUtilisateurDTO.
     * 
     * @param membreFamille Entité MembreFamille à convertir
     * @return DTO avec les informations de la famille et du rôle
     */
    private FamilleUtilisateurDTO convertMembreFamilleToDTO(MembreFamille membreFamille) {
        return FamilleUtilisateurDTO.builder()
                .idFamille(membreFamille.getFamille().getId())
                .nomFamille(membreFamille.getFamille().getNom())
                .descriptionFamille(membreFamille.getFamille().getDescription())
                .ethnie(membreFamille.getFamille().getEthnie())
                .region(membreFamille.getFamille().getRegion())
                .roleFamille(membreFamille.getRoleFamille().name())
                .lienParente(membreFamille.getLienParente())
                .dateAjout(membreFamille.getDateAjout())
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

