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
 * Contrôleur pour la création de contenu avec types spécifiques.
 * Supporte les types : CONTE, DEVINETTE, ARTISANAT, PROVERBE
 *
 * NOTE : La vérification des permissions (rôle ADMIN/EDITEUR) est entièrement déléguée
 * au ContenuCreationService pour respecter le principe du "Contrôleur Fin / Service Épais".
 */
@RestController
@RequestMapping("/api/contenus")
@CrossOrigin(origins = "*")
@Tag(name = "📝 Création de Contenu", description = "Endpoints pour créer différents types de contenu culturel")
public class ContenuCreationController {

    private final ContenuCreationService contenuCreationService;

    // Suppression de l'injection de MembreFamilleService car il n'est plus nécessaire ici.
    public ContenuCreationController(ContenuCreationService contenuCreationService) {
        this.contenuCreationService = contenuCreationService;
    }

    // 🚨 Suppression de la méthode privée verifierPermissionCreation,
    // car la logique est gérée par le ContenuCreationService.

    /**
     * Crée un nouveau contenu selon son type (méthode générique).
     * * Endpoint : POST /api/contenus/creer
     * * @param request Requête de création de contenu
     * @param authentication Authentification de l'utilisateur
     * @return Contenu créé
     */
    @PostMapping("/creer")
    @Operation(summary = "Créer un contenu générique", description = "Créer un contenu avec tous les champs possibles")
    public ResponseEntity<Contenu> creerContenu(
            @Valid @ModelAttribute CreationContenuRequest request,
            Authentication authentication) {

        // Laisse le ContenuCreationService gérer la validation et les permissions
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        Contenu contenu = contenuCreationService.creerContenu(request, auteurId);
        return ResponseEntity.ok(contenu);
    }

    /**
     * Crée un conte.
     * * Endpoint : POST /api/contenus/conte
     * * @param request Requête de création de conte
     * @param authentication Authentification de l'utilisateur
     * @return Conte créé
     */
    @PostMapping(value = "/conte", consumes = "multipart/form-data")
    @Operation(summary = "Créer un conte", description = "Créer un conte avec titre, photo, fichier (PDF/TXT) ou texte")
    public ResponseEntity<Contenu> creerConte(
            @Valid @ModelAttribute ConteRequest request,
            Authentication authentication) {

        // Laisse le ContenuCreationService gérer la validation et les permissions
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        Contenu contenu = contenuCreationService.creerConte(request, auteurId);
        return ResponseEntity.ok(contenu);
    }

    /**
     * Crée un artisanat.
     * * Endpoint : POST /api/contenus/artisanat
     * * @param request Requête de création d'artisanat
     * @param authentication Authentification de l'utilisateur
     * @return Artisanat créé
     */
    @PostMapping(value = "/artisanat", consumes = "multipart/form-data")
    @Operation(summary = "Créer un artisanat", description = "Créer un artisanat avec titre, description, photos et vidéo optionnelle")
    public ResponseEntity<Contenu> creerArtisanat(
            @Valid @ModelAttribute ArtisanatRequest request,
            Authentication authentication) {

        // Laisse le ContenuCreationService gérer la validation et les permissions
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        Contenu contenu = contenuCreationService.creerArtisanat(request, auteurId);
        return ResponseEntity.ok(contenu);
    }

    /**
     * Crée un proverbe.
     * * Endpoint : POST /api/contenus/proverbe
     * * @param request Requête de création de proverbe
     * @param authentication Authentification de l'utilisateur
     * @return Proverbe créé
     */
    @PostMapping(value = "/proverbe", consumes = "multipart/form-data")
    @Operation(summary = "Créer un proverbe", description = "Créer un proverbe avec origine, signification, texte et photo")
    public ResponseEntity<Contenu> creerProverbe(
            @Valid @ModelAttribute ProverbeRequest request,
            Authentication authentication) {

        // Laisse le ContenuCreationService gérer la validation et les permissions
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        Contenu contenu = contenuCreationService.creerProverbe(request, auteurId);
        return ResponseEntity.ok(contenu);
    }

    /**
     * Crée une devinette.
     * * Endpoint : POST /api/contenus/devinette
     * * @param request Requête de création de devinette
     * @param authentication Authentification de l'utilisateur
     * @return Devinette créée
     */
    @PostMapping(value = "/devinette", consumes = "multipart/form-data")
    @Operation(summary = "Créer une devinette", description = "Créer une devinette avec question, réponse et photo")
    public ResponseEntity<Contenu> creerDevinette(
            @Valid @ModelAttribute DevinetteRequest request,
            Authentication authentication) {

        // Laisse le ContenuCreationService gérer la validation et les permissions
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        Contenu contenu = contenuCreationService.creerDevinette(request, auteurId);
        return ResponseEntity.ok(contenu);
    }
}
