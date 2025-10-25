package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un proverbe avec ses informations détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProverbeDTO {
    
    private Long id;
    private String titre;
    private String proverbe; // Le proverbe lui-même
    private String signification; // La signification du proverbe
    private String origine; // L'origine du proverbe
    private String nomAuteur;
    private String prenomAuteur;
    private String emailAuteur;
    private String roleAuteur; // ADMIN, EDITEUR, LECTEUR
    private String lienParenteAuteur;
    private LocalDateTime dateCreation;
    private String statut; // BROUILLON, PUBLIE, EN_ATTENTE_VALIDATION
    private String urlPhoto; // URL de la photo du proverbe
    private String lieu;
    private String region;
    private Long idFamille;
    private String nomFamille;
}
