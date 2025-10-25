package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une proposition de réponse QCM.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropositionRequest {

    @NotBlank(message = "Le texte de la proposition est obligatoire")
    @Size(max = 200, message = "Le texte de la proposition ne peut pas dépasser 200 caractères")
    private String texte;

    @NotNull(message = "L'indication si c'est la bonne réponse est obligatoire")
    private Boolean estCorrecte;

    private Integer ordre;

    // Méthodes manquantes pour compatibilité
    public Long getIdQuestion() {
        return null; // Cette méthode n'est pas utilisée dans ce contexte
    }
}