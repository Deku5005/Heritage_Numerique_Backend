package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour représenter tous les quiz d'une famille avec les informations des membres.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizFamilleDTO {
    
    private Long idFamille;
    private String nomFamille;
    private String descriptionFamille;
    private Integer nombreQuizTotal;
    private Integer nombreMembresAvecQuiz;
    private Integer scoreTotalFamille;
    private List<QuizMembreDTO> membresQuiz;
}
