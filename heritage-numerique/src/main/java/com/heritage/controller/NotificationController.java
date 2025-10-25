package com.heritage.controller;

import com.heritage.dto.NotificationDTO;
import com.heritage.entite.Notification;
import com.heritage.service.NotificationService;
import com.heritage.util.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des notifications.
 * 
 * Endpoints :
 * - GET /api/notifications : récupérer toutes les notifications
 * - GET /api/notifications/non-lues : récupérer les notifications non lues
 * - PUT /api/notifications/{id}/lire : marquer comme lue
 * - GET /api/notifications/count : compter les non lues
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Récupère toutes les notifications de l'utilisateur connecté.
     * 
     * @param authentication Authentification
     * @return Liste des notifications
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        List<Notification> notifications = notificationService.getNotifications(utilisateurId);
        
        List<NotificationDTO> dtos = notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupère les notifications non lues de l'utilisateur.
     * 
     * @param authentication Authentification
     * @return Liste des notifications non lues
     */
    @GetMapping("/non-lues")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues(Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        List<Notification> notifications = notificationService.getNotificationsNonLues(utilisateurId);
        
        List<NotificationDTO> dtos = notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Marque une notification comme lue.
     * 
     * @param id ID de la notification
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PutMapping("/{id}/lire")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<String> marquerCommeLue(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        notificationService.marquerCommeLue(id, utilisateurId);
        return ResponseEntity.ok("Notification marquée comme lue");
    }

    /**
     * Compte le nombre de notifications non lues.
     * 
     * @param authentication Authentification
     * @return Nombre de notifications non lues
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Long> compterNotificationsNonLues(Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        Long count = notificationService.compterNotificationsNonLues(utilisateurId);
        return ResponseEntity.ok(count);
    }

    /**
     * Convertit une entité Notification en DTO.
     * 
     * @param notification Entité à convertir
     * @return DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .titre(notification.getTitre())
                .message(notification.getMessage())
                .canal(notification.getCanal())
                .lu(notification.getLu())
                .dateEnvoi(notification.getDateEnvoi())
                .dateLecture(notification.getDateLecture())
                .lien(notification.getLien())
                .build();
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

