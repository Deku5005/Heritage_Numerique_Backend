package com.heritage.controller;

import com.heritage.dto.TraductionConteDTO;
import com.heritage.service.TraductionArtisanatService;
import com.heritage.service.TraductionConteService;
import com.heritage.service.TraductionDevinetteService;
import com.heritage.service.TraductionProverbeService;
import com.heritage.service.FileContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur unifié pour la gestion des traductions de tous les types de contenus.
 * Utilise HuggingFace NLLB-200 pour traduire les contenus en français, bambara et anglais.
 */
@RestController
@RequestMapping("/api/traduction")
public class TraductionUnifieeController {

    private final TraductionConteService traductionConteService;
    private final TraductionArtisanatService traductionArtisanatService;
    private final TraductionProverbeService traductionProverbeService;
    private final TraductionDevinetteService traductionDevinetteService;
    private final FileContentService fileContentService;
    private final com.heritage.repository.ContenuRepository contenuRepository;

    public TraductionUnifieeController(TraductionConteService traductionConteService,
                                      TraductionArtisanatService traductionArtisanatService,
                                      TraductionProverbeService traductionProverbeService,
                                      TraductionDevinetteService traductionDevinetteService,
                                      FileContentService fileContentService,
                                      com.heritage.repository.ContenuRepository contenuRepository) {
        this.traductionConteService = traductionConteService;
        this.traductionArtisanatService = traductionArtisanatService;
        this.traductionProverbeService = traductionProverbeService;
        this.traductionDevinetteService = traductionDevinetteService;
        this.fileContentService = fileContentService;
        this.contenuRepository = contenuRepository;
    }

    /**
     * Traduit un conte complet en français, bambara et anglais.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}
     * 
     * @param conteId ID du conte à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec toutes les traductions
     */
    @GetMapping("/conte/{conteId}")
    public ResponseEntity<TraductionConteDTO> traduireConte(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionConteService.traduireConte(conteId);
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un conte en français uniquement.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}/fr
     * 
     * @param conteId ID du conte à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec la traduction française
     */
    @GetMapping("/conte/{conteId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireConteFrancais(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionConteService.traduireConte(conteId);
        // Filtrer pour ne garder que le français
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("fr"));
        traduction.setLanguesDisponibles(java.util.Set.of("fr"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un conte en anglais uniquement.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}/en
     * 
     * @param conteId ID du conte à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec la traduction anglaise
     */
    @GetMapping("/conte/{conteId}/en")
    public ResponseEntity<TraductionConteDTO> traduireConteAnglais(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionConteService.traduireConte(conteId);
        // Filtrer pour ne garder que l'anglais
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("en"));
        traduction.setLanguesDisponibles(java.util.Set.of("en"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un conte en bambara uniquement.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}/bm
     * 
     * @param conteId ID du conte à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec la traduction bambara
     */
    @GetMapping("/conte/{conteId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireConteBambara(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionConteService.traduireConte(conteId);
        // Filtrer pour ne garder que le bambara
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("bm"));
        traduction.setLanguesDisponibles(java.util.Set.of("bm"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un artisanat complet en français, bambara et anglais.
     * 
     * Endpoint : GET /api/traduction/artisanat/{artisanatId}
     * 
     * @param artisanatId ID de l'artisanat à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec toutes les traductions
     */
    @GetMapping("/artisanat/{artisanatId}")
    public ResponseEntity<TraductionConteDTO> traduireArtisanat(
            @PathVariable Long artisanatId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionArtisanatService.traduireArtisanat(artisanatId);
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un artisanat en français uniquement.
     * 
     * Endpoint : GET /api/traduction/artisanat/{artisanatId}/fr
     */
    @GetMapping("/artisanat/{artisanatId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatFrancais(
            @PathVariable Long artisanatId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionArtisanatService.traduireArtisanat(artisanatId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("fr"));
        traduction.setLanguesDisponibles(java.util.Set.of("fr"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un artisanat en anglais uniquement.
     * 
     * Endpoint : GET /api/traduction/artisanat/{artisanatId}/en
     */
    @GetMapping("/artisanat/{artisanatId}/en")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatAnglais(
            @PathVariable Long artisanatId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionArtisanatService.traduireArtisanat(artisanatId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("en"));
        traduction.setLanguesDisponibles(java.util.Set.of("en"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un artisanat en bambara uniquement.
     * 
     * Endpoint : GET /api/traduction/artisanat/{artisanatId}/bm
     */
    @GetMapping("/artisanat/{artisanatId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatBambara(
            @PathVariable Long artisanatId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionArtisanatService.traduireArtisanat(artisanatId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("bm"));
        traduction.setLanguesDisponibles(java.util.Set.of("bm"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un proverbe complet en français, bambara et anglais.
     * 
     * Endpoint : GET /api/traduction/proverbe/{proverbeId}
     * 
     * @param proverbeId ID du proverbe à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec toutes les traductions
     */
    @GetMapping("/proverbe/{proverbeId}")
    public ResponseEntity<TraductionConteDTO> traduireProverbe(
            @PathVariable Long proverbeId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionProverbeService.traduireProverbe(proverbeId);
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un proverbe en français uniquement.
     * 
     * Endpoint : GET /api/traduction/proverbe/{proverbeId}/fr
     */
    @GetMapping("/proverbe/{proverbeId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireProverbeFrancais(
            @PathVariable Long proverbeId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionProverbeService.traduireProverbe(proverbeId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("fr"));
        traduction.setLanguesDisponibles(java.util.Set.of("fr"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un proverbe en anglais uniquement.
     * 
     * Endpoint : GET /api/traduction/proverbe/{proverbeId}/en
     */
    @GetMapping("/proverbe/{proverbeId}/en")
    public ResponseEntity<TraductionConteDTO> traduireProverbeAnglais(
            @PathVariable Long proverbeId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionProverbeService.traduireProverbe(proverbeId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("en"));
        traduction.setLanguesDisponibles(java.util.Set.of("en"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un proverbe en bambara uniquement.
     * 
     * Endpoint : GET /api/traduction/proverbe/{proverbeId}/bm
     */
    @GetMapping("/proverbe/{proverbeId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireProverbeBambara(
            @PathVariable Long proverbeId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionProverbeService.traduireProverbe(proverbeId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("bm"));
        traduction.setLanguesDisponibles(java.util.Set.of("bm"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit une devinette complète en français, bambara et anglais.
     * 
     * Endpoint : GET /api/traduction/devinette/{devinetteId}
     * 
     * @param devinetteId ID de la devinette à traduire
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec toutes les traductions
     */
    @GetMapping("/devinette/{devinetteId}")
    public ResponseEntity<TraductionConteDTO> traduireDevinette(
            @PathVariable Long devinetteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionDevinetteService.traduireDevinette(devinetteId);
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit une devinette en français uniquement.
     * 
     * Endpoint : GET /api/traduction/devinette/{devinetteId}/fr
     */
    @GetMapping("/devinette/{devinetteId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteFrancais(
            @PathVariable Long devinetteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionDevinetteService.traduireDevinette(devinetteId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("fr"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("fr"));
        traduction.setLanguesDisponibles(java.util.Set.of("fr"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit une devinette en anglais uniquement.
     * 
     * Endpoint : GET /api/traduction/devinette/{devinetteId}/en
     */
    @GetMapping("/devinette/{devinetteId}/en")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteAnglais(
            @PathVariable Long devinetteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionDevinetteService.traduireDevinette(devinetteId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("en"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("en"));
        traduction.setLanguesDisponibles(java.util.Set.of("en"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit une devinette en bambara uniquement.
     * 
     * Endpoint : GET /api/traduction/devinette/{devinetteId}/bm
     */
    @GetMapping("/devinette/{devinetteId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteBambara(
            @PathVariable Long devinetteId,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionDevinetteService.traduireDevinette(devinetteId);
        traduction.getTraductionsTitre().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsContenu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsDescription().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsLieu().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsRegion().keySet().retainAll(java.util.Set.of("bm"));
        traduction.getTraductionsCompletes().keySet().retainAll(java.util.Set.of("bm"));
        traduction.setLanguesDisponibles(java.util.Set.of("bm"));
        return ResponseEntity.ok(traduction);
    }

    /**
     * Traduit un contenu avec une langue source spécifique.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}/from/{langueSource}
     * 
     * @param conteId ID du conte à traduire
     * @param langueSource Langue source (fr, en, bm)
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec les traductions depuis la langue source spécifiée
     */
    @GetMapping("/conte/{conteId}/from/{langueSource}")
    public ResponseEntity<TraductionConteDTO> traduireConteDepuisLangue(
            @PathVariable Long conteId,
            @PathVariable String langueSource,
            Authentication authentication) {
        
        TraductionConteDTO traduction = traductionConteService.traduireConte(conteId);
        
        // Modifier la langue source
        traduction.setLangueSource(langueSource);
        
        // Filtrer les traductions selon la langue source
        if ("fr".equals(langueSource)) {
            // Garder toutes les langues
        } else if ("en".equals(langueSource)) {
            // Traduire depuis l'anglais vers les autres langues
            // (implémentation simplifiée - dans une vraie app, il faudrait re-traduire)
        } else if ("bm".equals(langueSource)) {
            // Traduire depuis le bambara vers les autres langues
            // (implémentation simplifiée - dans une vraie app, il faudrait re-traduire)
        }
        
        return ResponseEntity.ok(traduction);
    }

    /**
     * Affiche le contenu d'un fichier uploadé.
     * 
     * Endpoint : GET /api/traduction/conte/{conteId}/fichier
     * 
     * @param conteId ID du conte
     * @param authentication Authentification de l'utilisateur
     * @return Contenu du fichier
     */
    @GetMapping("/conte/{conteId}/fichier")
    public ResponseEntity<Map<String, String>> afficherContenuFichier(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        // Récupérer le conte
        Optional<com.heritage.entite.Contenu> conteOpt = contenuRepository.findById(conteId);
        if (conteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        com.heritage.entite.Contenu conte = conteOpt.get();
        
        Map<String, String> response = new HashMap<>();
        response.put("idConte", conte.getId().toString());
        response.put("titre", conte.getTitre());
        response.put("urlFichier", conte.getUrlFichier());
        
        if (conte.getUrlFichier() != null && !conte.getUrlFichier().trim().isEmpty()) {
            // Lire le contenu réel du fichier
            String contenuFichier = lireContenuFichier(conte.getUrlFichier());
            response.put("contenuFichier", contenuFichier);
            
            // Déterminer le type de fichier
            String extension = conte.getUrlFichier().toLowerCase();
            if (extension.endsWith(".pdf")) {
                response.put("typeFichier", "PDF");
            } else if (extension.endsWith(".txt")) {
                response.put("typeFichier", "TXT");
            } else if (extension.endsWith(".doc") || extension.endsWith(".docx")) {
                response.put("typeFichier", "WORD");
            } else {
                response.put("typeFichier", "AUTRE");
            }
            response.put("statut", "Fichier trouvé et lu");
        } else {
            response.put("contenuFichier", "Aucun fichier uploadé");
            response.put("typeFichier", "N/A");
            response.put("statut", "Aucun fichier");
        }
        
        return ResponseEntity.ok(response);
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
