package com.heritage.service;

import com.heritage.dto.ArtisanatDTO;
import com.heritage.dto.ConteDTO;
import com.heritage.dto.DevinetteDTO;
import com.heritage.dto.ProverbeDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.MembreFamille;
import com.heritage.repository.ContenuRepository;
import com.heritage.repository.MembreFamilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des contenus publics.
 * Permet de récupérer les contenus publiés (statut PUBLIE) par catégorie,
 * accessible à tous sans authentification.
 * Inclut les contenus créés par le SuperAdmin et les contenus de familles validés.
 */
@Service
public class ContenuPublicService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public ContenuPublicService(ContenuRepository contenuRepository,
                               MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère tous les contes publics.
     * 
     * @return Liste des contes publics
     */
    @Transactional(readOnly = true)
    public List<ConteDTO> getContesPublics() {
        List<Contenu> contes = contenuRepository.findByStatutAndTypeContenu("PUBLIE", "CONTE");
        
        return contes.stream()
                .map(this::convertToConteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les artisanats publics.
     * 
     * @return Liste des artisanats publics
     */
    @Transactional(readOnly = true)
    public List<ArtisanatDTO> getArtisanatsPublics() {
        List<Contenu> artisanats = contenuRepository.findByStatutAndTypeContenu("PUBLIE", "ARTISANAT");
        
        return artisanats.stream()
                .map(this::convertToArtisanatDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les proverbes publics.
     * 
     * @return Liste des proverbes publics
     */
    @Transactional(readOnly = true)
    public List<ProverbeDTO> getProverbesPublics() {
        List<Contenu> proverbes = contenuRepository.findByStatutAndTypeContenu("PUBLIE", "PROVERBE");
        
        return proverbes.stream()
                .map(this::convertToProverbeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les devinettes publiques.
     * 
     * @return Liste des devinettes publiques
     */
    @Transactional(readOnly = true)
    public List<DevinetteDTO> getDevinettesPubliques() {
        List<Contenu> devinettes = contenuRepository.findByStatutAndTypeContenu("PUBLIE", "DEVINETTE");
        
        return devinettes.stream()
                .map(this::convertToDevinetteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu (conte) en DTO.
     */
    private ConteDTO convertToConteDTO(Contenu conte) {
        MembreFamille membreAuteur = null;
        if (conte.getFamille() != null) {
            membreAuteur = membreFamilleRepository
                    .findByUtilisateurIdAndFamilleId(conte.getAuteur().getId(), conte.getFamille().getId())
                    .orElse(null);
        }

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            lienParenteAuteur = membreAuteur.getLienParente() != null ? 
                    membreAuteur.getLienParente() : "Non spécifié";
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
                .urlPhoto(conte.getUrlPhoto())
                .lieu(conte.getLieu())
                .region(conte.getRegion())
                .idFamille(conte.getFamille() != null ? conte.getFamille().getId() : null)
                .nomFamille(conte.getFamille() != null ? conte.getFamille().getNom() : "Non spécifié")
                .quiz(null) // Pas de quiz dans les contenus publics
                .build();
    }

    /**
     * Convertit une entité Contenu (artisanat) en DTO.
     */
    private ArtisanatDTO convertToArtisanatDTO(Contenu artisanat) {
        MembreFamille membreAuteur = null;
        if (artisanat.getFamille() != null) {
            membreAuteur = membreFamilleRepository
                    .findByUtilisateurIdAndFamilleId(artisanat.getAuteur().getId(), artisanat.getFamille().getId())
                    .orElse(null);
        }

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            lienParenteAuteur = membreAuteur.getLienParente() != null ? 
                    membreAuteur.getLienParente() : "Non spécifié";
        }

        // Récupérer les photos et vidéos
        List<String> urlPhotos = new ArrayList<>();
        String urlVideo = null;
        
        // La photo est stockée dans le champ urlPhoto
        if (artisanat.getUrlPhoto() != null && !artisanat.getUrlPhoto().isEmpty()) {
            urlPhotos.add(artisanat.getUrlPhoto());
        }
        
        // La vidéo est stockée dans le champ urlFichier
        if (artisanat.getUrlFichier() != null && !artisanat.getUrlFichier().isEmpty()) {
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
                .idFamille(artisanat.getFamille() != null ? artisanat.getFamille().getId() : null)
                .nomFamille(artisanat.getFamille() != null ? artisanat.getFamille().getNom() : "Non spécifié")
                .build();
    }

    /**
     * Convertit une entité Contenu (proverbe) en DTO.
     */
    private ProverbeDTO convertToProverbeDTO(Contenu proverbe) {
        MembreFamille membreAuteur = null;
        if (proverbe.getFamille() != null) {
            membreAuteur = membreFamilleRepository
                    .findByUtilisateurIdAndFamilleId(proverbe.getAuteur().getId(), proverbe.getFamille().getId())
                    .orElse(null);
        }

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            lienParenteAuteur = membreAuteur.getLienParente() != null ? 
                    membreAuteur.getLienParente() : "Non spécifié";
        }

        // Extraire le proverbe, la signification et l'origine de la description
        String[] parts = proverbe.getDescription().split("\n");
        String proverbeText = "";
        String signification = "";
        String origine = "";
        
        for (String part : parts) {
            if (part.startsWith("Origine:")) {
                origine = part.substring("Origine:".length()).trim();
            } else if (part.startsWith("Signification:")) {
                signification = part.substring("Signification:".length()).trim();
            } else if (part.startsWith("Proverbe:")) {
                proverbeText = part.substring("Proverbe:".length()).trim();
            }
        }

        return ProverbeDTO.builder()
                .id(proverbe.getId())
                .titre(proverbe.getTitre())
                .proverbe(proverbeText)
                .signification(signification)
                .origine(origine)
                .nomAuteur(proverbe.getAuteur().getNom())
                .prenomAuteur(proverbe.getAuteur().getPrenom())
                .emailAuteur(proverbe.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(proverbe.getDateCreation())
                .statut(proverbe.getStatut())
                .urlPhoto(proverbe.getUrlPhoto())
                .lieu(proverbe.getLieu())
                .region(proverbe.getRegion())
                .idFamille(proverbe.getFamille() != null ? proverbe.getFamille().getId() : null)
                .nomFamille(proverbe.getFamille() != null ? proverbe.getFamille().getNom() : "Non spécifié")
                .build();
    }

    /**
     * Convertit une entité Contenu (devinette) en DTO.
     */
    private DevinetteDTO convertToDevinetteDTO(Contenu devinette) {
        MembreFamille membreAuteur = null;
        if (devinette.getFamille() != null) {
            membreAuteur = membreFamilleRepository
                    .findByUtilisateurIdAndFamilleId(devinette.getAuteur().getId(), devinette.getFamille().getId())
                    .orElse(null);
        }

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
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
                .urlPhoto(devinette.getUrlPhoto())
                .lieu(devinette.getLieu())
                .region(devinette.getRegion())
                .idFamille(devinette.getFamille() != null ? devinette.getFamille().getId() : null)
                .nomFamille(devinette.getFamille() != null ? devinette.getFamille().getNom() : "Non spécifié")
                .build();
    }
}

