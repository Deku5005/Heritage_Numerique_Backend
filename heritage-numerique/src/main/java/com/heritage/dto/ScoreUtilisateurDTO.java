package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour représenter le score d'un utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUtilisateurDTO {
    
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private Integer scoreTotal;
    private Integer nombreQuizReussis;
    private Integer nombreQuizFamiliaux;
    private Integer nombreQuizPublics;
}
