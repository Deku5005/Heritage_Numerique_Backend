package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant le résultat d'un quiz passé par un utilisateur.
 */
@Entity
@Table(name = "resultat_quiz", indexes = {
    @Index(name = "idx_quiz", columnList = "id_quiz"),
    @Index(name = "idx_utilisateur", columnList = "id_utilisateur"),
    @Index(name = "idx_date_passage", columnList = "date_passage")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultatQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "score_max", nullable = false)
    private Integer scoreMax;

    @Column(name = "temps_ecoule")
    private Integer tempsEcoule;

    @CreationTimestamp
    @Column(name = "date_passage", nullable = false, updatable = false)
    private LocalDateTime datePassage;

    // Méthodes manquantes pour compatibilité
    public void setTotalQuestions(int total) {
        this.scoreMax = total;
    }

    public int getTotalQuestions() {
        return this.scoreMax;
    }
}

