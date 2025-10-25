package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les quiz publics créés par le super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizPublicDTO {
    
    private Long id;
    private String titre;
    private String description;
    private String typeQuiz;
    private String statut;
    private LocalDateTime dateCreation;
    private String nomCreateur;
    private String prenomCreateur;
    private String titreContenu;
    private String nomFamille;
}
