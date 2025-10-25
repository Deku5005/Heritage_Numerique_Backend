package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant l'appartenance d'un utilisateur à une famille.
 * Table de liaison entre Utilisateur et Famille (relation N:N).
 * Chaque membre a un rôle spécifique au sein de la famille.
 */
@Entity
@Table(name = "membre_famille", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_membre_famille", columnNames = {"id_utilisateur", "id_famille"})
    },
    indexes = {
        @Index(name = "idx_famille", columnList = "id_famille"),
        @Index(name = "idx_utilisateur", columnList = "id_utilisateur")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembreFamille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = false)
    private Famille famille;

    /**
     * Rôle de l'utilisateur au sein de cette famille spécifique.
     * ADMIN : peut gérer la famille et inviter des membres
     * EDITEUR : peut créer du contenu
     * LECTEUR : peut uniquement consulter
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role_famille", nullable = false, length = 20)
    private RoleFamille roleFamille = RoleFamille.LECTEUR;

    /**
     * Lien de parenté de l'utilisateur au sein de la famille.
     * Exemples : "Père", "Mère", "Fils", "Fille", "Grand-père", "Grand-mère", "Oncle", "Tante", "Cousin", "Cousine", etc.
     */
    @Column(name = "lien_parente", length = 50)
    private String lienParente;

    @CreationTimestamp
    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;
}

