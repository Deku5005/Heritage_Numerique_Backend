package com.heritage.controller;

import com.heritage.dto.AjoutMembreArbreRequest;
import com.heritage.dto.ArbreGenealogiqueDTO;
import com.heritage.dto.MembreArbreDTO;
import com.heritage.service.ArbreGenealogiqueService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion de l'arbre généalogique de famille.
 * Chaque famille a un seul arbre généalogique.
 */
@RestController
@RequestMapping("/api/arbre-genealogique")
public class ArbreGenealogiqueController {

    private final ArbreGenealogiqueService arbreGenealogiqueService;

    public ArbreGenealogiqueController(ArbreGenealogiqueService arbreGenealogiqueService) {
        this.arbreGenealogiqueService = arbreGenealogiqueService;
    }

    /**
     * Récupère l'arbre généalogique d'une famille avec tous ses membres.
     * 
     * Endpoint : GET /api/arbre-genealogique/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Arbre généalogique complet
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<ArbreGenealogiqueDTO> getArbreByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        ArbreGenealogiqueDTO arbre = arbreGenealogiqueService.getArbreByFamille(familleId);
        return ResponseEntity.ok(arbre);
    }

    /**
     * Ajoute un membre à l'arbre généalogique d'une famille.
     * 
     * Endpoint : POST /api/arbre-genealogique/ajouter-membre
     * 
     * @param request Requête d'ajout de membre
     * @param authentication Authentification de l'utilisateur
     * @return DTO du membre ajouté
     */
    @PostMapping("/ajouter-membre")
    public ResponseEntity<MembreArbreDTO> ajouterMembreArbre(
            @Valid @ModelAttribute AjoutMembreArbreRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        MembreArbreDTO membre = arbreGenealogiqueService.ajouterMembreArbre(request, auteurId);
        return ResponseEntity.ok(membre);
    }

    /**
     * Récupère un membre spécifique de l'arbre généalogique.
     * 
     * Endpoint : GET /api/arbre-genealogique/membre/{membreId}
     * 
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return DTO du membre
     */
    @GetMapping("/membre/{membreId}")
    public ResponseEntity<MembreArbreDTO> getMembreArbreById(
            @PathVariable Long membreId,
            Authentication authentication) {
        
        MembreArbreDTO membre = arbreGenealogiqueService.getMembreArbreById(membreId);
        return ResponseEntity.ok(membre);
    }
}