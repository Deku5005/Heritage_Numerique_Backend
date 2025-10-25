package com.heritage.controller;

import com.heritage.dto.DashboardPersonnelDTO;
import com.heritage.service.DashboardPersonnelService;
import com.heritage.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour le dashboard personnel de l'utilisateur.
 * 
 * Endpoints :
 * - GET /api/dashboard/personnel : récupérer le dashboard de l'utilisateur connecté
 * 
 * Le dashboard affiche :
 * - Informations utilisateur
 * - Invitations en attente (avec boutons accepter/refuser)
 * - Familles de l'utilisateur
 * - Statistiques personnelles (contenus créés, quiz créés, etc.)
 * - Notifications non lues
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardPersonnelController {

    private final DashboardPersonnelService dashboardPersonnelService;

    public DashboardPersonnelController(DashboardPersonnelService dashboardPersonnelService) {
        this.dashboardPersonnelService = dashboardPersonnelService;
    }

    /**
     * Récupère le dashboard personnel de l'utilisateur connecté.
     * 
     * URL : GET /api/dashboard/personnel
     * Headers : Authorization: Bearer <token>
     * 
     * Réponse :
     * {
     *   "userId": 1,
     *   "nom": "Dupont",
     *   "prenom": "Jean",
     *   "email": "jean@example.com",
     *   "role": "ROLE_MEMBRE",
     *   "nombreFamillesAppartenance": 2,
     *   "nombreInvitationsEnAttente": 1,
     *   "nombreContenusCreés": 5,
     *   "nombreQuizCreés": 2,
     *   "nombreNotificationsNonLues": 3,
     *   "invitationsEnAttente": [
     *     {
     *       "id": 1,
     *       "nomFamille": "Famille Martin",
     *       "codeInvitation": "XYZ789",
     *       "statut": "EN_ATTENTE",
     *       ...
     *     }
     *   ],
     *   "familles": [...]
     * }
     * 
     * @param authentication Authentification
     * @return Dashboard personnel
     */
    @GetMapping("/personnel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<DashboardPersonnelDTO> getDashboardPersonnel(Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        DashboardPersonnelDTO dashboard = dashboardPersonnelService.getDashboardPersonnel(utilisateurId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Récupère les invitations en attente de l'utilisateur connecté.
     * 
     * URL : GET /api/dashboard-personnel/invitations
     * Headers : Authorization: Bearer <token>
     * 
     * @param authentication Authentification
     * @return Liste des invitations en attente
     */
    @GetMapping("/invitations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> getInvitationsEnAttente(Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        Object invitations = dashboardPersonnelService.getInvitationsEnAttente(utilisateurId);
        return ResponseEntity.ok(invitations);
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

