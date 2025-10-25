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
 * Entité représentant un arbre généalogique d'une famille.
 */
@Entity
@Table(name = "arbre_genealogique", indexes = {
    @Index(name = "idx_famille", columnList = "id_famille")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArbreGenealogique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = false)
    private Famille famille;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "arbre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembreArbre> membres = new ArrayList<>();
}

