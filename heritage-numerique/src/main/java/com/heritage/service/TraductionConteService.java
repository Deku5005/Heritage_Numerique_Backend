package com.heritage.service;

import com.heritage.dto.TraductionConteDTO;
import com.heritage.entite.Contenu;
import com.heritage.repository.ContenuRepository;
import com.heritage.service.FileContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service de traduction pour les contes.
 * Utilise HuggingFace NLLB-200 pour traduire les contes en français, bambara et anglais.
 */
@Service
public class TraductionConteService {

    private final ServiceTraductionMyMemory serviceTraductionMyMemory;
    private final ContenuRepository contenuRepository;
    private final FileContentService fileContentService;

    public TraductionConteService(ServiceTraductionMyMemory serviceTraductionMyMemory,
                                ContenuRepository contenuRepository,
                                FileContentService fileContentService) {
        this.serviceTraductionMyMemory = serviceTraductionMyMemory;
        this.contenuRepository = contenuRepository;
        this.fileContentService = fileContentService;
    }

    /**
     * Traduit un conte complet en français, bambara et anglais.
     * 
     * @param conteId ID du conte à traduire
     * @return DTO avec toutes les traductions
     */
    @Transactional(readOnly = true)
    public TraductionConteDTO traduireConte(Long conteId) {
        // Récupérer le conte
        Optional<Contenu> conteOpt = contenuRepository.findById(conteId);
        if (conteOpt.isEmpty()) {
            throw new RuntimeException("Conte non trouvé avec l'ID: " + conteId);
        }

        Contenu conte = conteOpt.get();
        
        // Vérifier que c'est bien un conte
        if (!"CONTE".equals(conte.getTypeContenu())) {
            throw new RuntimeException("Le contenu avec l'ID " + conteId + " n'est pas un conte");
        }

        // Vérifier la disponibilité du service de traduction
        if (!serviceTraductionMyMemory.estDisponible()) {
            throw new RuntimeException("Service de traduction non disponible");
        }

        try {
            // Construire le contenu complet du conte
            StringBuilder contenuComplet = new StringBuilder();
            contenuComplet.append(conte.getTitre()).append("\n\n");
            
            if (conte.getDescription() != null && !conte.getDescription().trim().isEmpty()) {
                contenuComplet.append(conte.getDescription()).append("\n\n");
            }
            
            // Ajouter les informations de lieu et région
            if (conte.getLieu() != null && !conte.getLieu().trim().isEmpty()) {
                contenuComplet.append("Lieu: ").append(conte.getLieu()).append("\n");
            }
            if (conte.getRegion() != null && !conte.getRegion().trim().isEmpty()) {
                contenuComplet.append("Région: ").append(conte.getRegion()).append("\n");
            }
            
            // Ajouter le contenu du fichier uploadé si disponible
            if (conte.getUrlFichier() != null && !conte.getUrlFichier().trim().isEmpty()) {
                String contenuFichier = lireContenuFichier(conte.getUrlFichier());
                if (contenuFichier != null && !contenuFichier.trim().isEmpty()) {
                    contenuComplet.append("\n\nContenu du fichier:\n").append(contenuFichier);
                }
            }
            
            // Traduire le titre
            Map<String, String> traductionsTitre = serviceTraductionMyMemory.traduireTitre(conte.getTitre());
            
            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (conte.getDescription() != null && !conte.getDescription().trim().isEmpty()) {
                traductionsDescription = serviceTraductionMyMemory.traduireContenu(conte.getDescription());
            }
            
            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (conte.getLieu() != null && !conte.getLieu().trim().isEmpty()) {
                traductionsLieu = serviceTraductionMyMemory.traduireContenu(conte.getLieu());
            }
            
            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (conte.getRegion() != null && !conte.getRegion().trim().isEmpty()) {
                traductionsRegion = serviceTraductionMyMemory.traduireContenu(conte.getRegion());
            }
            
            // Traduire le contenu complet
            Map<String, String> traductionsContenu = serviceTraductionMyMemory.traduireContenu(contenuComplet.toString());

            // Construire le DTO de réponse
            return TraductionConteDTO.builder()
                    .idConte(conte.getId())
                    .titreOriginal(conte.getTitre())
                    .descriptionOriginale(conte.getDescription())
                    .lieuOriginal(conte.getLieu())
                    .regionOriginale(conte.getRegion())
                    .traductionsTitre(traductionsTitre)
                    .traductionsContenu(traductionsContenu)
                    .traductionsDescription(traductionsDescription)
                    .traductionsLieu(traductionsLieu)
                    .traductionsRegion(traductionsRegion)
                    .traductionsCompletes(traductionsDescription) // Ajouter les traductions de description
                    .languesDisponibles(traductionsTitre.keySet())
                    .langueSource("fr")
                    .statutTraduction("SUCCES")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la traduction du conte: " + e.getMessage(), e);
        }
    }

    /**
     * Lit le contenu d'un fichier uploadé (PDF, TXT, DOC, DOCX).
     * 
     * @param urlFichier URL du fichier à lire
     * @return Contenu du fichier sous forme de texte
     */
    private String lireContenuFichier(String urlFichier) {
        return fileContentService.lireContenuFichier(urlFichier);
    }

    /**
     * Lit un fichier texte simple.
     */
    private String lireFichierTexte(String urlFichier) {
        try {
            // Construire le chemin complet du fichier
            String cheminComplet = System.getProperty("user.dir") + urlFichier;
            java.io.File fichier = new java.io.File(cheminComplet);
            
            if (!fichier.exists()) {
                System.err.println("❌ Fichier non trouvé: " + cheminComplet);
                return "Fichier texte non trouvé: " + urlFichier;
            }
            
            // Lire le contenu du fichier texte
            StringBuilder contenu = new StringBuilder();
            contenu.append("=== CONTENU DU FICHIER TEXTE ===\n");
            contenu.append("Fichier: ").append(fichier.getName()).append("\n");
            contenu.append("Taille: ").append(fichier.length()).append(" bytes\n\n");
            
            // Lire le contenu réel du fichier
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fichier))) {
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    contenu.append(ligne).append("\n");
                }
            }
            
            return contenu.toString();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture du fichier texte: " + e.getMessage());
            return "Erreur lors de la lecture du fichier texte: " + e.getMessage();
        }
    }

    /**
     * Lit un fichier PDF.
     */
    private String lireFichierPDF(String urlFichier) {
        try {
            // Construire le chemin complet du fichier
            String cheminComplet = System.getProperty("user.dir") + urlFichier;
            java.io.File fichier = new java.io.File(cheminComplet);
            
            if (!fichier.exists()) {
                System.err.println("❌ Fichier non trouvé: " + cheminComplet);
                return "Fichier PDF non trouvé: " + urlFichier;
            }
            
            // Lire le contenu du fichier PDF (simulation pour l'instant)
            // Dans une vraie implémentation, utiliser Apache Tika ou PDFBox
            StringBuilder contenu = new StringBuilder();
            contenu.append("=== CONTENU DU FICHIER PDF ===\n");
            contenu.append("Fichier: ").append(fichier.getName()).append("\n");
            contenu.append("Taille: ").append(fichier.length()).append(" bytes\n");
            contenu.append("Chemin: ").append(cheminComplet).append("\n\n");
            
            // Simulation du contenu PDF
            contenu.append("L'épopée de Soundjata Keïta\n");
            contenu.append("Fondateur de l'Empire du Mali\n\n");
            contenu.append("Soundjata Keïta était un prince du royaume du Manding...\n");
            contenu.append("Il a unifié les royaumes mandingues et créé l'Empire du Mali...\n");
            contenu.append("Sa mère était Sogolon Kedjou, une princesse du royaume de Do...\n");
            contenu.append("Il a vaincu Soumaoro Kanté à la bataille de Kirina...\n");
            contenu.append("Soundjata est devenu le premier empereur du Mali...\n");
            
            return contenu.toString();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture du fichier PDF: " + e.getMessage());
            return "Erreur lors de la lecture du fichier PDF: " + e.getMessage();
        }
    }

    /**
     * Lit un fichier Word (DOC/DOCX).
     */
    private String lireFichierWord(String urlFichier) {
        try {
            // Construire le chemin complet du fichier
            String cheminComplet = System.getProperty("user.dir") + urlFichier;
            java.io.File fichier = new java.io.File(cheminComplet);
            
            if (!fichier.exists()) {
                System.err.println("❌ Fichier non trouvé: " + cheminComplet);
                return "Fichier Word non trouvé: " + urlFichier;
            }
            
            // Simulation du contenu Word (dans une vraie implémentation, utiliser Apache POI)
            StringBuilder contenu = new StringBuilder();
            contenu.append("=== CONTENU DU FICHIER WORD ===\n");
            contenu.append("Fichier: ").append(fichier.getName()).append("\n");
            contenu.append("Taille: ").append(fichier.length()).append(" bytes\n");
            contenu.append("Chemin: ").append(cheminComplet).append("\n\n");
            
            // Simulation du contenu Word
            contenu.append("Document Word - Conte traditionnel\n");
            contenu.append("L'histoire de Soundjata Keïta\n\n");
            contenu.append("Chapitre 1: La naissance de Soundjata\n");
            contenu.append("Soundjata est né dans le royaume du Manding...\n\n");
            contenu.append("Chapitre 2: L'exil\n");
            contenu.append("Après la mort de son père, Soundjata a dû s'exiler...\n\n");
            contenu.append("Chapitre 3: Le retour et la victoire\n");
            contenu.append("Soundjata est revenu pour réclamer son trône...\n");
            
            return contenu.toString();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la lecture du fichier Word: " + e.getMessage());
            return "Erreur lors de la lecture du fichier Word: " + e.getMessage();
        }
    }
}
