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
 * Entité représentant un quiz sur l'histoire familiale.
 */
@Entity
@Table(name = "quiz", indexes = {
    @Index(name = "idx_famille", columnList = "id_famille"),
    @Index(name = "idx_createur", columnList = "id_createur")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = true)
    private Famille famille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contenu", nullable = true)
    private Contenu contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "difficulte", nullable = false, length = 20)
    private String difficulte = "MOYEN";

    @Column(name = "temps_limite")
    private Integer tempsLimite;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionQuiz> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<ResultatQuiz> resultats = new ArrayList<>();

    // Méthodes manquantes pour compatibilité
    public String getContenu() {
        return this.description;
    }

    public String getTypeQuiz() {
        return "FAMILIAL"; // Par défaut, tous les quiz sont familiaux
    }

    public String getStatut() {
        return this.actif ? "ACTIF" : "INACTIF";
    }
}

