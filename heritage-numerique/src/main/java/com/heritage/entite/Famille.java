package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une famille/groupe familial.
 * Une famille regroupe plusieurs utilisateurs via la table membre_famille.
 */
@Entity
@Table(name = "famille", indexes = {
    @Index(name = "idx_createur", columnList = "id_createur")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Famille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ethnie", length = 100)
    private String ethnie;

    @Column(name = "region", length = 100)
    private String region;

    /**
     * Utilisateur qui a créé cette famille.
     * Il devient automatiquement admin de la famille.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    /**
     * Membres de cette famille (relation N:N avec Utilisateur).
     */
    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembreFamille> membres = new ArrayList<>();

    /**
     * Invitations envoyées pour cette famille.
     */
    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL)
    private List<Invitation> invitations = new ArrayList<>();

    /**
     * Contenus de cette famille.
     */
    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL)
    private List<Contenu> contenus = new ArrayList<>();

    /**
     * Quiz de cette famille.
     */
    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL)
    private List<Quiz> quiz = new ArrayList<>();

    /**
     * Arbres généalogiques de cette famille.
     */
    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL)
    private List<ArbreGenealogique> arbresGenealogiques = new ArrayList<>();
}

