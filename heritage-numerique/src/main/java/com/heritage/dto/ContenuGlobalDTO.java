package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les contenus globaux dans la vue super-admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenuGlobalDTO {
    
    private Long id;
    private String titre;
    private String description;
    private String typeContenu;
    private String statut;
    private LocalDateTime dateCreation;
    private String nomCreateur;
    private String prenomCreateur;
    private String emailCreateur;
    private String nomFamille;
    private String regionFamille;
    private String texteProverbe;
    private String significationProverbe;
    private String origineProverbe;
}
