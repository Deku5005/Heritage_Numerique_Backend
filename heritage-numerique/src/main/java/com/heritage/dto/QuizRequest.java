package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer ou modifier un quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    private String difficulte; // FACILE, MOYEN, DIFFICILE
    private Integer tempsLimite;
    private Boolean actif;
}


