package com.heritage.controller;

import com.heritage.dto.UtilisateurAvecRoleFamilleDTO;
import com.heritage.dto.UtilisateurDTO;
import com.heritage.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour la gestion des utilisateurs.
 * 
 * Endpoints :
 * - GET /api/utilisateurs/{id} : Récupère les informations d'un utilisateur par son ID
 * - GET /api/utilisateurs/email/{email} : Récupère les informations d'un utilisateur par son email
 * - GET /api/utilisateurs/{utilisateurId}/famille/{familleId} : Récupère un utilisateur avec son rôle dans une famille spécifique
 * 
 * Sécurité :
 * - Le mot de passe n'est JAMAIS retourné dans les réponses (DTO sans mot de passe)
 */
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
@Tag(name = "👤 Utilisateurs", description = "Endpoints pour la gestion des informations utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère les informations d'un utilisateur par son ID.
     * 
     * URL : GET /api/utilisateurs/{id}
     * 
     * @param id ID de l'utilisateur
     * @return UtilisateurDTO contenant toutes les informations (sauf le mot de passe) + les familles avec leurs rôles
     */
    @Operation(
        summary = "Récupérer un utilisateur par ID",
        description = "Retourne toutes les informations d'un utilisateur (sauf le mot de passe) incluant ses familles avec les rôles (ADMIN, EDITEUR, LECTEUR) et liens de parenté"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Utilisateur trouvé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UtilisateurDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long id) {
        UtilisateurDTO utilisateur = utilisateurService.getUserById(id);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Récupère les informations d'un utilisateur par son email.
     * 
     * URL : GET /api/utilisateurs/email/{email}
     * 
     * @param email Email de l'utilisateur
     * @return UtilisateurDTO contenant toutes les informations (sauf le mot de passe) + les familles avec leurs rôles
     */
    @Operation(
        summary = "Récupérer un utilisateur par email",
        description = "Retourne toutes les informations d'un utilisateur (sauf le mot de passe) incluant ses familles avec les rôles (ADMIN, EDITEUR, LECTEUR) et liens de parenté"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Utilisateur trouvé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UtilisateurDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé",
            content = @Content
        )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByEmail(
            @Parameter(description = "Email de l'utilisateur", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        UtilisateurDTO utilisateur = utilisateurService.getUserByEmail(email);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Récupère les informations d'un utilisateur avec son rôle dans une famille spécifique.
     * 
     * URL : GET /api/utilisateurs/{utilisateurId}/famille/{familleId}
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param familleId ID de la famille
     * @return UtilisateurAvecRoleFamilleDTO contenant les informations de l'utilisateur et son rôle dans cette famille
     */
    @Operation(
        summary = "Récupérer un utilisateur avec son rôle dans une famille",
        description = "Retourne toutes les informations d'un utilisateur (sauf le mot de passe) avec son rôle spécifique (ADMIN, EDITEUR, LECTEUR) et son lien de parenté dans une famille donnée"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Utilisateur trouvé avec son rôle dans la famille",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UtilisateurAvecRoleFamilleDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur non trouvé ou l'utilisateur n'est pas membre de cette famille",
            content = @Content
        )
    })
    @GetMapping("/{utilisateurId}/famille/{familleId}")
    public ResponseEntity<UtilisateurAvecRoleFamilleDTO> getUtilisateurWithRoleInFamille(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long utilisateurId,
            @Parameter(description = "ID de la famille", required = true, example = "1")
            @PathVariable Long familleId) {
        UtilisateurAvecRoleFamilleDTO utilisateur = utilisateurService.getUserWithRoleInFamille(utilisateurId, familleId);
        return ResponseEntity.ok(utilisateur);
    }
}

