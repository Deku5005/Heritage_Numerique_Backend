package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une proposition de réponse pour une question de quiz.
 */
@Entity
@Table(name = "proposition", indexes = {
    @Index(name = "idx_question", columnList = "id_question")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", nullable = false)
    private QuestionQuiz question;

    @Column(name = "texte_proposition", nullable = false, columnDefinition = "TEXT")
    private String texteProposition;

    @Column(name = "est_correcte", nullable = false)
    private Boolean estCorrecte = false;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Méthodes manquantes pour compatibilité
    public String getTexte() {
        return this.texteProposition;
    }

    public void setTexte(String texte) {
        this.texteProposition = texte;
    }
}

