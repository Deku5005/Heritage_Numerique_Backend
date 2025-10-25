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
 * Utilise le même service de traduction que les contes.
 */
@Service
public class TraductionProverbeService {

    private final ServiceTraductionMyMemory serviceTraductionMyMemory;
    private final ContenuRepository contenuRepository;

    public TraductionProverbeService(ServiceTraductionMyMemory serviceTraductionMyMemory,
                                   ContenuRepository contenuRepository) {
        this.serviceTraductionMyMemory = serviceTraductionMyMemory;
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

        // Vérifier la disponibilité du service de traduction
        if (!serviceTraductionMyMemory.estDisponible()) {
            throw new RuntimeException("Service de traduction non disponible");
        }

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
            Map<String, String> traductionsTitre = serviceTraductionMyMemory.traduireTitre(proverbe.getTitre());
            
            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (proverbe.getDescription() != null && !proverbe.getDescription().trim().isEmpty()) {
                traductionsDescription = serviceTraductionMyMemory.traduireContenu(proverbe.getDescription());
            }
            
            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (proverbe.getLieu() != null && !proverbe.getLieu().trim().isEmpty()) {
                traductionsLieu = serviceTraductionMyMemory.traduireContenu(proverbe.getLieu());
            }
            
            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (proverbe.getRegion() != null && !proverbe.getRegion().trim().isEmpty()) {
                traductionsRegion = serviceTraductionMyMemory.traduireContenu(proverbe.getRegion());
            }
            
            // Traduire le contenu complet
            Map<String, String> traductionsContenu = serviceTraductionMyMemory.traduireContenu(contenuComplet.toString());

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
                    .langueSource("fr")
                    .statutTraduction("SUCCES")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la traduction du proverbe: " + e.getMessage(), e);
        }
    }
}