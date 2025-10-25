package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les informations d'une famille.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilleDTO {

    private Long id;
    private String nom;
    private String description;
    private String ethnie;
    private String region;
    private Long idCreateur;
    private String nomCreateur;
    private String nomAdmin; // Nom de l'admin actuel de la famille
    private LocalDateTime dateCreation;
    private Integer nombreMembres;
}


