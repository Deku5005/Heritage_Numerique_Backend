package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter un contenu publique avec son quiz et ses questions.
 * Utilisé pour afficher un conte avec son quiz complet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenuAvecQuizDTO {

    // Informations du contenu
    private Long id;
    private Long idFamille;
    private String nomFamille;
    private Long idAuteur;
    private String nomAuteur;
    private Long idCategorie;
    private String nomCategorie;
    private String titre;
    private String description;
    private String typeContenu;
    private String urlFichier;
    private String urlPhoto;
    private Long tailleFichier;
    private Integer duree;
    private LocalDate dateEvenement;
    private String lieu;
    private String region;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Informations du quiz (si disponible)
    private QuizAvecQuestionsDTO quiz;

    /**
     * DTO interne pour le quiz avec ses questions.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAvecQuestionsDTO {
        private Long id;
        private String titre;
        private String description;
        private String difficulte;
        private Integer tempsLimite;
        private Boolean actif;
        private Integer nombreQuestions;
        private LocalDateTime dateCreation;
        private List<QuestionAvecPropositionsDTO> questions;
    }

    /**
     * DTO interne pour une question avec ses propositions.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAvecPropositionsDTO {
        private Long id;
        private String texteQuestion;
        private String typeQuestion;
        private Integer ordre;
        private Integer points;
        private List<PropositionDTO> propositions;
    }

    /**
     * DTO interne pour une proposition de réponse.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropositionDTO {
        private Long id;
        private String texteProposition;
        private Boolean estCorrecte;
        private Integer ordre;
    }
}

