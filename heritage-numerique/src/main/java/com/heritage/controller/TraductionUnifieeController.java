package com.heritage.controller;

import com.heritage.dto.TraductionConteDTO;
import com.heritage.service.TraductionArtisanatService;
import com.heritage.service.TraductionConteService;
import com.heritage.service.TraductionDevinetteService;
import com.heritage.service.TraductionProverbeService;
import com.heritage.service.FileContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contrôleur unifié pour la gestion des traductions de tous les types de contenus.
 * Utilise Djelia pour traduire les contenus en français, bambara et anglais.
 */
@RestController
@RequestMapping("/api/traduction")
@CrossOrigin(origins = "*")
@Tag(name = "🌐 Traduction", description = "Endpoints pour traduire les contenus (contes, artisanats, proverbes, devinettes) en français, bambara et anglais via l'API Djelia")
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

    // --- Méthode Générique pour gérer la traduction et les erreurs ---

    private ResponseEntity<TraductionConteDTO> handleTranslationRequest(
            Long id,
            String typeContenu, // Utilisé pour le logging/erreurs (e.g., "conte", "artisanat")
            String targetLang, // Langue spécifique, ou null pour toutes
            java.util.function.Function<Long, TraductionConteDTO> serviceCall) {

        System.out.println("⏳ Requête de traduction reçue pour " + typeContenu + " ID: " + id + (targetLang != null ? " en " + targetLang : " (toutes langues)"));

        try {
            TraductionConteDTO traduction = serviceCall.apply(id);

            if (targetLang != null) {
                traduction = filterTranslationByLanguage(traduction, targetLang);
            }

            System.out.println("✅ Traduction complétée avec succès pour " + typeContenu + " ID: " + id);
            return ResponseEntity.ok(traduction);

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur de traitement de la traduction (" + typeContenu + " ID: " + id + "): " + e.getMessage());

            // Gérer spécifiquement les erreurs "non trouvé" (404)
            if (e.getMessage().contains("Conte non trouvé") || e.getMessage().contains("n'est pas un conte")) {
                return new ResponseEntity(
                        Map.of("erreur", e.getMessage()),
                        HttpStatus.NOT_FOUND
                );
            }

            // Pour toutes les autres erreurs (API Djelia, lecture fichier), retourne 500 Internal Server Error
            return new ResponseEntity(
                    Map.of("erreur", "Erreur interne lors de la traduction du " + typeContenu + ".", "detail", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Filtre les traductions pour ne conserver que la langue cible spécifiée.
     * Mappe les codes de langue courts (fr, en, bm) vers les codes Djelia (fra_Latn, en, bam_Latn).
     */
    private TraductionConteDTO filterTranslationByLanguage(TraductionConteDTO traduction, String targetLang) {
        // Mapper les codes de langue courts vers les codes Djelia
        String djeliaLangCode = mapToDjeliaLanguageCode(targetLang);
        Set<String> langSet = Set.of(djeliaLangCode);

        // Cette méthode modifie l'objet DTO en place
        traduction.getTraductionsTitre().keySet().retainAll(langSet);
        traduction.getTraductionsContenu().keySet().retainAll(langSet);
        traduction.getTraductionsDescription().keySet().retainAll(langSet);
        traduction.getTraductionsLieu().keySet().retainAll(langSet);
        traduction.getTraductionsRegion().keySet().retainAll(langSet);
        traduction.getTraductionsCompletes().keySet().retainAll(langSet);
        traduction.setLanguesDisponibles(langSet);

        return traduction;
    }

    /**
     * Mappe les codes de langue courts (fr, en, bm) vers les codes Djelia (fra_Latn, eng_Latn, bam_Latn).
     * 
     * @param langCode Code de langue court
     * @return Code de langue Djelia correspondant
     */
    private String mapToDjeliaLanguageCode(String langCode) {
        if (langCode == null) {
            return null;
        }
        switch (langCode.toLowerCase()) {
            case "fr":
                return "fra_Latn";
            case "bm":
                return "bam_Latn";
            case "en":
                return "eng_Latn"; // Djelia utilise "eng_Latn" pour l'anglais
            default:
                // Si c'est déjà un code Djelia ou inconnu, retourner tel quel
                return langCode;
        }
    }

    // -------------------------------------------------------------------------
    // --- CONTE Endpoints ---
    // -------------------------------------------------------------------------

    /**
     * Traduit un conte complet en français, bambara et anglais.
     * Endpoint : GET /api/traduction/conte/{conteId}
     */
    @Operation(
        summary = "Traduire un conte dans toutes les langues",
        description = "Traduit un conte complet (titre, description, contenu, lieu, région) en français, bambara et anglais via l'API Djelia"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Traduction réussie",
                content = @Content(schema = @Schema(implementation = TraductionConteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Conte non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la traduction")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/conte/{conteId}")
    public ResponseEntity<TraductionConteDTO> traduireConte(
            @Parameter(description = "ID du conte à traduire", required = true, example = "1")
            @PathVariable Long conteId,
            Authentication authentication) {

        return handleTranslationRequest(conteId, "conte", null, traductionConteService::traduireConte);
    }

    @Operation(
        summary = "Traduire un conte en français",
        description = "Traduit un conte uniquement en français (retourne seulement la traduction française)"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/conte/{conteId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireConteFrancais(
            @Parameter(description = "ID du conte à traduire", required = true, example = "1")
            @PathVariable Long conteId,
            Authentication authentication) {

        return handleTranslationRequest(conteId, "conte", "fr", traductionConteService::traduireConte);
    }

    @Operation(
        summary = "Traduire un conte en anglais",
        description = "Traduit un conte uniquement en anglais (retourne seulement la traduction anglaise)"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/conte/{conteId}/en")
    public ResponseEntity<TraductionConteDTO> traduireConteAnglais(
            @Parameter(description = "ID du conte à traduire", required = true, example = "1")
            @PathVariable Long conteId,
            Authentication authentication) {

        return handleTranslationRequest(conteId, "conte", "en", traductionConteService::traduireConte);
    }

    @Operation(
        summary = "Traduire un conte en bambara",
        description = "Traduit un conte uniquement en bambara (retourne seulement la traduction bambara)"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/conte/{conteId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireConteBambara(
            @Parameter(description = "ID du conte à traduire", required = true, example = "1")
            @PathVariable Long conteId,
            Authentication authentication) {

        return handleTranslationRequest(conteId, "conte", "bm", traductionConteService::traduireConte);
    }

    // -------------------------------------------------------------------------
    // --- ARTISANAT Endpoints ---
    // -------------------------------------------------------------------------

    /**
     * Traduit un artisanat complet en français, bambara et anglais.
     * Endpoint : GET /api/traduction/artisanat/{artisanatId}
     */
    @Operation(
        summary = "Traduire un artisanat dans toutes les langues",
        description = "Traduit un artisanat complet en français, bambara et anglais via l'API Djelia"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/artisanat/{artisanatId}")
    public ResponseEntity<TraductionConteDTO> traduireArtisanat(
            @Parameter(description = "ID de l'artisanat à traduire", required = true, example = "1")
            @PathVariable Long artisanatId,
            Authentication authentication) {

        return handleTranslationRequest(artisanatId, "artisanat", null, traductionArtisanatService::traduireArtisanat);
    }

    @Operation(summary = "Traduire un artisanat en français")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/artisanat/{artisanatId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatFrancais(
            @Parameter(description = "ID de l'artisanat à traduire", required = true, example = "1")
            @PathVariable Long artisanatId,
            Authentication authentication) {

        return handleTranslationRequest(artisanatId, "artisanat", "fr", traductionArtisanatService::traduireArtisanat);
    }

    @Operation(summary = "Traduire un artisanat en anglais")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/artisanat/{artisanatId}/en")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatAnglais(
            @Parameter(description = "ID de l'artisanat à traduire", required = true, example = "1")
            @PathVariable Long artisanatId,
            Authentication authentication) {

        return handleTranslationRequest(artisanatId, "artisanat", "en", traductionArtisanatService::traduireArtisanat);
    }

    @Operation(summary = "Traduire un artisanat en bambara")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/artisanat/{artisanatId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatBambara(
            @Parameter(description = "ID de l'artisanat à traduire", required = true, example = "1")
            @PathVariable Long artisanatId,
            Authentication authentication) {

        return handleTranslationRequest(artisanatId, "artisanat", "bm", traductionArtisanatService::traduireArtisanat);
    }

    // -------------------------------------------------------------------------
    // --- PROVERBE Endpoints ---
    // -------------------------------------------------------------------------

    /**
     * Traduit un proverbe complet en français, bambara et anglais.
     * Endpoint : GET /api/traduction/proverbe/{proverbeId}
     */
    @Operation(
        summary = "Traduire un proverbe dans toutes les langues",
        description = "Traduit un proverbe complet en français, bambara et anglais via l'API Djelia"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/proverbe/{proverbeId}")
    public ResponseEntity<TraductionConteDTO> traduireProverbe(
            @Parameter(description = "ID du proverbe à traduire", required = true, example = "1")
            @PathVariable Long proverbeId,
            Authentication authentication) {

        return handleTranslationRequest(proverbeId, "proverbe", null, traductionProverbeService::traduireProverbe);
    }

    @Operation(summary = "Traduire un proverbe en français")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/proverbe/{proverbeId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireProverbeFrancais(
            @Parameter(description = "ID du proverbe à traduire", required = true, example = "1")
            @PathVariable Long proverbeId,
            Authentication authentication) {

        return handleTranslationRequest(proverbeId, "proverbe", "fr", traductionProverbeService::traduireProverbe);
    }

    @Operation(summary = "Traduire un proverbe en anglais")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/proverbe/{proverbeId}/en")
    public ResponseEntity<TraductionConteDTO> traduireProverbeAnglais(
            @Parameter(description = "ID du proverbe à traduire", required = true, example = "1")
            @PathVariable Long proverbeId,
            Authentication authentication) {

        return handleTranslationRequest(proverbeId, "proverbe", "en", traductionProverbeService::traduireProverbe);
    }

    @Operation(summary = "Traduire un proverbe en bambara")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/proverbe/{proverbeId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireProverbeBambara(
            @Parameter(description = "ID du proverbe à traduire", required = true, example = "1")
            @PathVariable Long proverbeId,
            Authentication authentication) {

        return handleTranslationRequest(proverbeId, "proverbe", "bm", traductionProverbeService::traduireProverbe);
    }

    // -------------------------------------------------------------------------
    // --- DEVINETTE Endpoints ---
    // -------------------------------------------------------------------------

    /**
     * Traduit une devinette complète en français, bambara et anglais.
     * Endpoint : GET /api/traduction/devinette/{devinetteId}
     */
    @Operation(
        summary = "Traduire une devinette dans toutes les langues",
        description = "Traduit une devinette complète en français, bambara et anglais via l'API Djelia"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/devinette/{devinetteId}")
    public ResponseEntity<TraductionConteDTO> traduireDevinette(
            @Parameter(description = "ID de la devinette à traduire", required = true, example = "1")
            @PathVariable Long devinetteId,
            Authentication authentication) {

        return handleTranslationRequest(devinetteId, "devinette", null, traductionDevinetteService::traduireDevinette);
    }

    @Operation(summary = "Traduire une devinette en français")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/devinette/{devinetteId}/fr")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteFrancais(
            @Parameter(description = "ID de la devinette à traduire", required = true, example = "1")
            @PathVariable Long devinetteId,
            Authentication authentication) {

        return handleTranslationRequest(devinetteId, "devinette", "fr", traductionDevinetteService::traduireDevinette);
    }

    @Operation(summary = "Traduire une devinette en anglais")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/devinette/{devinetteId}/en")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteAnglais(
            @Parameter(description = "ID de la devinette à traduire", required = true, example = "1")
            @PathVariable Long devinetteId,
            Authentication authentication) {

        return handleTranslationRequest(devinetteId, "devinette", "en", traductionDevinetteService::traduireDevinette);
    }

    @Operation(summary = "Traduire une devinette en bambara")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/devinette/{devinetteId}/bm")
    public ResponseEntity<TraductionConteDTO> traduireDevinetteBambara(
            @Parameter(description = "ID de la devinette à traduire", required = true, example = "1")
            @PathVariable Long devinetteId,
            Authentication authentication) {

        return handleTranslationRequest(devinetteId, "devinette", "bm", traductionDevinetteService::traduireDevinette);
    }

    // -------------------------------------------------------------------------
    // --- Autres Endpoints ---
    // -------------------------------------------------------------------------

    /**
     * Traduit un contenu avec une langue source spécifique.
     * * NOTE: La méthode de service `traduireConte` actuelle utilise par défaut "fr" comme source.
     * Pour supporter dynamiquement une autre source, il faudrait une méthode de service
     * `traduireConte(Long conteId, String sourceLang)` que nous n'avons pas implémentée.
     * Cet endpoint reste donc inchangé et renvoie une réponse basée sur la traduction
     * par défaut (source FR).
     * * Endpoint : GET /api/traduction/conte/{conteId}/from/{langueSource}
     */
    @GetMapping("/conte/{conteId}/from/{langueSource}")
    public ResponseEntity<TraductionConteDTO> traduireConteDepuisLangue(
            @PathVariable Long conteId,
            @PathVariable String langueSource,
            Authentication authentication) {

        // Utilisation de la méthode générique de gestion d'erreurs
        return handleTranslationRequest(conteId, "conte", null, id -> {
            TraductionConteDTO traduction = traductionConteService.traduireConte(id);
            // Simuler la source pour le DTO de retour (la traduction réelle reste basée sur FR)
            traduction.setLangueSource(langueSource);
            return traduction;
        });
    }

    /**
     * Affiche le contenu d'un fichier uploadé.
     * * Endpoint : GET /api/traduction/conte/{conteId}/fichier
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

        try {
            if (conte.getUrlFichier() != null && !conte.getUrlFichier().trim().isEmpty()) {
                // Lire le contenu réel du fichier via le service délégué
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
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage du contenu du fichier: " + e.getMessage());
            response.put("erreur", "Impossible de lire le fichier");
            response.put("detail", e.getMessage());
            response.put("statut", "ERREUR LECTURE FICHIER");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lit le contenu d'un fichier uploadé (PDF, TXT, DOC, DOCX).
     */
    private String lireContenuFichier(String urlFichier) {
        return fileContentService.lireContenuFichier(urlFichier);
    }

    // NOTE: Les méthodes privées de lecture de fichiers (lireFichierTexte, lireFichierPDF, lireFichierWord)
    // ont été supprimées car elles existent déjà dans TraductionConteService et sont redondantes ici.
    // L'appel au service délégué `fileContentService.lireContenuFichier(urlFichier)` est suffisant.
}