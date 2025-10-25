package com.heritage.controller;

import com.heritage.dto.ContributionMembreDTO;
import com.heritage.dto.ContributionsFamilleDTO;
import com.heritage.service.ContributionFamilleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des contributions de famille.
 * Fournit des statistiques détaillées sur les contributions de chaque membre.
 */
@RestController
@RequestMapping("/api/contributions")
public class ContributionFamilleController {

    private final ContributionFamilleService contributionFamilleService;

    public ContributionFamilleController(ContributionFamilleService contributionFamilleService) {
        this.contributionFamilleService = contributionFamilleService;
    }

    /**
     * Récupère toutes les contributions d'une famille avec statistiques par membre.
     * 
     * Endpoint : GET /api/contributions/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Statistiques des contributions de la famille
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<ContributionsFamilleDTO> getContributionsFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        ContributionsFamilleDTO contributions = contributionFamilleService.getContributionsFamille(familleId);
        return ResponseEntity.ok(contributions);
    }

    /**
     * Récupère les contributions d'un membre spécifique dans une famille.
     * 
     * Endpoint : GET /api/contributions/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Statistiques des contributions du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<ContributionMembreDTO> getContributionsMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        ContributionMembreDTO contributions = contributionFamilleService.getContributionsMembre(familleId, membreId);
        return ResponseEntity.ok(contributions);
    }
}
