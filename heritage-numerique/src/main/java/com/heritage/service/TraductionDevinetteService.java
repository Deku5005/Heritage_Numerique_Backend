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
 * Service de traduction pour les devinettes.
 * Utilise DjeliaTranslationService pour traduire les devinettes en français, bambara et anglais.
 */
@Service
public class TraductionDevinetteService {

    private final DjeliaTranslationService djeliaTranslationService;
    private final ContenuRepository contenuRepository;

    // Définition de la langue source par défaut pour ce contexte
    private static final String LANGUE_SOURCE_DEFAUT = "fra_Latn";

    public TraductionDevinetteService(DjeliaTranslationService djeliaTranslationService,
                                    ContenuRepository contenuRepository) {
        this.djeliaTranslationService = djeliaTranslationService;
        this.contenuRepository = contenuRepository;
    }

    /**
     * Traduit une devinette complète en français, bambara et anglais.
     * 
     * @param devinetteId ID de la devinette à traduire
     * @return DTO avec toutes les traductions
     */
    @Transactional(readOnly = true)
    public TraductionConteDTO traduireDevinette(Long devinetteId) {
        // Récupérer la devinette
        Optional<Contenu> devinetteOpt = contenuRepository.findById(devinetteId);
        if (devinetteOpt.isEmpty()) {
            throw new RuntimeException("Devinette non trouvée avec l'ID: " + devinetteId);
        }

        Contenu devinette = devinetteOpt.get();
        
        // Vérifier que c'est bien une devinette
        if (!"DEVINETTE".equals(devinette.getTypeContenu())) {
            throw new RuntimeException("Le contenu avec l'ID " + devinetteId + " n'est pas une devinette");
        }

        // --- Le contrôle de disponibilité est retiré car DjeliaTranslationService gère le fallback en cas d'erreur API ---

        try {
            // Construire le contenu complet de la devinette
            StringBuilder contenuComplet = new StringBuilder();
            contenuComplet.append(devinette.getTitre()).append("\n\n");
            
            if (devinette.getDescription() != null && !devinette.getDescription().trim().isEmpty()) {
                contenuComplet.append(devinette.getDescription()).append("\n\n");
            }
            
            // Ajouter les informations de lieu et région
            if (devinette.getLieu() != null && !devinette.getLieu().trim().isEmpty()) {
                contenuComplet.append("Lieu: ").append(devinette.getLieu()).append("\n");
            }
            if (devinette.getRegion() != null && !devinette.getRegion().trim().isEmpty()) {
                contenuComplet.append("Région: ").append(devinette.getRegion()).append("\n");
            }
            
            // Traduire le titre
            Map<String, String> traductionsTitre = djeliaTranslationService.traduireTout(
                    devinette.getTitre(), LANGUE_SOURCE_DEFAUT);
            
            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (devinette.getDescription() != null && !devinette.getDescription().trim().isEmpty()) {
                traductionsDescription = djeliaTranslationService.traduireTout(
                        devinette.getDescription(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (devinette.getLieu() != null && !devinette.getLieu().trim().isEmpty()) {
                traductionsLieu = djeliaTranslationService.traduireTout(
                        devinette.getLieu(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (devinette.getRegion() != null && !devinette.getRegion().trim().isEmpty()) {
                traductionsRegion = djeliaTranslationService.traduireTout(
                        devinette.getRegion(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire le contenu complet
            Map<String, String> traductionsContenu = djeliaTranslationService.traduireTout(
                    contenuComplet.toString(), LANGUE_SOURCE_DEFAUT);

            // Construire le DTO de réponse
            return TraductionConteDTO.builder()
                    .idConte(devinette.getId())
                    .titreOriginal(devinette.getTitre())
                    .descriptionOriginale(devinette.getDescription())
                    .lieuOriginal(devinette.getLieu())
                    .regionOriginale(devinette.getRegion())
                    .traductionsTitre(traductionsTitre)
                    .traductionsContenu(traductionsContenu)
                    .traductionsDescription(traductionsDescription)
                    .traductionsLieu(traductionsLieu)
                    .traductionsRegion(traductionsRegion)
                    .traductionsCompletes(traductionsDescription)
                    .languesDisponibles(traductionsTitre.keySet())
                    .langueSource(LANGUE_SOURCE_DEFAUT)
                    .statutTraduction("SUCCES")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la traduction de la devinette: " + e.getMessage(), e);
        }
    }
}