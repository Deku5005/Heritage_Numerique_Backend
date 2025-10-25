package com.heritage.controller;

import com.heritage.dto.ConteDTO;
import com.heritage.service.ConteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des contes de famille.
 * Fournit des informations détaillées sur les contes créés par les membres.
 */
@RestController
@RequestMapping("/api/contes")
public class ConteController {

    private final ConteService conteService;

    public ConteController(ConteService conteService) {
        this.conteService = conteService;
    }

    /**
     * Récupère tous les contes d'une famille avec leurs informations détaillées.
     * 
     * Endpoint : GET /api/contes/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des contes avec informations des auteurs
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<ConteDTO>> getContesByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<ConteDTO> contes = conteService.getContesByFamille(familleId);
        return ResponseEntity.ok(contes);
    }

    /**
     * Récupère un conte spécifique par son ID.
     * 
     * Endpoint : GET /api/contes/{conteId}
     * 
     * @param conteId ID du conte
     * @param authentication Authentification de l'utilisateur
     * @return DTO du conte
     */
    @GetMapping("/{conteId}")
    public ResponseEntity<ConteDTO> getConteById(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        ConteDTO conte = conteService.getConteById(conteId);
        return ResponseEntity.ok(conte);
    }

    /**
     * Récupère les contes d'un membre spécifique dans une famille.
     * 
     * Endpoint : GET /api/contes/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Liste des contes du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<List<ConteDTO>> getContesByMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        List<ConteDTO> contes = conteService.getContesByMembre(familleId, membreId);
        return ResponseEntity.ok(contes);
    }
}
