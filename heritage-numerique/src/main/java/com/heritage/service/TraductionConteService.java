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
 * Utilisateur DjeliaTranslationService pour traduire les contes en français, bambara et anglais.
 */
@Service
public class TraductionConteService {

    // Remplacement de ServiceTraductionMyMemory par DjeliaTranslationService
    private final DjeliaTranslationService djeliaTranslationService;
    private final ContenuRepository contenuRepository;
    private final FileContentService fileContentService;

    // Définition de la langue source par défaut pour ce contexte
    private static final String LANGUE_SOURCE_DEFAUT = "fra_Latn";

    public TraductionConteService(DjeliaTranslationService djeliaTranslationService, // Nouveau service injecté
                                  ContenuRepository contenuRepository,
                                  FileContentService fileContentService) {
        this.djeliaTranslationService = djeliaTranslationService;
        this.contenuRepository = contenuRepository;
        this.fileContentService = fileContentService;
    }

    /**
     * Traduit un conte complet en français, bambara et anglais.
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

        // --- Le contrôle de disponibilité est retiré car DjeliaTranslationService gère le fallback en cas d'erreur API ---

        try {
            // 1. Récupérer le contenu principal du fichier pour la traduction
            String contenuFichier = "";
            if (conte.getUrlFichier() != null && !conte.getUrlFichier().trim().isEmpty()) {
                // Utilise le service de lecture de fichier délégué
                contenuFichier = lireContenuFichier(conte.getUrlFichier());
            }

            // --- Traduction des champs de métadonnées INDIVIDUELS (petits blocs) ---

            // Traduire le titre
            Map<String, String> traductionsTitre = djeliaTranslationService.traduireTout(
                    conte.getTitre(), LANGUE_SOURCE_DEFAUT);

            // Traduire la description
            Map<String, String> traductionsDescription = new HashMap<>();
            if (conte.getDescription() != null && !conte.getDescription().trim().isEmpty()) {
                traductionsDescription = djeliaTranslationService.traduireTout(
                        conte.getDescription(), LANGUE_SOURCE_DEFAUT);
            }

            // Traduire le lieu
            Map<String, String> traductionsLieu = new HashMap<>();
            if (conte.getLieu() != null && !conte.getLieu().trim().isEmpty()) {
                traductionsLieu = djeliaTranslationService.traduireTout(
                        conte.getLieu(), LANGUE_SOURCE_DEFAUT);
            }

            // Traduire la région
            Map<String, String> traductionsRegion = new HashMap<>();
            if (conte.getRegion() != null && !conte.getRegion().trim().isEmpty()) {
                traductionsRegion = djeliaTranslationService.traduireTout(
                        conte.getRegion(), LANGUE_SOURCE_DEFAUT);
            }

            // 2. Traduire le CONTENU PRINCIPAL (texte narratif pur)
            Map<String, String> traductionsContenu = new HashMap<>();
            if (!contenuFichier.trim().isEmpty()) {
                // Traduire SEULEMENT le contenu pur du fichier.
                traductionsContenu = djeliaTranslationService.traduireTout(
                        contenuFichier, LANGUE_SOURCE_DEFAUT);
            }


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
                    .traductionsCompletes(traductionsContenu) // Utilise la traduction du contenu
                    .languesDisponibles(traductionsTitre.keySet())
                    .langueSource(LANGUE_SOURCE_DEFAUT)
                    .statutTraduction("SUCCES")
                    .build();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la traduction du conte: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la traduction du conte: " + e.getMessage(), e);
        }
    }

    /**
     * Lit le contenu d'un fichier uploadé (PDF, TXT, DOC, DOCX) via le service délégué.
     * @param urlFichier URL du fichier à lire
     * @return Contenu du fichier sous forme de texte
     */
    private String lireContenuFichier(String urlFichier) {
        return fileContentService.lireContenuFichier(urlFichier);
    }

    // NOTE: Les méthodes privées lireFichierTexte, lireFichierPDF, lireFichierWord n'ont pas été modifiées
    // car elles n'étaient pas utilisées dans votre logique originale. Elles sont conservées telles quelles.

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

            // Simulation du contenu Word
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
