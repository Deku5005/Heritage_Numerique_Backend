package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer ou modifier une question de quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotNull(message = "L'ID du quiz est obligatoire")
    private Long idQuiz;

    @NotBlank(message = "Le texte de la question est obligatoire")
    @Size(max = 2000, message = "Le texte de la question ne peut pas dépasser 2000 caractères")
    private String texteQuestion;

    @Size(max = 20, message = "Le type de question ne peut pas dépasser 20 caractères")
    private String typeQuestion = "QCM"; // QCM, VRAI_FAUX, LIBRE

    @NotNull(message = "L'ordre est obligatoire")
    private Integer ordre;

    @NotNull(message = "Le nombre de points est obligatoire")
    private Integer points = 1;
}
