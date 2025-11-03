package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.entite.Contenu;
import com.heritage.service.ContenuCreationService;
import com.heritage.service.MembreFamilleService;
import com.heritage.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la création de contenu avec types spécifiques.
 * Supporte les types : CONTE, DEVINETTE, ARTISANAT, PROVERBE
 */
@RestController
@RequestMapping("/api/contenus")
@CrossOrigin(origins = "*")
@Tag(name = "📝 Création de Contenu", description = "Endpoints pour créer différents types de contenu culturel")
public class ContenuCreationController {

    private final ContenuCreationService contenuCreationService;
    private final MembreFamilleService membreFamilleService;

    public ContenuCreationController(ContenuCreationService contenuCreationService, MembreFamilleService membreFamilleService) {
        this.contenuCreationService = contenuCreationService;
        this.membreFamilleService = membreFamilleService;
    }

    /**
     * Vérifie si l'utilisateur a le droit de créer du contenu dans la famille.
     * Seuls les membres avec le rôle ADMIN ou EDITEUR peuvent créer du contenu.
     */
    private void verifierPermissionCreation(Long idFamille) {
        Long userId = AuthenticationHelper.getCurrentUserId();
        String roleMembre = membreFamilleService.getRoleMembre(userId, idFamille);

        if (roleMembre == null) {
            throw new RuntimeException("Vous n'êtes pas membre de cette famille");
        }

        if (!"ADMIN".equals(roleMembre) && !"EDITEUR".equals(roleMembre)) {
            throw new RuntimeException("Seuls les administrateurs et éditeurs peuvent créer du contenu");
        }
    }

    /**
     * Crée un nouveau contenu selon son type.
     * * Endpoint : POST /api/contenus/creer
     * * Types supportés :
     * - CONTE : titre, photo, fichier (PDF/TXT) ou texte
     * - ARTISANAT : titre, description, photos, vidéo (optionnelle)
     * - PROVERBE : titre, origine, signification, proverbe, photo
     * - DEVINETTE : titre, devinette, réponse, photo
     * * @param request Requête de création de contenu
     * @param authentication Authentification de l'utilisateur
     * @return Contenu créé
     */
    @PostMapping("/creer")
    @Operation(summary = "Créer un contenu générique", description = "Créer un contenu avec tous les champs possibles")
    public ResponseEntity<Contenu> creerContenu(
            @Valid @ModelAttribute CreationContenuRequest request,
            Authentication authentication) {

        verifierPermissionCreation(request.getIdFamille());
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        // La logique de blocage PDF/TXT est exécutée dans le ContenuCreationService
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

        verifierPermissionCreation(request.getIdFamille());
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        // La logique de blocage PDF/TXT est exécutée dans le ContenuCreationService
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

        verifierPermissionCreation(request.getIdFamille());
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        // La logique de blocage PDF/TXT est exécutée dans le ContenuCreationService
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

        verifierPermissionCreation(request.getIdFamille());
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        // La logique de blocage PDF/TXT est exécutée dans le ContenuCreationService
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

        verifierPermissionCreation(request.getIdFamille());
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        // La logique de blocage PDF/TXT est exécutée dans le ContenuCreationService
        Contenu contenu = contenuCreationService.creerDevinette(request, auteurId);
        return ResponseEntity.ok(contenu);
    }
}
