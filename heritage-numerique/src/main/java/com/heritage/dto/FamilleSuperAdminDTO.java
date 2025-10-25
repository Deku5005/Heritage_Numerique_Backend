package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les familles dans la vue super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilleSuperAdminDTO {
    
    private Long id;
    private String nom;
    private String description;
    private String ethnie;
    private String region;
    private LocalDateTime dateCreation;
    private String nomAdmin;
    private String prenomAdmin;
    private String emailAdmin;
    private Long nombreMembres;
}
