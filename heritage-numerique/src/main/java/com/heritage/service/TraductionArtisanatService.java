package com.heritage.service;

import com.heritage.dto.TraductionConteDTO;
import com.heritage.entite.Contenu;
import com.heritage.repository.ContenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service de traduction pour les artisanats.
 * Utilise le même service de traduction que les contes.
 */
@Service
public class TraductionArtisanatService {

    private final ServiceTraductionMyMemory serviceTraductionMyMemory;
    private final ContenuRepository contenuRepository;

    public TraductionArtisanatService(ServiceTraductionMyMemory serviceTraductionMyMemory,
                                     ContenuRepository contenuRepository) {
        this.serviceTraductionMyMemory = serviceTraductionMyMemory;
        this.contenuRepository = contenuRepository;
    }

    /**
     * Traduit un artisanat complet en français, bambara et anglais.
     * 
     * @param artisanatId ID de l'artisanat à traduire
     * @return DTO avec toutes les traductions
     */
    @Transactional(readOnly = true)
    public TraductionConteDTO traduireArtisanat(Long artisanatId) {
        // Récupérer l'artisanat
        Optional<Contenu> artisanatOpt = contenuRepository.findById(artisanatId);
        if (artisanatOpt.isEmpty()) {
            throw new RuntimeException("Artisanat non trouvé avec l'ID: " + artisanatId);
        }

        Contenu artisanat = artisanatOpt.get();
        
        // Vérifier que c'est bien un artisanat
        if (!"ARTISANAT".equals(artisanat.getTypeContenu())) {
            throw new RuntimeException("Le contenu avec l'ID " + artisanatId + " n'est pas un artisanat");
        }

        // Vérifier la disponibilité du service de traduction
        if (!serviceTraductionMyMemory.estDisponible()) {
            throw new RuntimeException("Service de traduction non disponible");
        }

        try {
            // Construire le contenu complet de l'artisanat
            StringBuilder contenuComplet = new StringBuilder();
            contenuComplet.append(artisanat.getTitre()).append("\n\n");
            
            if (artisanat.getDescription() != null && !artisanat.getDescription().trim().isEmpty()) {
                contenuComplet.append(artisanat.getDescription()).append("\n\n");
            }
            
            // Ajouter les informations de lieu et région
            if (artisanat.getLieu() != null && !artisanat.getLieu().trim().isEmpty()) {
                contenuComplet.append("Lieu: ").append(artisanat.getLieu()).append("\n");
            }
            if (artisanat.getRegion() != null && !artisanat.getRegion().trim().isEmpty()) {
                contenuComplet.append("Région: ").append(artisanat.getRegion()).append("\n");
            }
            
            // Traduire le titre
            Map<String, String> traductionsTitre = serviceTraductionMyMemory.traduireTitre(artisanat.getTitre());
            
            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (artisanat.getDescription() != null && !artisanat.getDescription().trim().isEmpty()) {
                traductionsDescription = serviceTraductionMyMemory.traduireContenu(artisanat.getDescription());
            }
            
            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (artisanat.getLieu() != null && !artisanat.getLieu().trim().isEmpty()) {
                traductionsLieu = serviceTraductionMyMemory.traduireContenu(artisanat.getLieu());
            }
            
            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (artisanat.getRegion() != null && !artisanat.getRegion().trim().isEmpty()) {
                traductionsRegion = serviceTraductionMyMemory.traduireContenu(artisanat.getRegion());
            }
            
            // Traduire le contenu complet
            Map<String, String> traductionsContenu = serviceTraductionMyMemory.traduireContenu(contenuComplet.toString());

            // Construire le DTO de réponse
            return TraductionConteDTO.builder()
                    .idConte(artisanat.getId())
                    .titreOriginal(artisanat.getTitre())
                    .descriptionOriginale(artisanat.getDescription())
                    .lieuOriginal(artisanat.getLieu())
                    .regionOriginale(artisanat.getRegion())
                    .traductionsTitre(traductionsTitre)
                    .traductionsContenu(traductionsContenu)
                    .traductionsDescription(traductionsDescription)
                    .traductionsLieu(traductionsLieu)
                    .traductionsRegion(traductionsRegion)
                    .traductionsCompletes(traductionsDescription)
                    .languesDisponibles(traductionsTitre.keySet())
                    .langueSource("fr")
                    .statutTraduction("SUCCES")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la traduction de l'artisanat: " + e.getMessage(), e);
        }
    }
}