package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour représenter les quiz d'un membre de famille avec ses informations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizMembreDTO {
    
    private Long idMembre;
    private String nomMembre;
    private String prenomMembre;
    private String emailMembre;
    private String roleMembre;
    private String lienParenteMembre;
    private Integer nombreQuizTotal;
    private Integer nombreQuizTermines;
    private Integer nombreQuizEnCours;
    private Integer scoreTotalMembre;
    private List<QuizProgressionDTO> quizProgression;
}
