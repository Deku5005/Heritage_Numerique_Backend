package com.heritage.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour créer un quiz sur un contenu (conte).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizContenuRequest {

    @NotNull(message = "L'ID du contenu est obligatoire")
    private Long idContenu;

    @NotBlank(message = "Le titre du quiz est obligatoire")
    @Size(max = 200, message = "Le titre du quiz ne peut pas dépasser 200 caractères")
    private String titre;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "Les questions sont obligatoires")
    @Size(min = 1, message = "Au moins une question est requise")
    private List<QuestionQuizRequest> questions;
}
