package com.heritage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour répondre à une question de quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseQuestionRequest {

    @NotNull(message = "L'ID de la question est obligatoire")
    private Long idQuestion;

    private Long idProposition; // Pour QCM
    private Boolean reponseVraiFaux; // Pour VRAI_FAUX
}
