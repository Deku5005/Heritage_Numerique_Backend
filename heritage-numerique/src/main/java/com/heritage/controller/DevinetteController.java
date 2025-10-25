package com.heritage.controller;

import com.heritage.dto.DevinetteDTO;
import com.heritage.service.DevinetteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des devinettes de famille.
 * Fournit des informations détaillées sur les devinettes créées par les membres.
 */
@RestController
@RequestMapping("/api/devinettes")
public class DevinetteController {

    private final DevinetteService devinetteService;

    public DevinetteController(DevinetteService devinetteService) {
        this.devinetteService = devinetteService;
    }

    /**
     * Récupère toutes les devinettes d'une famille avec leurs informations détaillées.
     * 
     * Endpoint : GET /api/devinettes/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des devinettes avec informations des auteurs
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<DevinetteDTO>> getDevinettesByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<DevinetteDTO> devinettes = devinetteService.getDevinettesByFamille(familleId);
        return ResponseEntity.ok(devinettes);
    }

    /**
     * Récupère une devinette spécifique par son ID.
     * 
     * Endpoint : GET /api/devinettes/{devinetteId}
     * 
     * @param devinetteId ID de la devinette
     * @param authentication Authentification de l'utilisateur
     * @return DTO de la devinette
     */
    @GetMapping("/{devinetteId}")
    public ResponseEntity<DevinetteDTO> getDevinetteById(
            @PathVariable Long devinetteId,
            Authentication authentication) {
        
        DevinetteDTO devinette = devinetteService.getDevinetteById(devinetteId);
        return ResponseEntity.ok(devinette);
    }

    /**
     * Récupère les devinettes d'un membre spécifique dans une famille.
     * 
     * Endpoint : GET /api/devinettes/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Liste des devinettes du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<List<DevinetteDTO>> getDevinettesByMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        List<DevinetteDTO> devinettes = devinetteService.getDevinettesByMembre(familleId, membreId);
        return ResponseEntity.ok(devinettes);
    }
}
