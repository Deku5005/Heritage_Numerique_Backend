package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.service.SuperAdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;


/**
 * Contrôleur pour le dashboard du super-admin.
 * Fournit toutes les fonctionnalités de gestion globale de l'application.
 */
@RestController
@RequestMapping("/api/superadmin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class SuperAdminDashboardController {


    private final SuperAdminDashboardService superAdminDashboardService;


    public SuperAdminDashboardController(SuperAdminDashboardService superAdminDashboardService) {
        this.superAdminDashboardService = superAdminDashboardService;
    }


    /**
     * Récupère le dashboard complet du super-admin.
     * * Endpoint : GET /api/superadmin/dashboard
     * * @param authentication Authentification de l'utilisateur
     * @return Dashboard complet avec toutes les statistiques
     */
    @GetMapping
    public ResponseEntity<SuperAdminDashboardDTO> getDashboardComplet(Authentication authentication) {
        SuperAdminDashboardDTO dashboard = superAdminDashboardService.getDashboardComplet();
        return ResponseEntity.ok(dashboard);
    }


    /**
     * Récupère toutes les familles de l'application.
     * * Endpoint : GET /api/superadmin/dashboard/familles
     * * @param authentication Authentification de l'utilisateur
     * @return Liste de toutes les familles
     */
    @GetMapping("/familles")
    public ResponseEntity<List<FamilleSuperAdminDTO>> getAllFamilles(Authentication authentication) {
        List<FamilleSuperAdminDTO> familles = superAdminDashboardService.getAllFamilles();
        return ResponseEntity.ok(familles);
    }


    /**
     * Récupère tous les quiz publics créés par le super-admin.
     * * Endpoint : GET /api/superadmin/dashboard/quiz-publics
     * * @param authentication Authentification de l'utilisateur
     * @return Liste des quiz publics
     */
    @GetMapping("/quiz-publics")
    public ResponseEntity<List<QuizPublicDTO>> getQuizPublics(Authentication authentication) {
        List<QuizPublicDTO> quizPublics = superAdminDashboardService.getQuizPublics();
        return ResponseEntity.ok(quizPublics);
    }


    /**
     * Récupère tous les contes de l'application.
     * * Endpoint : GET /api/superadmin/dashboard/contes
     * * @param authentication Authentification de l'utilisateur
     * @return Liste de tous les contes
     */
    @GetMapping("/contes")
    public ResponseEntity<List<ContenuGlobalDTO>> getAllContes(Authentication authentication) {
        List<ContenuGlobalDTO> contes = superAdminDashboardService.getAllContes();
        return ResponseEntity.ok(contes);
    }


    /**
     * Récupère le détail d’un conte par son ID.
     *
     * Endpoint : GET /api/superadmin/dashboard/contes/{id}
     *
     * @param id ID du conte
     * @param authentication Authentification de l'utilisateur
     * @return Détails complets du conte
     */
    @GetMapping("/contes/{id}")
    public ResponseEntity<ContenuGlobalDTO> getDetailConte(
            @PathVariable Long id,
            Authentication authentication) {
        ContenuGlobalDTO conte = superAdminDashboardService.getDetailConte(id);
        return ResponseEntity.ok(conte);
    }


    /**
     * Récupère tous les artisanats de l'application.
     * * Endpoint : GET /api/superadmin/dashboard/artisanats
     * * @param authentication Authentification de l'utilisateur
     * @return Liste de tous les artisanats
     */
    @GetMapping("/artisanats")
    public ResponseEntity<List<ContenuGlobalDTO>> getAllArtisanats(Authentication authentication) {
        List<ContenuGlobalDTO> artisanats = superAdminDashboardService.getAllArtisanats();
        return ResponseEntity.ok(artisanats);
    }


    /**
     * Récupère tous les proverbes de l'application.
     * * Endpoint : GET /api/superadmin/dashboard/proverbes
     * * @param authentication Authentification de l'utilisateur
     * @return Liste de tous les proverbes
     */
    @GetMapping("/proverbes")
    public ResponseEntity<List<ContenuGlobalDTO>> getAllProverbes(Authentication authentication) {
        List<ContenuGlobalDTO> proverbes = superAdminDashboardService.getAllProverbes();
        return ResponseEntity.ok(proverbes);
    }


    /**
     * Récupère toutes les devinettes de l'application.
     * * Endpoint : GET /api/superadmin/dashboard/devinettes
     * * @param authentication Authentification de l'utilisateur
     * @return Liste de toutes les devinettes
     */
    @GetMapping("/devinettes")
    public ResponseEntity<List<ContenuGlobalDTO>> getAllDevinettes(Authentication authentication) {
        List<ContenuGlobalDTO> devinettes = superAdminDashboardService.getAllDevinettes();
        return ResponseEntity.ok(devinettes);
    }
    /**
     * Supprime une devinette spécifique par son ID.
     *
     * Endpoint : DELETE /api/superadmin/dashboard/devinettes/{id}
     */
    @DeleteMapping("/devinettes/{id}")
    public ResponseEntity<Void> supprimerDevinette(
            @PathVariable Long id,
            Authentication authentication) {


        superAdminDashboardService.supprimerDevinette(id);
        return ResponseEntity.noContent().build(); // Code HTTP 204 : succès sans corps de réponse
    }


    /**
     * Récupère le détail d’un proverbe par son ID.
     * Endpoint : GET /api/superadmin/dashboard/proverbes/{id}
     */
    @GetMapping("/proverbes/{id}")
    public ResponseEntity<ContenuGlobalDTO> getDetailProverbe(
            @PathVariable Long id,
            Authentication authentication) {
        ContenuGlobalDTO proverbe = superAdminDashboardService.getDetailProverbe(id);
        return ResponseEntity.ok(proverbe);
    }


    /**
     * Supprime un proverbe spécifique par son ID. 👈 Appel du service mis à jour
     * Endpoint : DELETE /api/superadmin/dashboard/proverbes/{id}
     */
    @DeleteMapping("/proverbes/{id}")
    public ResponseEntity<Void> supprimerProverbe(
            @PathVariable Long id,
            Authentication authentication) {


        superAdminDashboardService.supprimerProverbe(id);


        // Code HTTP 204 : succès sans corps de réponse, standard pour la suppression
        return ResponseEntity.noContent().build();
    }


    /**
     * Récupère le détail d’un artisanat par son ID.
     * Endpoint : GET /api/superadmin/dashboard/artisanats/{id}
     */
    @GetMapping("/artisanats/{id}")
    public ResponseEntity<ContenuGlobalDTO> getDetailArtisanat(
            @PathVariable Long id,
            Authentication authentication) {
        ContenuGlobalDTO artisanat = superAdminDashboardService.getDetailArtisanat(id);
        return ResponseEntity.ok(artisanat);
    }
}

