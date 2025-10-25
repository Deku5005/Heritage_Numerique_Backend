package com.heritage.controller;

import com.heritage.dto.ArtisanatDTO;
import com.heritage.service.ArtisanatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des artisanats de famille.
 * Fournit des informations détaillées sur les artisanats créés par les membres.
 */
@RestController
@RequestMapping("/api/artisanats")
public class ArtisanatController {

    private final ArtisanatService artisanatService;

    public ArtisanatController(ArtisanatService artisanatService) {
        this.artisanatService = artisanatService;
    }

    /**
     * Récupère tous les artisanats d'une famille avec leurs informations détaillées.
     * 
     * Endpoint : GET /api/artisanats/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des artisanats avec informations des auteurs
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<ArtisanatDTO>> getArtisanatsByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<ArtisanatDTO> artisanats = artisanatService.getArtisanatsByFamille(familleId);
        return ResponseEntity.ok(artisanats);
    }

    /**
     * Récupère un artisanat spécifique par son ID.
     * 
     * Endpoint : GET /api/artisanats/{artisanatId}
     * 
     * @param artisanatId ID de l'artisanat
     * @param authentication Authentification de l'utilisateur
     * @return DTO de l'artisanat
     */
    @GetMapping("/{artisanatId}")
    public ResponseEntity<ArtisanatDTO> getArtisanatById(
            @PathVariable Long artisanatId,
            Authentication authentication) {
        
        ArtisanatDTO artisanat = artisanatService.getArtisanatById(artisanatId);
        return ResponseEntity.ok(artisanat);
    }

    /**
     * Récupère les artisanats d'un membre spécifique dans une famille.
     * 
     * Endpoint : GET /api/artisanats/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Liste des artisanats du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<List<ArtisanatDTO>> getArtisanatsByMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        List<ArtisanatDTO> artisanats = artisanatService.getArtisanatsByMembre(familleId, membreId);
        return ResponseEntity.ok(artisanats);
    }
}
