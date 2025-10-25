package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour créer une question de quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionQuizRequest {

    @NotBlank(message = "La question est obligatoire")
    @Size(max = 500, message = "La question ne peut pas dépasser 500 caractères")
    private String question;

    @NotNull(message = "Le type de réponse est obligatoire")
    private TypeReponse typeReponse; // QCM ou VRAI_FAUX

    private List<PropositionRequest> propositions; // Pour QCM uniquement

    private Boolean reponseVraiFaux; // Pour VRAI_FAUX uniquement
}
