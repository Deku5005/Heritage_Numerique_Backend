package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les informations d'un quiz.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {

    private Long id;
    private Long idFamille;
    private String nomFamille;
    private Long idCreateur;
    private String nomCreateur;
    private String titre;
    private String description;
    private String difficulte;
    private Integer tempsLimite;
    private Boolean actif;
    private Integer nombreQuestions;
    private LocalDateTime dateCreation;
    private List<QuestionQuizDTO> questions;

    // Méthodes manquantes pour compatibilité
    public void setQuiz(QuizDTO quiz) {
        // Cette méthode n'est pas utilisée dans ce contexte
    }
}


