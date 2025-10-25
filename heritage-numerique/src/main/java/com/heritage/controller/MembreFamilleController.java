package com.heritage.controller;

import com.heritage.dto.AjoutMembreRequest;
import com.heritage.dto.MembreFamilleDTO;
import com.heritage.service.MembreFamilleService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des membres de famille.
 * Permet de récupérer les informations des membres d'une famille.
 */
@RestController
@RequestMapping("/api/membres")
public class MembreFamilleController {

    private final MembreFamilleService membreFamilleService;

    public MembreFamilleController(MembreFamilleService membreFamilleService) {
        this.membreFamilleService = membreFamilleService;
    }

    /**
     * Récupère tous les membres d'une famille.
     * 
     * Endpoint : GET /api/membres/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des membres avec leurs informations
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<MembreFamilleDTO>> getMembresByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<MembreFamilleDTO> membres = membreFamilleService.getMembresByFamille(familleId);
        return ResponseEntity.ok(membres);
    }

    /**
     * Récupère un membre spécifique par son ID.
     * 
     * Endpoint : GET /api/membres/{membreId}
     * 
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return Informations du membre
     */
    @GetMapping("/{membreId}")
    public ResponseEntity<MembreFamilleDTO> getMembreById(
            @PathVariable Long membreId,
            Authentication authentication) {
        
        MembreFamilleDTO membre = membreFamilleService.getMembreById(membreId);
        return ResponseEntity.ok(membre);
    }

    /**
     * Ajoute manuellement un membre à une famille.
     * 
     * Endpoint : POST /api/membres/ajouter
     * 
     * Seul l'administrateur de la famille peut ajouter des membres manuellement.
     * Si l'utilisateur n'existe pas, un compte sera créé automatiquement.
     * 
     * @param request Requête d'ajout de membre
     * @param authentication Authentification de l'utilisateur
     * @return Informations du membre ajouté
     */
    @PostMapping("/ajouter")
    public ResponseEntity<MembreFamilleDTO> ajouterMembreManuellement(
            @Valid @RequestBody AjoutMembreRequest request,
            Authentication authentication) {
        
        Long adminId = AuthenticationHelper.getCurrentUserId();
        MembreFamilleDTO membre = membreFamilleService.ajouterMembreManuellement(request, adminId);
        return ResponseEntity.ok(membre);
    }
}
