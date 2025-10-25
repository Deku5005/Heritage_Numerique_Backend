package com.heritage.controller;

import com.heritage.dto.DashboardDTO;
import com.heritage.service.DashboardService;
import com.heritage.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour le tableau de bord familial.
 * 
 * Endpoints :
 * - GET /api/dashboard/famille/{familleId} : récupérer le dashboard d'une famille
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Récupère le dashboard (statistiques) d'une famille.
     * Affiche :
     * - Nombre de membres
     * - Invitations en attente
     * - Contenus privés et publics
     * - Quiz actifs
     * - Notifications non lues
     * - Arbres généalogiques
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification
     * @return Dashboard avec statistiques
     */
    @GetMapping("/famille/{familleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<DashboardDTO> getDashboardFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        DashboardDTO dashboard = dashboardService.getDashboard(familleId, utilisateurId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Récupère l'ID de l'utilisateur depuis l'authentification.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
}

