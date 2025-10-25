package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter une devinette avec ses informations détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevinetteDTO {
    
    private Long id;
    private String titre;
    private String devinette; // Le texte de la devinette
    private String reponse; // La réponse/signification
    private String nomAuteur;
    private String prenomAuteur;
    private String emailAuteur;
    private String roleAuteur; // ADMIN, EDITEUR, LECTEUR
    private String lienParenteAuteur;
    private LocalDateTime dateCreation;
    private String statut; // BROUILLON, PUBLIE, EN_ATTENTE_VALIDATION
    private String urlPhoto; // URL de la photo de la devinette
    private String lieu;
    private String region;
    private Long idFamille;
    private String nomFamille;
}
