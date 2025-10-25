package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour le dashboard complet du super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminDashboardDTO {
    
    private Long nombreUtilisateurs;
    private Long nombreFamilles;
    private Long nombreContes;
    private Long nombreArtisanats;
    private Long nombreProverbes;
    private Long nombreDevinettes;
    private Long nombreQuizPublics;
    private List<ContenuRecentDTO> contenusRecents;
    private List<FamilleRecenteDTO> famillesRecentes;
}
