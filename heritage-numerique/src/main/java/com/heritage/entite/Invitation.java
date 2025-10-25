package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une invitation à rejoindre une famille.
 * 
 * Sécurité :
 * - Le code d'invitation est unique et alphanumérique (8 caractères)
 * - L'email invité est stocké pour validation lors de l'inscription
 * - Les invitations expirent après 30 jours (voir InvitationService.expireOldInvitations())
 * - Le statut passe à ACCEPTEE lors de l'inscription réussie
 */
@Entity
@Table(name = "invitation", indexes = {
    @Index(name = "idx_code_invitation", columnList = "code_invitation"),
    @Index(name = "idx_email_invite", columnList = "email_invite"),
    @Index(name = "idx_statut", columnList = "statut"),
    @Index(name = "idx_famille", columnList = "id_famille")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = false)
    private Famille famille;

    /**
     * Utilisateur qui a émis l'invitation (doit être admin de la famille).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emetteur", nullable = false)
    private Utilisateur emetteur;

    /**
     * Utilisateur destinataire de l'invitation.
     * NULL au moment de la création (si l'utilisateur n'existe pas encore).
     * Rempli automatiquement lors de l'inscription avec le code.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur_invite")
    private Utilisateur utilisateurInvite;

    /**
     * Nom de la personne invitée.
     */
    @Column(name = "nom_invite", length = 100)
    private String nomInvite;

    /**
     * Email de la personne invitée.
     * Lors de l'inscription, on vérifie que l'email d'inscription correspond
     * pour garantir que l'invitation n'est pas utilisée par une autre personne.
     */
    @Column(name = "email_invite", nullable = false, length = 255)
    private String emailInvite;

    /**
     * Numéro de téléphone de la personne invitée.
     */
    @Column(name = "telephone_invite", length = 20)
    private String telephoneInvite;

    /**
     * Lien de parenté avec la famille.
     * Exemples : "Fils", "Fille", "Frère", "Sœur", "Cousin", "Cousine", "Oncle", "Tante", etc.
     */
    @Column(name = "lien_parente", length = 50)
    private String lienParente;

    /**
     * Code d'invitation unique (8 caractères alphanumériques).
     * Généré lors de la création de l'invitation.
     */
    @Column(name = "code_invitation", nullable = false, unique = true, length = 8)
    private String codeInvitation;

    /**
     * Statut de l'invitation :
     * EN_ATTENTE : invitation créée, en attente d'utilisation
     * ACCEPTEE : utilisée lors d'une inscription réussie
     * REFUSEE : refusée explicitement
     * EXPIREE : expirée (>30 jours)
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "EN_ATTENTE";

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Date d'expiration (48 heures après création).
     * Calculée automatiquement lors de la création.
     */
    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    /**
     * Date d'utilisation (acceptation ou refus).
     */
    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;
}

