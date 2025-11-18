package com.heritage.controller;

import com.heritage.dto.ArtisanatDTO;
import com.heritage.dto.ConteDTO;
import com.heritage.dto.DevinetteDTO;
import com.heritage.dto.ProverbeDTO;
import com.heritage.dto.TraductionConteDTO;
import com.heritage.service.ContenuPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Controller REST pour la récupération des contenus publics par catégories.
 * 
 * Endpoints de récupération :
 * - GET /api/public/contes : Récupère tous les contes publics
 * - GET /api/public/artisanats : Récupère tous les artisanats publics
 * - GET /api/public/proverbes : Récupère tous les proverbes publics
 * - GET /api/public/devinettes : Récupère toutes les devinettes publiques
 * 
 * Endpoints de traduction (publics) :
 * - GET /api/public/traduction/contes/{conteId} : Traduit un conte public dans toutes les langues
 * - GET /api/public/traduction/contes/{conteId}/{lang} : Traduit un conte public dans une langue spécifique (fr, en, bm)
 * - GET /api/public/traduction/artisanats/{artisanatId} : Traduit un artisanat public dans toutes les langues
 * - GET /api/public/traduction/proverbes/{proverbeId} : Traduit un proverbe public dans toutes les langues
 * - GET /api/public/traduction/devinettes/{devinetteId} : Traduit une devinette publique dans toutes les langues
 * 
 * Sécurité :
 * - Tous les endpoints sont accessibles sans authentification
 * - Seuls les contenus avec le statut PUBLIE sont retournés/traduits
 * - Inclut les contenus créés par le SuperAdmin et les contenus validés des familles
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
@Tag(name = "🌍 Contenus Publics", description = "Endpoints pour la récupération des contenus publics par catégorie")
public class ContenuPublicController {

    private final ContenuPublicService contenuPublicService;

    public ContenuPublicController(ContenuPublicService contenuPublicService) {
        this.contenuPublicService = contenuPublicService;
    }

    /**
     * Récupère tous les contes publics.
     * 
     * URL : GET /api/public/contes
     * 
     * @return Liste des contes publics avec leurs détails
     */
    @Operation(
        summary = "Récupérer tous les contes publics",
        description = "Retourne tous les contes ayant le statut PUBLIE. Accessible sans authentification. Inclut les contes créés par le SuperAdmin."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des contes publics récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ConteDTO.class)
            )
        )
    })
    @GetMapping("/contes")
    public ResponseEntity<List<ConteDTO>> getContesPublics() {
        List<ConteDTO> contes = contenuPublicService.getContesPublics();
        return ResponseEntity.ok(contes);
    }

    /**
     * Récupère tous les artisanats publics.
     * 
     * URL : GET /api/public/artisanats
     * 
     * @return Liste des artisanats publics avec leurs détails
     */
    @Operation(
        summary = "Récupérer tous les artisanats publics",
        description = "Retourne tous les artisanats ayant le statut PUBLIE. Accessible sans authentification. Inclut les artisanats créés par le SuperAdmin."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des artisanats publics récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArtisanatDTO.class)
            )
        )
    })
    @GetMapping("/artisanats")
    public ResponseEntity<List<ArtisanatDTO>> getArtisanatsPublics() {
        List<ArtisanatDTO> artisanats = contenuPublicService.getArtisanatsPublics();
        return ResponseEntity.ok(artisanats);
    }

    /**
     * Récupère tous les proverbes publics.
     * 
     * URL : GET /api/public/proverbes
     * 
     * @return Liste des proverbes publics avec leurs détails
     */
    @Operation(
        summary = "Récupérer tous les proverbes publics",
        description = "Retourne tous les proverbes ayant le statut PUBLIE. Accessible sans authentification. Inclut les proverbes créés par le SuperAdmin."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des proverbes publics récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProverbeDTO.class)
            )
        )
    })
    @GetMapping("/proverbes")
    public ResponseEntity<List<ProverbeDTO>> getProverbesPublics() {
        List<ProverbeDTO> proverbes = contenuPublicService.getProverbesPublics();
        return ResponseEntity.ok(proverbes);
    }

    /**
     * Récupère toutes les devinettes publiques.
     * 
     * URL : GET /api/public/devinettes
     * 
     * @return Liste des devinettes publiques avec leurs détails
     */
    @Operation(
        summary = "Récupérer toutes les devinettes publiques",
        description = "Retourne toutes les devinettes ayant le statut PUBLIE. Accessible sans authentification. Inclut les devinettes créées par le SuperAdmin."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des devinettes publiques récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DevinetteDTO.class)
            )
        )
    })
    @GetMapping("/devinettes")
    public ResponseEntity<List<DevinetteDTO>> getDevinettesPubliques() {
        List<DevinetteDTO> devinettes = contenuPublicService.getDevinettesPubliques();
        return ResponseEntity.ok(devinettes);
    }

    // -------------------------------------------------------------------------
    // --- Endpoints de Traduction pour Contenus Publics ---
    // -------------------------------------------------------------------------

    /**
     * Traduit un conte public en français, bambara et anglais.
     * 
     * URL : GET /api/public/traduction/contes/{conteId}
     * 
     * @param conteId ID du conte public à traduire
     * @return DTO avec toutes les traductions
     */
    @Operation(
        summary = "Traduire un conte public dans toutes les langues",
        description = "Traduit un conte public (statut PUBLIE) en français, bambara et anglais via l'API Djelia. Accessible sans authentification."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Traduction réussie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TraductionConteDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Conte non trouvé"),
        @ApiResponse(responseCode = "403", description = "Le conte n'est pas public"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la traduction")
    })
    @GetMapping("/traduction/contes/{conteId}")
    public ResponseEntity<TraductionConteDTO> traduireContePublic(
            @Parameter(description = "ID du conte public à traduire", required = true, example = "1")
            @PathVariable Long conteId) {
        try {
            TraductionConteDTO traduction = contenuPublicService.traduireContePublic(conteId);
            return ResponseEntity.ok(traduction);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("n'est pas public")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Traduit un conte public dans une langue spécifique.
     * 
     * URL : GET /api/public/traduction/contes/{conteId}/{lang}
     * 
     * @param conteId ID du conte public à traduire
     * @param lang Langue cible (fr, en, bm)
     * @return DTO avec la traduction dans la langue spécifiée
     */
    @Operation(
        summary = "Traduire un conte public dans une langue spécifique",
        description = "Traduit un conte public (statut PUBLIE) dans la langue spécifiée (fr, en, bm). Accessible sans authentification."
    )
    @GetMapping("/traduction/contes/{conteId}/{lang}")
    public ResponseEntity<TraductionConteDTO> traduireContePublicParLangue(
            @Parameter(description = "ID du conte public à traduire", required = true, example = "1")
            @PathVariable Long conteId,
            @Parameter(description = "Langue cible (fr, en, bm)", required = true, example = "fr")
            @PathVariable String lang) {
        try {
            TraductionConteDTO traduction = contenuPublicService.traduireContePublic(conteId);
            return ResponseEntity.ok(filterTranslationByLanguage(traduction, lang));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("n'est pas public")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Traduit un artisanat public en français, bambara et anglais.
     * 
     * URL : GET /api/public/traduction/artisanats/{artisanatId}
     * 
     * @param artisanatId ID de l'artisanat public à traduire
     * @return DTO avec toutes les traductions
     */
    @Operation(
        summary = "Traduire un artisanat public dans toutes les langues",
        description = "Traduit un artisanat public (statut PUBLIE) en français, bambara et anglais via l'API Djelia. Accessible sans authentification."
    )
    @GetMapping("/traduction/artisanats/{artisanatId}")
    public ResponseEntity<TraductionConteDTO> traduireArtisanatPublic(
            @Parameter(description = "ID de l'artisanat public à traduire", required = true, example = "1")
            @PathVariable Long artisanatId) {
        try {
            TraductionConteDTO traduction = contenuPublicService.traduireArtisanatPublic(artisanatId);
            return ResponseEntity.ok(traduction);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("n'est pas public")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Traduit un proverbe public en français, bambara et anglais.
     * 
     * URL : GET /api/public/traduction/proverbes/{proverbeId}
     * 
     * @param proverbeId ID du proverbe public à traduire
     * @return DTO avec toutes les traductions
     */
    @Operation(
        summary = "Traduire un proverbe public dans toutes les langues",
        description = "Traduit un proverbe public (statut PUBLIE) en français, bambara et anglais via l'API Djelia. Accessible sans authentification."
    )
    @GetMapping("/traduction/proverbes/{proverbeId}")
    public ResponseEntity<TraductionConteDTO> traduireProverbePublic(
            @Parameter(description = "ID du proverbe public à traduire", required = true, example = "1")
            @PathVariable Long proverbeId) {
        try {
            TraductionConteDTO traduction = contenuPublicService.traduireProverbePublic(proverbeId);
            return ResponseEntity.ok(traduction);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("n'est pas public")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Traduit une devinette publique en français, bambara et anglais.
     * 
     * URL : GET /api/public/traduction/devinettes/{devinetteId}
     * 
     * @param devinetteId ID de la devinette publique à traduire
     * @return DTO avec toutes les traductions
     */
    @Operation(
        summary = "Traduire une devinette publique dans toutes les langues",
        description = "Traduit une devinette publique (statut PUBLIE) en français, bambara et anglais via l'API Djelia. Accessible sans authentification."
    )
    @GetMapping("/traduction/devinettes/{devinetteId}")
    public ResponseEntity<TraductionConteDTO> traduireDevinettePublic(
            @Parameter(description = "ID de la devinette publique à traduire", required = true, example = "1")
            @PathVariable Long devinetteId) {
        try {
            TraductionConteDTO traduction = contenuPublicService.traduireDevinettePublic(devinetteId);
            return ResponseEntity.ok(traduction);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("n'est pas public")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Filtre une traduction pour ne retourner que la langue spécifiée.
     * 
     * @param traduction Traduction complète
     * @param langCode Code de langue (fr, en, bm)
     * @return Traduction filtrée
     */
    private TraductionConteDTO filterTranslationByLanguage(TraductionConteDTO traduction, String langCode) {
        // Mapper les codes de langue courts vers les codes Djelia
        String mappedLang = mapToDjeliaLanguageCode(langCode);
        if (mappedLang == null) {
            mappedLang = langCode;
        }

        Set<String> langSet = Set.of(mappedLang);

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
                return "eng_Latn";
            default:
                return langCode;
        }
    }
}

