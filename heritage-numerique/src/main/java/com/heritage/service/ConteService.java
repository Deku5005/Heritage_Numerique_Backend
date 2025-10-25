package com.heritage.service;

import com.heritage.dto.ConteDTO;
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
 * Service pour la gestion des contes de famille.
 * Fournit des informations détaillées sur les contes créés par les membres.
 */
@Service
public class ConteService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public ConteService(ContenuRepository contenuRepository,
                       MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère tous les contes d'une famille avec leurs informations détaillées.
     * 
     * @param familleId ID de la famille
     * @return Liste des contes avec informations des auteurs
     */
    @Transactional(readOnly = true)
    public List<ConteDTO> getContesByFamille(Long familleId) {
        // 1. Récupérer tous les contes de la famille
        List<Contenu> contes = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "CONTE");
        
        // 2. Convertir en DTO avec informations détaillées
        return contes.stream()
                .map(this::convertToConteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un conte spécifique par son ID.
     * 
     * @param conteId ID du conte
     * @return DTO du conte
     */
    @Transactional(readOnly = true)
    public ConteDTO getConteById(Long conteId) {
        Contenu conte = contenuRepository.findById(conteId)
                .orElseThrow(() -> new NotFoundException("Conte non trouvé"));
        
        if (!"CONTE".equals(conte.getTypeContenu())) {
            throw new NotFoundException("Ce contenu n'est pas un conte");
        }
        
        return convertToConteDTO(conte);
    }

    /**
     * Récupère les contes d'un membre spécifique dans une famille.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return Liste des contes du membre
     */
    @Transactional(readOnly = true)
    public List<ConteDTO> getContesByMembre(Long familleId, Long membreId) {
        // Vérifier que le membre existe et appartient à la famille
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        // Récupérer les contes du membre
        List<Contenu> contes = contenuRepository.findByFamilleIdAndAuteurIdAndTypeContenu(
                familleId, membre.getUtilisateur().getId(), "CONTE");
        
        return contes.stream()
                .map(this::convertToConteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu (conte) en DTO.
     * 
     * @param conte Entité conte à convertir
     * @return DTO du conte
     */
    private ConteDTO convertToConteDTO(Contenu conte) {
        // Récupérer les informations du membre auteur
        MembreFamille membreAuteur = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(conte.getAuteur().getId(), conte.getFamille().getId())
                .orElse(null);

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            // Note: Le champ lienParente n'existe pas dans l'entité MembreFamille
            lienParenteAuteur = "Non spécifié";
        }

        return ConteDTO.builder()
                .id(conte.getId())
                .titre(conte.getTitre())
                .description(conte.getDescription())
                .nomAuteur(conte.getAuteur().getNom())
                .prenomAuteur(conte.getAuteur().getPrenom())
                .emailAuteur(conte.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(conte.getDateCreation())
                .statut(conte.getStatut())
                .urlFichier(conte.getUrlFichier())
                .urlPhoto(conte.getUrlFichier()) // Pour les contes, urlFichier contient la photo
                .lieu(conte.getLieu())
                .region(conte.getRegion())
                .idFamille(conte.getFamille().getId())
                .nomFamille(conte.getFamille().getNom())
                .build();
    }
}
