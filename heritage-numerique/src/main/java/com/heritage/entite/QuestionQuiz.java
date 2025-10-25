package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une question d'un quiz.
 */
@Entity
@Table(name = "question_quiz", indexes = {
    @Index(name = "idx_quiz", columnList = "id_quiz"),
    @Index(name = "idx_ordre", columnList = "ordre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz", nullable = false)
    private Quiz quiz;

    @Column(name = "texte_question", nullable = false, columnDefinition = "TEXT")
    private String texteQuestion;

    @Column(name = "type_question", nullable = false, length = 20)
    private String typeQuestion = "QCM";

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

    @Column(name = "points", nullable = false)
    private Integer points = 1;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposition> propositions = new ArrayList<>();

    // Méthodes manquantes pour compatibilité
    public String getTypeReponse() {
        return this.typeQuestion;
    }

    public String getQuestion() {
        return this.texteQuestion;
    }
}

