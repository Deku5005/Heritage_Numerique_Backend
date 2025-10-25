package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.entite.Contenu;
import com.heritage.service.ContenuCreationService;
import com.heritage.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour la création de contenu en JSON (sans fichiers).
 * Endpoints alternatifs pour les tests sans upload de fichiers.
 */
@RestController
@RequestMapping("/api/contenus-json")
@CrossOrigin(origins = "*")
@Tag(name = "📝 Création de Contenu JSON", description = "Endpoints pour créer du contenu en JSON (sans fichiers)")
public class ContenuJsonController {

    private final ContenuCreationService contenuCreationService;

    public ContenuJsonController(ContenuCreationService contenuCreationService) {
        this.contenuCreationService = contenuCreationService;
    }

    @PostMapping("/conte")
    @Operation(summary = "Créer un conte (JSON)", description = "Créer un conte avec du texte uniquement (sans fichiers)")
    public ResponseEntity<Contenu> creerConteJson(
            @Valid @RequestBody ConteJsonRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        
        // Convertir en ConteRequest sans fichiers
        ConteRequest conteRequest = new ConteRequest();
        conteRequest.setIdFamille(request.getIdFamille());
        conteRequest.setIdCategorie(request.getIdCategorie());
        conteRequest.setTitre(request.getTitre());
        conteRequest.setDescription(request.getDescription());
        conteRequest.setTexteConte(request.getTexteConte());
        conteRequest.setLieu(request.getLieu());
        conteRequest.setRegion(request.getRegion());
        // Pas de fichiers
        
        Contenu contenu = contenuCreationService.creerConte(conteRequest, auteurId);
        return ResponseEntity.ok(contenu);
    }

    @PostMapping("/artisanat")
    @Operation(summary = "Créer un artisanat (JSON)", description = "Créer un artisanat avec description uniquement (sans fichiers)")
    public ResponseEntity<Contenu> creerArtisanatJson(
            @Valid @RequestBody ArtisanatJsonRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        
        // Convertir en ArtisanatRequest sans fichiers
        ArtisanatRequest artisanatRequest = new ArtisanatRequest();
        artisanatRequest.setIdFamille(request.getIdFamille());
        artisanatRequest.setIdCategorie(request.getIdCategorie());
        artisanatRequest.setTitre(request.getTitre());
        artisanatRequest.setDescription(request.getDescription());
        artisanatRequest.setLieu(request.getLieu());
        artisanatRequest.setRegion(request.getRegion());
        // Pas de fichiers
        
        Contenu contenu = contenuCreationService.creerArtisanat(artisanatRequest, auteurId);
        return ResponseEntity.ok(contenu);
    }

    @PostMapping("/proverbe")
    @Operation(summary = "Créer un proverbe (JSON)", description = "Créer un proverbe avec texte uniquement (sans fichiers)")
    public ResponseEntity<Contenu> creerProverbeJson(
            @Valid @RequestBody ProverbeJsonRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        
        // Convertir en ProverbeRequest sans fichiers
        ProverbeRequest proverbeRequest = new ProverbeRequest();
        proverbeRequest.setIdFamille(request.getIdFamille());
        proverbeRequest.setIdCategorie(request.getIdCategorie());
        proverbeRequest.setTitre(request.getTitre());
        proverbeRequest.setOrigineProverbe(request.getOrigineProverbe());
        proverbeRequest.setSignificationProverbe(request.getSignificationProverbe());
        proverbeRequest.setTexteProverbe(request.getTexteProverbe());
        proverbeRequest.setLieu(request.getLieu());
        proverbeRequest.setRegion(request.getRegion());
        // Pas de fichiers
        
        Contenu contenu = contenuCreationService.creerProverbe(proverbeRequest, auteurId);
        return ResponseEntity.ok(contenu);
    }

    @PostMapping("/devinette")
    @Operation(summary = "Créer une devinette (JSON)", description = "Créer une devinette avec texte uniquement (sans fichiers)")
    public ResponseEntity<Contenu> creerDevinetteJson(
            @Valid @RequestBody DevinetteJsonRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        
        // Convertir en DevinetteRequest sans fichiers
        DevinetteRequest devinetteRequest = new DevinetteRequest();
        devinetteRequest.setIdFamille(request.getIdFamille());
        devinetteRequest.setIdCategorie(request.getIdCategorie());
        devinetteRequest.setTitre(request.getTitre());
        devinetteRequest.setTexteDevinette(request.getTexteDevinette());
        devinetteRequest.setReponseDevinette(request.getReponseDevinette());
        devinetteRequest.setLieu(request.getLieu());
        devinetteRequest.setRegion(request.getRegion());
        // Pas de fichiers
        
        Contenu contenu = contenuCreationService.creerDevinette(devinetteRequest, auteurId);
        return ResponseEntity.ok(contenu);
    }
}
