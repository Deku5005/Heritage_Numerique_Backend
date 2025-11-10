package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour les informations d'un contenu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenuDTO {

    private Long id;
    private Long idFamille;
    private Long idAuteur;
    private String nomAuteur;
    private Long idCategorie;
    private String nomCategorie;
    private String titre;
    private String description;
    private String typeContenu;
    private String urlFichier;
    private String urlPhoto;
    private Long tailleFichier;
    private Integer duree;
    private LocalDate dateEvenement;
    private String lieu;
    private String region;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String texteProverbe;
    private String significationProverbe;
    private String origineProverbe;
}


