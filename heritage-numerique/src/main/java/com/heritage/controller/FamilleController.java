package com.heritage.controller;

import com.heritage.dto.FamilleDTO;
import com.heritage.dto.FamilleRequest;
import com.heritage.dto.ChangerRoleRequest;
import com.heritage.service.FamilleService;
import com.heritage.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des familles.
 * 
 * Endpoints protégés (authentification requise).
 */
@RestController
@RequestMapping("/api/familles")
@CrossOrigin(origins = "*")
@Tag(name = "👨‍👩‍👧‍👦 Gestion des Familles", description = "Endpoints pour la création et gestion des familles")
@SecurityRequirement(name = "Bearer Authentication")
public class FamilleController {

    private final FamilleService familleService;

    public FamilleController(FamilleService familleService) {
        this.familleService = familleService;
    }

    /**
     * Crée une nouvelle famille.
     * L'utilisateur connecté devient automatiquement ADMIN de la famille.
     * 
     * URL : POST /api/familles
     * Headers : Authorization: Bearer <token>
     * 
     * Body JSON :
     * {
     *   "nom": "Famille Dupont",
     *   "description": "Notre histoire familiale"
     * }
     * 
     * @param request Requête de création
     * @return Famille créée
     */
    @PostMapping
    public ResponseEntity<FamilleDTO> createFamille(@Valid @RequestBody FamilleRequest request) {
        // Récupérer l'ID de l'utilisateur connecté depuis le SecurityContext
        // (défini par le JwtAuthenticationFilter)
        Long userId = getCurrentUserId();
        
        FamilleDTO famille = familleService.createFamille(request, userId);
        return new ResponseEntity<>(famille, HttpStatus.CREATED);
    }

    /**
     * Récupère une famille par son ID.
     * 
     * URL : GET /api/familles/{id}
     * Headers : Authorization: Bearer <token>
     * 
     * @param id ID de la famille
     * @return Famille
     */
    @GetMapping("/{id}")
    public ResponseEntity<FamilleDTO> getFamille(@PathVariable Long id) {
        FamilleDTO famille = familleService.getFamilleById(id);
        return ResponseEntity.ok(famille);
    }

    /**
     * Récupère toutes les familles de l'utilisateur connecté.
     * 
     * URL : GET /api/familles/mes-familles
     * Headers : Authorization: Bearer <token>
     * 
     * @return Liste des familles
     */
    @GetMapping("/mes-familles")
    public ResponseEntity<List<FamilleDTO>> getMesFamilles() {
        Long userId = getCurrentUserId();
        List<FamilleDTO> familles = familleService.getFamillesByUtilisateur(userId);
        return ResponseEntity.ok(familles);
    }

    /**
     * Change le rôle d'un membre dans une famille.
     * Seul l'ADMIN de la famille peut effectuer cette action.
     * 
     * URL : PUT /api/familles/{familleId}/membres/{membreId}/role
     * Headers : Authorization: Bearer <token>
     * 
     * Body JSON :
     * {
     *   "nouveauRole": "EDITEUR"
     * }
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre (MembreFamille)
     * @param request Requête contenant le nouveau rôle
     * @return Famille mise à jour
     */
    @PutMapping("/{familleId}/membres/{membreId}/role")
    @PreAuthorize("hasRole('MEMBRE')")
    public ResponseEntity<FamilleDTO> changerRoleMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            @Valid @RequestBody ChangerRoleRequest request) {
        
        Long adminId = getCurrentUserId();
        FamilleDTO famille = familleService.changerRoleMembre(familleId, membreId, request, adminId);
        return ResponseEntity.ok(famille);
    }

    /**
     * Récupère l'ID de l'utilisateur connecté.
     * Utilise AuthenticationHelper qui extrait l'ID du UserPrincipal.
     * 
     * @return ID de l'utilisateur
     */
    private Long getCurrentUserId() {
        return AuthenticationHelper.getCurrentUserId();
    }
}

