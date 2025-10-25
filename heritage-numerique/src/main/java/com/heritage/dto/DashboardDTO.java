package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour le tableau de bord familial.
 * Affiche les statistiques d'une famille.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private Long idFamille;
    private String nomFamille;
    private Integer nombreMembres;
    private Integer nombreInvitationsEnAttente;
    private Integer nombreContenusPrives;
    private Integer nombreContenusPublics;
    private Integer nombreQuizActifs;
    private Integer nombreNotificationsNonLues;
    private Integer nombreArbreGenealogiques;
}

