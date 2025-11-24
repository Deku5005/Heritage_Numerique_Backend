package com.heritage.controller;

import com.heritage.service.LectureVocaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour la lecture vocale des contenus familiaux.
 * 
 * Endpoints :
 * - GET /api/lecture-vocale/contenu/{contenuId} : Génère l'audio pour un contenu familial (langue par défaut: français)
 * - GET /api/lecture-vocale/contenu/{contenuId}/{lang} : Génère l'audio pour un contenu familial dans une langue spécifique
 * - GET /api/lecture-vocale/contenu/{contenuId}/texte : Récupère le texte complet pour la lecture vocale
 * 
 * Sécurité :
 * - Les endpoints nécessitent une authentification
 * - Seuls les membres de la famille peuvent accéder aux contenus familiaux
 */
@RestController
@RequestMapping("/api/lecture-vocale")
@CrossOrigin(origins = "*")
@Tag(name = "🔊 Lecture Vocale - Contenus Familiaux", description = "Endpoints pour la lecture vocale des contenus familiaux via Djelia TTS")
public class LectureVocaleController {

    private final LectureVocaleService lectureVocaleService;

    public LectureVocaleController(LectureVocaleService lectureVocaleService) {
        this.lectureVocaleService = lectureVocaleService;
    }

    /**
     * Génère l'audio pour un contenu familial (langue par défaut: français).
     * 
     * URL : GET /api/lecture-vocale/contenu/{contenuId}
     * 
     * @param contenuId ID du contenu familial
     * @param authentication Authentification de l'utilisateur
     * @return Fichier audio (MP3 ou WAV)
     */
    @Operation(
        summary = "Générer l'audio pour un contenu familial",
        description = "Génère un fichier audio pour la lecture vocale d'un contenu familial. Inclut le titre, la description, le contenu textuel et le contenu extrait du fichier. Langue par défaut: français."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Audio généré avec succès",
            content = @Content(
                mediaType = "audio/mpeg",
                schema = @Schema(type = "string", format = "binary")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Contenu non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la génération audio")
    })
    @GetMapping("/contenu/{contenuId}")
    public ResponseEntity<byte[]> genererAudio(
            @Parameter(description = "ID du contenu familial", required = true, example = "1")
            @PathVariable Long contenuId,
            Authentication authentication) {
        
        try {
            // Vérifier que le contenu existe
            if (!lectureVocaleService.contenuExiste(contenuId)) {
                return ResponseEntity.notFound().build();
            }

            // Générer l'audio en français par défaut
            byte[] audioBytes = lectureVocaleService.genererAudio(contenuId, "fr");

            if (audioBytes == null || audioBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la génération audio".getBytes());
            }

            // Définir les headers pour le téléchargement/lecture audio
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioBytes.length);
            headers.setContentDispositionFormData("attachment", "lecture_" + contenuId + ".mp3");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audioBytes);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération audio: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Génère l'audio pour un contenu familial dans une langue spécifique.
     * 
     * URL : GET /api/lecture-vocale/contenu/{contenuId}/{lang}
     * 
     * @param contenuId ID du contenu familial
     * @param lang Langue pour la lecture (fr, en, bm)
     * @param authentication Authentification de l'utilisateur
     * @return Fichier audio (MP3 ou WAV)
     */
    @Operation(
        summary = "Générer l'audio pour un contenu familial dans une langue spécifique",
        description = "Génère un fichier audio pour la lecture vocale d'un contenu familial dans la langue spécifiée (fr, en, bm). Inclut le titre, la description, le contenu textuel et le contenu extrait du fichier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Audio généré avec succès",
            content = @Content(
                mediaType = "audio/mpeg",
                schema = @Schema(type = "string", format = "binary")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Contenu non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la génération audio")
    })
    @GetMapping("/contenu/{contenuId}/{lang}")
    public ResponseEntity<byte[]> genererAudioParLangue(
            @Parameter(description = "ID du contenu familial", required = true, example = "1")
            @PathVariable Long contenuId,
            @Parameter(description = "Langue pour la lecture (fr, en, bm)", required = true, example = "fr")
            @PathVariable String lang,
            Authentication authentication) {
        
        try {
            // Vérifier que le contenu existe
            if (!lectureVocaleService.contenuExiste(contenuId)) {
                return ResponseEntity.notFound().build();
            }

            // Générer l'audio dans la langue spécifiée
            byte[] audioBytes = lectureVocaleService.genererAudio(contenuId, lang);

            if (audioBytes == null || audioBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de la génération audio".getBytes());
            }

            // Définir les headers pour le téléchargement/lecture audio
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioBytes.length);
            headers.setContentDispositionFormData("attachment", "lecture_" + contenuId + "_" + lang + ".mp3");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audioBytes);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération audio: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère le texte complet pour la lecture vocale d'un contenu familial.
     * 
     * URL : GET /api/lecture-vocale/contenu/{contenuId}/texte
     * 
     * @param contenuId ID du contenu familial
     * @param authentication Authentification de l'utilisateur
     * @return Texte complet pour la lecture vocale
     */
    @Operation(
        summary = "Récupérer le texte complet pour la lecture vocale",
        description = "Récupère le texte complet d'un contenu familial qui sera utilisé pour la lecture vocale. Inclut le titre, la description, le contenu textuel et le contenu extrait du fichier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Texte récupéré avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = java.util.Map.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Contenu non trouvé")
    })
    @GetMapping("/contenu/{contenuId}/texte")
    public ResponseEntity<java.util.Map<String, String>> getTextePourLecture(
            @Parameter(description = "ID du contenu familial", required = true, example = "1")
            @PathVariable Long contenuId,
            Authentication authentication) {
        
        try {
            // Vérifier que le contenu existe
            if (!lectureVocaleService.contenuExiste(contenuId)) {
                return ResponseEntity.notFound().build();
            }

            // Récupérer le texte complet
            String texte = lectureVocaleService.getTexteCompletPourLecture(contenuId);

            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("contenuId", contenuId.toString());
            response.put("texte", texte);
            response.put("longueur", String.valueOf(texte.length()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération du texte: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

