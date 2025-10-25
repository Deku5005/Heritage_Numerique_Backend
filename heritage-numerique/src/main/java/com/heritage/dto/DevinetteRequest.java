package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO pour la création d'une devinette.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevinetteRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @NotBlank(message = "La devinette est obligatoire")
    @Size(max = 500, message = "La devinette ne peut pas dépasser 500 caractères")
    private String texteDevinette; // La devinette

    @NotBlank(message = "La réponse est obligatoire")
    @Size(max = 200, message = "La réponse ne peut pas dépasser 200 caractères")
    private String reponseDevinette; // La réponse/signification

    private MultipartFile photoDevinette; // Photo de la devinette

    private String lieu;
    private String region;
}
