package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une notification envoyée à un utilisateur.
 * 
 * Types de notifications :
 * - INVITATION : Invitation à rejoindre une famille
 * - ACCEPTATION : Acceptation d'une invitation
 * - CONTENU_PUBLIE : Contenu rendu public
 * - QUIZ_CREE : Nouveau quiz créé
 * - DEMANDE_PUBLICATION : Demande de publication d'un contenu
 * 
 * Canaux de notification :
 * - EMAIL : Notification par email
 * - SMS : Notification par SMS
 * - IN_APP : Notification dans l'application
 */
@Entity
@Table(name = "notification", indexes = {
    @Index(name = "idx_destinataire", columnList = "id_destinataire"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_lu", columnList = "lu"),
    @Index(name = "idx_date_envoi", columnList = "date_envoi")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire", nullable = false)
    private Utilisateur destinataire;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // INVITATION, ACCEPTATION, CONTENU_PUBLIE, QUIZ_CREE, etc.

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "canal", nullable = false, length = 20)
    private String canal; // EMAIL, SMS, IN_APP

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    @CreationTimestamp
    @Column(name = "date_envoi", nullable = false, updatable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Column(name = "lien", length = 500)
    private String lien; // Lien vers la ressource concernée

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON avec métadonnées supplémentaires
}

