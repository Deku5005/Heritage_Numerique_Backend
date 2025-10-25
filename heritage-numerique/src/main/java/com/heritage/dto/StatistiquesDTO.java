package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les statistiques globales du super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesDTO {

    private Long nombreUtilisateurs;
    private Long nombreFamilles;
    private Long nombreContenus;
    private Long nombreContenusPublics;
    private Long nombreQuiz;
    private Long nombreCategories;
    private Long nombreInvitationsEnAttente;
    private Long nombreNotificationsEnvoyees;
}

