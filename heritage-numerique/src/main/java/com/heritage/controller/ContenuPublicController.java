package com.heritage.controller;

import com.heritage.dto.ArtisanatDTO;
import com.heritage.dto.ConteDTO;
import com.heritage.dto.DevinetteDTO;
import com.heritage.dto.ProverbeDTO;
import com.heritage.service.ContenuPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la récupération des contenus publics par catégories.
 * 
 * Endpoints :
 * - GET /api/public/contes : Récupère tous les contes publics
 * - GET /api/public/artisanats : Récupère tous les artisanats publics
 * - GET /api/public/proverbes : Récupère tous les proverbes publics
 * - GET /api/public/devinettes : Récupère toutes les devinettes publiques
 * 
 * Sécurité :
 * - Tous les endpoints sont accessibles sans authentification
 * - Seuls les contenus avec le statut PUBLIE sont retournés
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
}

