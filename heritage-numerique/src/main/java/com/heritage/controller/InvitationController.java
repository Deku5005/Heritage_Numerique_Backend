package com.heritage.controller;

import com.heritage.dto.InvitationDTO;
import com.heritage.dto.InvitationRequest;
import com.heritage.service.InvitationService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des invitations.
 * 
 * Endpoints protégés (authentification requise).
 */
@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Crée une nouvelle invitation pour rejoindre une famille.
     * 
     * URL : POST /api/invitations
     * Headers : Authorization: Bearer <token>
     * 
     * Body JSON :
     * {
     *   "idFamille": 1,
     *   "emailInvite": "nouvel.utilisateur@example.com"
     * }
     * 
     * Processus :
     * 1. Vérifier que l'utilisateur connecté est admin de la famille (TODO)
     * 2. Générer un code d'invitation unique (8 caractères alphanumériques)
     * 3. Définir la date d'expiration (30 jours)
     * 4. Sauvegarder l'invitation
     * 
     * Réponse :
     * {
     *   "id": 1,
     *   "idFamille": 1,
     *   "nomFamille": "Famille Dupont",
     *   "emailInvite": "nouvel.utilisateur@example.com",
     *   "codeInvitation": "ABC12345",
     *   "statut": "EN_ATTENTE",
     *   "dateCreation": "2024-01-15T10:30:00",
     *   "dateExpiration": "2024-02-14T10:30:00"
     * }
     * 
     * @param request Requête de création d'invitation
     * @return Invitation créée
     */
    @PostMapping
    public ResponseEntity<InvitationDTO> createInvitation(@Valid @RequestBody InvitationRequest request) {
        Long emetteurId = getCurrentUserId();
        InvitationDTO invitation = invitationService.createInvitation(request, emetteurId);
        return new ResponseEntity<>(invitation, HttpStatus.CREATED);
    }

    /**
     * Récupère toutes les invitations d'une famille.
     * 
     * URL : GET /api/invitations/famille/{familleId}
     * Headers : Authorization: Bearer <token>
     * 
     * @param familleId ID de la famille
     * @return Liste des invitations
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<InvitationDTO>> getInvitationsByFamille(@PathVariable Long familleId) {
        // TODO: Vérifier que l'utilisateur a accès à cette famille
        List<InvitationDTO> invitations = invitationService.getInvitationsByFamille(familleId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * Récupère toutes les invitations envoyées par l'utilisateur connecté.
     * 
     * URL : GET /api/invitations/mes-invitations
     * Headers : Authorization: Bearer <token>
     * 
     * @return Liste des invitations
     */
    @GetMapping("/mes-invitations")
    public ResponseEntity<List<InvitationDTO>> getMesInvitations() {
        Long emetteurId = getCurrentUserId();
        List<InvitationDTO> invitations = invitationService.getInvitationsByEmetteur(emetteurId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * Accepte une invitation.
     * L'utilisateur accepte formellement de rejoindre la famille.
     * 
     * URL : POST /api/invitations/{id}/accepter
     * Headers : Authorization: Bearer <token>
     * 
     * Workflow :
     * 1. Vérifier que l'invitation existe et est EN_ATTENTE
     * 2. Vérifier que l'email de l'utilisateur correspond
     * 3. Mettre à jour le statut à ACCEPTEE
     * 4. Activer l'appartenance à la famille
     * 5. Notifier l'admin de la famille
     * 
     * @param id ID de l'invitation
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<String> accepterInvitation(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long utilisateurId = getCurrentUserId(authentication);
        invitationService.accepterInvitation(id, utilisateurId);
        return ResponseEntity.ok("Invitation acceptée avec succès");
    }

    /**
     * Refuse une invitation.
     * L'utilisateur refuse de rejoindre la famille.
     * 
     * URL : POST /api/invitations/{id}/refuser
     * Headers : Authorization: Bearer <token>
     * 
     * Workflow :
     * 1. Vérifier que l'invitation existe et est EN_ATTENTE
     * 2. Vérifier que l'email de l'utilisateur correspond
     * 3. Mettre à jour le statut à REFUSEE
     * 4. Supprimer le lien membre_famille
     * 5. Notifier l'admin de la famille
     * 
     * @param id ID de l'invitation
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<String> refuserInvitation(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long utilisateurId = getCurrentUserId(authentication);
        invitationService.refuserInvitation(id, utilisateurId);
        return ResponseEntity.ok("Invitation refusée");
    }

    /**
     * Récupère l'ID de l'utilisateur connecté depuis l'Authentication.
     * Utilise AuthenticationHelper pour extraire l'ID du UserPrincipal.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getCurrentUserId(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
    
    /**
     * Récupère l'ID de l'utilisateur connecté depuis le SecurityContext.
     * 
     * @return ID de l'utilisateur
     */
    private Long getCurrentUserId() {
        return AuthenticationHelper.getCurrentUserId();
    }
}

