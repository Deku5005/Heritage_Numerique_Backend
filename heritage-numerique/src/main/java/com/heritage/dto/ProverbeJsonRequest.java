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
public class ProverbeJsonRequest {
    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @NotBlank(message = "L'origine du proverbe est obligatoire")
    @Size(max = 255, message = "L'origine ne peut pas dépasser 255 caractères")
    private String origineProverbe;

    @NotBlank(message = "La signification du proverbe est obligatoire")
    @Size(max = 1000, message = "La signification ne peut pas dépasser 1000 caractères")
    private String significationProverbe;

    @NotBlank(message = "Le texte du proverbe est obligatoire")
    @Size(max = 2000, message = "Le texte du proverbe ne peut pas dépasser 2000 caractères")
    private String texteProverbe;

    private String lieu;
    private String region;
}
