package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteJsonRequest {
    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private String texteConte;

    private String lieu;
    private String region;
}
