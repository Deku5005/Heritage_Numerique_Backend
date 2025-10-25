package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les contenus récents dans le dashboard super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenuRecentDTO {
    
    private Long id;
    private String titre;
    private String typeContenu;
    private LocalDateTime dateCreation;
    private String nomCreateur;
    private String prenomCreateur;
    private String nomFamille;
}
