package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter un artisanat avec ses informations détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanatDTO {
    
    private Long id;
    private String titre;
    private String description;
    private String nomAuteur;
    private String prenomAuteur;
    private String emailAuteur;
    private String roleAuteur; // ADMIN, EDITEUR, LECTEUR
    private String lienParenteAuteur;
    private LocalDateTime dateCreation;
    private String statut; // BROUILLON, PUBLIE, EN_ATTENTE_VALIDATION
    private List<String> urlPhotos; // URLs des photos de l'artisanat
    private String urlVideo; // URL de la vidéo (optionnelle)
    private String lieu;
    private String region;
    private Long idFamille;
    private String nomFamille;
}
