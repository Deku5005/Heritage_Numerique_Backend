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
 * Service de traduction pour les proverbes.
 * Utilise DjeliaTranslationService pour traduire les proverbes en français, bambara et anglais.
 */
@Service
public class TraductionProverbeService {

    private final DjeliaTranslationService djeliaTranslationService;
    private final ContenuRepository contenuRepository;

    // Définition de la langue source par défaut pour ce contexte
    private static final String LANGUE_SOURCE_DEFAUT = "fra_Latn";

    public TraductionProverbeService(DjeliaTranslationService djeliaTranslationService,
                                   ContenuRepository contenuRepository) {
        this.djeliaTranslationService = djeliaTranslationService;
        this.contenuRepository = contenuRepository;
    }

    /**
     * Traduit un proverbe complet en français, bambara et anglais.
     * 
     * @param proverbeId ID du proverbe à traduire
     * @return DTO avec toutes les traductions
     */
    @Transactional(readOnly = true)
    public TraductionConteDTO traduireProverbe(Long proverbeId) {
        // Récupérer le proverbe
        Optional<Contenu> proverbeOpt = contenuRepository.findById(proverbeId);
        if (proverbeOpt.isEmpty()) {
            throw new RuntimeException("Proverbe non trouvé avec l'ID: " + proverbeId);
        }

        Contenu proverbe = proverbeOpt.get();
        
        // Vérifier que c'est bien un proverbe
        if (!"PROVERBE".equals(proverbe.getTypeContenu())) {
            throw new RuntimeException("Le contenu avec l'ID " + proverbeId + " n'est pas un proverbe");
        }

        // --- Le contrôle de disponibilité est retiré car DjeliaTranslationService gère le fallback en cas d'erreur API ---

        try {
            // Construire le contenu complet du proverbe
            StringBuilder contenuComplet = new StringBuilder();
            contenuComplet.append(proverbe.getTitre()).append("\n\n");
            
            if (proverbe.getDescription() != null && !proverbe.getDescription().trim().isEmpty()) {
                contenuComplet.append(proverbe.getDescription()).append("\n\n");
            }
            
            // Ajouter les informations de lieu et région
            if (proverbe.getLieu() != null && !proverbe.getLieu().trim().isEmpty()) {
                contenuComplet.append("Lieu: ").append(proverbe.getLieu()).append("\n");
            }
            if (proverbe.getRegion() != null && !proverbe.getRegion().trim().isEmpty()) {
                contenuComplet.append("Région: ").append(proverbe.getRegion()).append("\n");
            }
            
            // Traduire le titre
            Map<String, String> traductionsTitre = djeliaTranslationService.traduireTout(
                    proverbe.getTitre(), LANGUE_SOURCE_DEFAUT);
            
            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (proverbe.getDescription() != null && !proverbe.getDescription().trim().isEmpty()) {
                traductionsDescription = djeliaTranslationService.traduireTout(
                        proverbe.getDescription(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (proverbe.getLieu() != null && !proverbe.getLieu().trim().isEmpty()) {
                traductionsLieu = djeliaTranslationService.traduireTout(
                        proverbe.getLieu(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (proverbe.getRegion() != null && !proverbe.getRegion().trim().isEmpty()) {
                traductionsRegion = djeliaTranslationService.traduireTout(
                        proverbe.getRegion(), LANGUE_SOURCE_DEFAUT);
            }
            
            // Traduire le contenu complet
            Map<String, String> traductionsContenu = djeliaTranslationService.traduireTout(
                    contenuComplet.toString(), LANGUE_SOURCE_DEFAUT);

            // Construire le DTO de réponse
            return TraductionConteDTO.builder()
                    .idConte(proverbe.getId())
                    .titreOriginal(proverbe.getTitre())
                    .descriptionOriginale(proverbe.getDescription())
                    .lieuOriginal(proverbe.getLieu())
                    .regionOriginale(proverbe.getRegion())
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
            throw new RuntimeException("Erreur lors de la traduction du proverbe: " + e.getMessage(), e);
        }
    }
}