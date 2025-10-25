package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un quiz avec les informations de progression d'un membre.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizProgressionDTO {
    
    private Long idQuiz;
    private String titreQuiz;
    private String descriptionQuiz;
    private String nomConte;
    private String descriptionConte;
    private String nomAuteurConte;
    private String prenomAuteurConte;
    private Integer nombreQuestionsTotal;
    private Integer nombreQuestionsRepondues;
    private Integer scoreActuel;
    private LocalDateTime dateDerniereReponse;
    private String statutQuiz; // EN_COURS, TERMINE, NON_COMMENCE
    private String typeQuiz; // FAMILIAL, PUBLIC
    private Long idContenu;
    private Long idFamille;
    private String nomFamille;
}
