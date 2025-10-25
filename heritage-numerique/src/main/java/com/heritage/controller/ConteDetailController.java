package com.heritage.controller;

import com.heritage.dto.ConteDetailDTO;
import com.heritage.service.ConteDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des contes avec leur contenu détaillé.
 * Permet d'afficher le contenu des fichiers PDF et texte.
 */
@RestController
@RequestMapping("/api/conte-detail")
public class ConteDetailController {

    private final ConteDetailService conteDetailService;

    public ConteDetailController(ConteDetailService conteDetailService) {
        this.conteDetailService = conteDetailService;
    }

    /**
     * Récupère un conte avec son contenu détaillé.
     * 
     * Endpoint : GET /api/conte-detail/{conteId}
     * 
     * @param conteId ID du conte
     * @param authentication Authentification de l'utilisateur
     * @return DTO du conte avec son contenu
     */
    @GetMapping("/{conteId}")
    public ResponseEntity<ConteDetailDTO> getConteDetail(
            @PathVariable Long conteId,
            Authentication authentication) {
        
        ConteDetailDTO conte = conteDetailService.getConteDetail(conteId);
        return ResponseEntity.ok(conte);
    }

    /**
     * Récupère tous les contes d'une famille avec leur contenu détaillé.
     * 
     * Endpoint : GET /api/conte-detail/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des contes avec leur contenu
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<ConteDetailDTO>> getContesDetailByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<ConteDetailDTO> contes = conteDetailService.getContesDetailByFamille(familleId);
        return ResponseEntity.ok(contes);
    }
}
