package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter le résultat d'un quiz.
 * 
 * Utilisé pour retourner les informations de score et de performance
 * après qu'un utilisateur ait répondu à un quiz.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultatQuizDTO {
    
    /**
     * Identifiant du résultat
     */
    private Long id;
    
    /**
     * Identifiant du quiz
     */
    private Long idQuiz;
    
    /**
     * Titre du quiz
     */
    private String titreQuiz;
    
    /**
     * Identifiant de l'utilisateur
     */
    private Long idUtilisateur;
    
    /**
     * Nom de l'utilisateur
     */
    private String nomUtilisateur;
    
    /**
     * Prénom de l'utilisateur
     */
    private String prenomUtilisateur;
    
    /**
     * Email de l'utilisateur
     */
    private String emailUtilisateur;
    
    /**
     * Score obtenu par l'utilisateur
     */
    private Integer score;
    
    /**
     * Nombre total de questions
     */
    private Integer totalQuestions;
    
    /**
     * Temps écoulé en secondes
     */
    private Integer tempsEcoule;
    
    /**
     * Date de passage du quiz
     */
    private LocalDateTime datePassage;
    
    /**
     * Statut du quiz (TERMINE, EN_COURS, etc.)
     */
    private String statutQuiz;
    
    /**
     * Type de quiz (FAMILIAL, PUBLIC)
     */
    private String typeQuiz;
    
    /**
     * Identifiant du contenu associé
     */
    private Long idContenu;
    
    /**
     * Titre du contenu associé
     */
    private String titreContenu;
    
    /**
     * Identifiant de la famille
     */
    private Long idFamille;
    
    /**
     * Nom de la famille
     */
    private String nomFamille;
    
    /**
     * Message de félicitations ou d'encouragement
     */
    private String message;
    
    /**
     * Indique si le quiz a été réussi (score >= 70%)
     */
    private Boolean reussi;
    
    /**
     * Rang de l'utilisateur dans le quiz (si applicable)
     */
    private Integer rang;
    
    /**
     * Nombre total de participants au quiz
     */
    private Integer nombreParticipants;
}
