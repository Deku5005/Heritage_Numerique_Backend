package com.heritage.service;

import com.heritage.dto.DevinetteDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.ContenuRepository;
import com.heritage.repository.MembreFamilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des devinettes de famille.
 * Fournit des informations détaillées sur les devinettes créées par les membres.
 */
@Service
public class DevinetteService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public DevinetteService(ContenuRepository contenuRepository,
                           MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère toutes les devinettes d'une famille avec leurs informations détaillées.
     * 
     * @param familleId ID de la famille
     * @return Liste des devinettes avec informations des auteurs
     */
    @Transactional(readOnly = true)
    public List<DevinetteDTO> getDevinettesByFamille(Long familleId) {
        List<Contenu> devinettes = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "DEVINETTE");
        
        return devinettes.stream()
                .map(this::convertToDevinetteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une devinette spécifique par son ID.
     * 
     * @param devinetteId ID de la devinette
     * @return DTO de la devinette
     */
    @Transactional(readOnly = true)
    public DevinetteDTO getDevinetteById(Long devinetteId) {
        Contenu devinette = contenuRepository.findById(devinetteId)
                .orElseThrow(() -> new NotFoundException("Devinette non trouvée"));
        
        if (!"DEVINETTE".equals(devinette.getTypeContenu())) {
            throw new NotFoundException("Ce contenu n'est pas une devinette");
        }
        
        return convertToDevinetteDTO(devinette);
    }

    /**
     * Récupère les devinettes d'un membre spécifique dans une famille.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return Liste des devinettes du membre
     */
    @Transactional(readOnly = true)
    public List<DevinetteDTO> getDevinettesByMembre(Long familleId, Long membreId) {
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        List<Contenu> devinettes = contenuRepository.findByFamilleIdAndAuteurIdAndTypeContenu(
                familleId, membre.getUtilisateur().getId(), "DEVINETTE");
        
        return devinettes.stream()
                .map(this::convertToDevinetteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu (devinette) en DTO.
     */
    private DevinetteDTO convertToDevinetteDTO(Contenu devinette) {
        MembreFamille membreAuteur = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(devinette.getAuteur().getId(), devinette.getFamille().getId())
                .orElse(null);

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "INCONNU";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            lienParenteAuteur = membreAuteur.getLienParente() != null ? 
                    membreAuteur.getLienParente() : "Non spécifié";
        }

        // Extraire la devinette et la réponse de la description
        String[] parts = devinette.getDescription().split("\n");
        String devinetteText = "";
        String reponse = "";
        
        for (String part : parts) {
            if (part.startsWith("Devinette:")) {
                devinetteText = part.substring("Devinette:".length()).trim();
            } else if (part.startsWith("Réponse:")) {
                reponse = part.substring("Réponse:".length()).trim();
            }
        }

        return DevinetteDTO.builder()
                .id(devinette.getId())
                .titre(devinette.getTitre())
                .devinette(devinetteText)
                .reponse(reponse)
                .nomAuteur(devinette.getAuteur().getNom())
                .prenomAuteur(devinette.getAuteur().getPrenom())
                .emailAuteur(devinette.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(devinette.getDateCreation())
                .statut(devinette.getStatut())
                .urlPhoto(devinette.getUrlFichier())
                .lieu(devinette.getLieu())
                .region(devinette.getRegion())
                .idFamille(devinette.getFamille().getId())
                .nomFamille(devinette.getFamille().getNom())
                .build();
    }
}
