package com.heritage.controller;

import com.heritage.dto.ContenuDTO;
import com.heritage.dto.ContenuRequest;
import com.heritage.dto.DemandePublicationDTO;
import com.heritage.service.ContenuService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des contenus.
 * 
 * Endpoints :
 * - POST /api/contenus : créer un contenu privé (EDITEUR ou ADMIN)
 * - POST /api/contenus/{id}/demander-publication : demander la publication (ADMIN famille uniquement)
 * - POST /api/contenus/demandes/{demandeId}/valider : valider publication (SUPERADMIN uniquement)
 * - POST /api/contenus/demandes/{demandeId}/rejeter : rejeter publication (SUPERADMIN uniquement)
 * - GET /api/contenus/publics : récupérer les contenus publics (tous)
 * - GET /api/contenus/famille/{familleId} : récupérer les contenus privés (membres uniquement)
 * - GET /api/contenus/demandes/famille/{familleId} : récupérer toutes les demandes de publication d'une famille (membres uniquement)
 */
@RestController
@RequestMapping("/api/contenus")
@CrossOrigin(origins = "*")
public class ContenuController {

    private final ContenuService contenuService;

    public ContenuController(ContenuService contenuService) {
        this.contenuService = contenuService;
    }

    /**
     * Crée un nouveau contenu privé pour une famille.
     * Les membres EDITEUR et ADMIN peuvent créer des contenus.
     * Les LECTEUR peuvent seulement consulter.
     * 
     * @param request Requête de création
     * @param authentication Authentification Spring Security
     * @return Contenu créé
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<ContenuDTO> createContenu(
            @Valid @RequestBody ContenuRequest request,
            Authentication authentication) {
        
        Long auteurId = getUserIdFromAuth(authentication);
        ContenuDTO contenu = contenuService.createContenu(request, auteurId);
        return new ResponseEntity<>(contenu, HttpStatus.CREATED);
    }

    /**
     * Demande la publication publique d'un contenu.
     * Seul l'ADMIN de la famille peut demander la publication.
     * Le SUPERADMIN devra ensuite valider pour rendre le contenu public.
     * 
     * @param contenuId ID du contenu
     * @param authentication Authentification
     * @return DemandePublicationDTO avec le statut de la demande (EN_ATTENTE, APPROUVEE, REJETEE)
     */
    @PostMapping("/{contenuId}/demander-publication")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<DemandePublicationDTO> demanderPublication(
            @PathVariable Long contenuId,
            Authentication authentication) {
        
        Long demandeurId = getUserIdFromAuth(authentication);
        DemandePublicationDTO demande = contenuService.demanderPublication(contenuId, demandeurId);
        return ResponseEntity.ok(demande);
    }

    /**
     * Valide une demande de publication et rend le contenu public.
     * Seul le SUPERADMIN peut valider.
     * 
     * @param demandeId ID de la demande
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes/{demandeId}/valider")
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
     * Seul le SUPERADMIN peut rejeter.
     * 
     * @param demandeId ID de la demande
     * @param commentaire Raison du rejet
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes/{demandeId}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejeterPublication(
            @PathVariable Long demandeId,
            @RequestParam String commentaire,
            Authentication authentication) {
        
        Long valideurId = getUserIdFromAuth(authentication);
        contenuService.rejeterPublication(demandeId, valideurId, commentaire);
        return ResponseEntity.ok("Demande de publication rejetée");
    }

    /**
     * Récupère tous les contenus publics.
     * Accessible par tous (même non authentifiés potentiellement).
     * 
     * @return Liste des contenus publics
     */
    @GetMapping("/publics")
    public ResponseEntity<List<ContenuDTO>> getContenusPublics() {
        List<ContenuDTO> contenus = contenuService.getContenusPublics();
        return ResponseEntity.ok(contenus);
    }

    /**
     * Récupère les contenus privés d'une famille.
     * Accessible uniquement par les membres de la famille.
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification
     * @return Liste des contenus privés
     */
    @GetMapping("/famille/{familleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<ContenuDTO>> getContenusPrivesFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        List<ContenuDTO> contenus = contenuService.getContenusPrivesFamille(familleId, utilisateurId);
        return ResponseEntity.ok(contenus);
    }

    /**
     * Récupère toutes les demandes de publication d'une famille.
     * Accessible uniquement par les membres de la famille.
     * Permet de voir l'historique complet des demandes (EN_ATTENTE, APPROUVEE, REJETEE).
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification
     * @return Liste des demandes de publication avec leurs statuts
     */
    @GetMapping("/demandes/famille/{familleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<DemandePublicationDTO>> getDemandesPublicationFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        List<DemandePublicationDTO> demandes = contenuService.getDemandesPublicationFamille(familleId, utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    /**
     * Récupère l'ID de l'utilisateur depuis l'authentification.
     * Utilise AuthenticationHelper pour extraire l'ID du UserPrincipal.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
}

