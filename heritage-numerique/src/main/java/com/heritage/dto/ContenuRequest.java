package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour créer ou modifier un contenu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenuRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    private String description;

    @NotBlank(message = "Le type de contenu est obligatoire")
    private String typeContenu; // PHOTO, VIDEO, AUDIO, DOCUMENT, TEXTE

    private String urlFichier;
    private Long tailleFichier;
    private Integer duree;
    private LocalDate dateEvenement;
    private String lieu;
    private String region;
    private String statut; // BROUILLON, PUBLIE, ARCHIVE
}


