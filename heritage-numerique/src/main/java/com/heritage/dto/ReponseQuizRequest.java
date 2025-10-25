package com.heritage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour répondre à un quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseQuizRequest {

    @NotNull(message = "L'ID du quiz est obligatoire")
    private Long idQuiz;

    @NotNull(message = "Les réponses sont obligatoires")
    private List<ReponseQuestionRequest> reponses;

    private Integer tempsEcoule;
}