package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les familles récentes dans le dashboard super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilleRecenteDTO {
    
    private Long id;
    private String nom;
    private String description;
    private String ethnie;
    private String region;
    private LocalDateTime dateCreation;
    private String nomAdmin;
    private String prenomAdmin;
}
