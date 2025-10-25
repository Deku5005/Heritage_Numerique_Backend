package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour les contributions d'une famille avec statistiques par membre.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionsFamilleDTO {
    
    private Long idFamille;
    private String nomFamille;
    private String descriptionFamille;
    private String ethnieFamille;
    private String regionFamille;
    private Integer totalMembres;
    private Integer totalContributions;
    private Integer totalContes;
    private Integer totalProverbes;
    private Integer totalArtisanats;
    private Integer totalDevinettes;
    private List<ContributionMembreDTO> contributionsMembres = new ArrayList<>();
}
