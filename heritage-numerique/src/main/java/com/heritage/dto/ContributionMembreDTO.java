package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques de contribution d'un membre de famille.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionMembreDTO {
    
    private Long idMembre;
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String roleFamille;
    private String lienParente;
    private Integer totalContributions;
    private Integer nombreContes;
    private Integer nombreProverbes;
    private Integer nombreArtisanats;
    private Integer nombreDevinettes;
    private String dateAjout;
}
