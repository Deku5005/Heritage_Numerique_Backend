package com.heritage.service;

import com.heritage.dto.ArtisanatDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.ContenuRepository;
import com.heritage.repository.MembreFamilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des artisanats de famille.
 * Fournit des informations détaillées sur les artisanats créés par les membres.
 */
@Service
public class ArtisanatService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public ArtisanatService(ContenuRepository contenuRepository,
                           MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère tous les artisanats d'une famille avec leurs informations détaillées.
     * 
     * @param familleId ID de la famille
     * @return Liste des artisanats avec informations des auteurs
     */
    @Transactional(readOnly = true)
    public List<ArtisanatDTO> getArtisanatsByFamille(Long familleId) {
        List<Contenu> artisanats = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "ARTISANAT");
        
        return artisanats.stream()
                .map(this::convertToArtisanatDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un artisanat spécifique par son ID.
     * 
     * @param artisanatId ID de l'artisanat
     * @return DTO de l'artisanat
     */
    @Transactional(readOnly = true)
    public ArtisanatDTO getArtisanatById(Long artisanatId) {
        Contenu artisanat = contenuRepository.findById(artisanatId)
                .orElseThrow(() -> new NotFoundException("Artisanat non trouvé"));
        
        if (!"ARTISANAT".equals(artisanat.getTypeContenu())) {
            throw new NotFoundException("Ce contenu n'est pas un artisanat");
        }
        
        return convertToArtisanatDTO(artisanat);
    }

    /**
     * Récupère les artisanats d'un membre spécifique dans une famille.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return Liste des artisanats du membre
     */
    @Transactional(readOnly = true)
    public List<ArtisanatDTO> getArtisanatsByMembre(Long familleId, Long membreId) {
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        List<Contenu> artisanats = contenuRepository.findByFamilleIdAndAuteurIdAndTypeContenu(
                familleId, membre.getUtilisateur().getId(), "ARTISANAT");
        
        return artisanats.stream()
                .map(this::convertToArtisanatDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu (artisanat) en DTO.
     */
    private ArtisanatDTO convertToArtisanatDTO(Contenu artisanat) {
        MembreFamille membreAuteur = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(artisanat.getAuteur().getId(), artisanat.getFamille().getId())
                .orElse(null);

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            // Note: Le champ lienParente n'existe pas dans l'entité MembreFamille
            lienParenteAuteur = "Non spécifié";
        }

        // Extraire les URLs des photos de la description
        List<String> urlPhotos = Arrays.asList();
        String urlVideo = null;
        
        if (artisanat.getDescription() != null && artisanat.getDescription().contains("Photos:")) {
            String photosPart = artisanat.getDescription().substring(artisanat.getDescription().indexOf("Photos:") + 7);
            if (photosPart.contains("\n")) {
                photosPart = photosPart.substring(0, photosPart.indexOf("\n"));
            }
            urlPhotos = Arrays.stream(photosPart.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        
        if (artisanat.getUrlFichier() != null && !artisanat.getUrlFichier().startsWith("photos/")) {
            urlVideo = artisanat.getUrlFichier();
        }

        return ArtisanatDTO.builder()
                .id(artisanat.getId())
                .titre(artisanat.getTitre())
                .description(artisanat.getDescription())
                .nomAuteur(artisanat.getAuteur().getNom())
                .prenomAuteur(artisanat.getAuteur().getPrenom())
                .emailAuteur(artisanat.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(artisanat.getDateCreation())
                .statut(artisanat.getStatut())
                .urlPhotos(urlPhotos)
                .urlVideo(urlVideo)
                .lieu(artisanat.getLieu())
                .region(artisanat.getRegion())
                .idFamille(artisanat.getFamille().getId())
                .nomFamille(artisanat.getFamille().getNom())
                .build();
    }
}
