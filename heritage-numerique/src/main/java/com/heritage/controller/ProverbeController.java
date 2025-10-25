package com.heritage.controller;

import com.heritage.dto.ProverbeDTO;
import com.heritage.service.ProverbeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des proverbes de famille.
 * Fournit des informations détaillées sur les proverbes créés par les membres.
 */
@RestController
@RequestMapping("/api/proverbes")
public class ProverbeController {

    private final ProverbeService proverbeService;

    public ProverbeController(ProverbeService proverbeService) {
        this.proverbeService = proverbeService;
    }

    /**
     * Récupère tous les proverbes d'une famille avec leurs informations détaillées.
     * 
     * Endpoint : GET /api/proverbes/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des proverbes avec informations des auteurs
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<ProverbeDTO>> getProverbesByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<ProverbeDTO> proverbes = proverbeService.getProverbesByFamille(familleId);
        return ResponseEntity.ok(proverbes);
    }

    /**
     * Récupère un proverbe spécifique par son ID.
     * 
     * Endpoint : GET /api/proverbes/{proverbeId}
     * 
     * @param proverbeId ID du proverbe
     * @param authentication Authentification de l'utilisateur
     * @return DTO du proverbe
     */
    @GetMapping("/{proverbeId}")
    public ResponseEntity<ProverbeDTO> getProverbeById(
            @PathVariable Long proverbeId,
            Authentication authentication) {
        
        ProverbeDTO proverbe = proverbeService.getProverbeById(proverbeId);
        return ResponseEntity.ok(proverbe);
    }

    /**
     * Récupère les proverbes d'un membre spécifique dans une famille.
     * 
     * Endpoint : GET /api/proverbes/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Liste des proverbes du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<List<ProverbeDTO>> getProverbesByMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        List<ProverbeDTO> proverbes = proverbeService.getProverbesByMembre(familleId, membreId);
        return ResponseEntity.ok(proverbes);
    }
}
