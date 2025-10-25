package com.heritage.controller;

import com.heritage.dto.CategorieDTO;
import com.heritage.dto.DemandePublicationDTO;
import com.heritage.dto.StatistiquesDTO;
import com.heritage.service.ContenuService;
import com.heritage.service.SuperAdminService;
import com.heritage.util.AuthenticationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour les fonctionnalités du super-admin.
 * 
 * Endpoints :
 * - GET /api/superadmin/statistiques : statistiques globales
 * - POST /api/superadmin/categories : créer une catégorie
 * - DELETE /api/superadmin/categories/{id} : supprimer une catégorie
 * - GET /api/superadmin/categories : lister les catégories
 * 
 * Sécurité : Tous les endpoints nécessitent le rôle ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/superadmin")
@CrossOrigin(origins = "*")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final ContenuService contenuService;

    public SuperAdminController(
            SuperAdminService superAdminService,
            ContenuService contenuService) {
        this.superAdminService = superAdminService;
        this.contenuService = contenuService;
    }

    /**
     * Récupère les statistiques globales de la plateforme.
     * Accessible uniquement par le super-admin (ROLE_ADMIN).
     * 
     * Statistiques :
     * - Nombre d'utilisateurs
     * - Nombre de familles
     * - Nombre de contenus (total et publics)
     * - Nombre de quiz
     * - Nombre de catégories
     * - Invitations en attente
     * - Notifications envoyées
     * 
     * @param authentication Authentification
     * @return Statistiques globales
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatistiquesDTO> getStatistiques(Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        StatistiquesDTO stats = superAdminService.getStatistiquesGlobales(adminId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Crée une nouvelle catégorie de contenu.
     * Seul le super-admin peut créer des catégories.
     * 
     * @param nom Nom de la catégorie
     * @param description Description
     * @param icone Icône
     * @param authentication Authentification
     * @return Catégorie créée
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorieDTO> createCategorie(
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icone,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        CategorieDTO categorie = superAdminService.createCategorie(nom, description, icone, adminId);
        return new ResponseEntity<>(categorie, HttpStatus.CREATED);
    }

    /**
     * Supprime une catégorie.
     * Attention : échouera si des contenus utilisent cette catégorie.
     * 
     * @param id ID de la catégorie
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategorie(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        superAdminService.deleteCategorie(id, adminId);
        return ResponseEntity.ok("Catégorie supprimée avec succès");
    }

    /**
     * Récupère toutes les catégories.
     * Accessible par tous les utilisateurs authentifiés.
     * 
     * @return Liste des catégories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        List<CategorieDTO> categories = superAdminService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupère toutes les demandes de publication en attente.
     * Seul le super-admin peut voir toutes les demandes.
     * 
     * @param authentication Authentification
     * @return Liste des demandes en attente
     */
    @GetMapping("/demandes-publication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemandePublicationDTO>> getDemandesPublicationEnAttente(
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        List<DemandePublicationDTO> demandes = superAdminService.getDemandesPublicationEnAttente(adminId);
        return ResponseEntity.ok(demandes);
    }

    /**
     * Valide une demande de publication (endpoint dans superadmin).
     * 
     * @param demandeId ID de la demande
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes-publication/{demandeId}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> validerPublication(
            @PathVariable Long demandeId,
            Authentication authentication) {
        
        Long valideurId = getUserIdFromAuth(authentication);
        contenuService.validerPublication(demandeId, valideurId);
        return ResponseEntity.ok("Contenu publié avec succès");
    }

    /**
     * Rejette une demande de publication.
     * 
     * @param demandeId ID de la demande
     * @param commentaire Raison du rejet
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes-publication/{demandeId}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejeterPublication(
            @PathVariable Long demandeId,
            @RequestParam String commentaire,
            Authentication authentication) {
        
        Long valideurId = getUserIdFromAuth(authentication);
        contenuService.rejeterPublication(demandeId, valideurId, commentaire);
        return ResponseEntity.ok("Demande rejetée");
    }

    /**
     * Récupère l'ID de l'utilisateur depuis l'authentification.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
}

