package com.heritage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO pour la création d'un conte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @Schema(description = "Texte du conte si pas de fichier", type = "string")
    private String texteConte; // Texte du conte si pas de fichier
    
    @Schema(description = "Fichier PDF ou TXT du conte", type = "string", format = "binary")
    private MultipartFile fichierConte; // Fichier PDF ou TXT du conte
    
    @Schema(description = "Photo du conte", type = "string", format = "binary")
    private MultipartFile photoConte; // Photo du conte

    private String lieu;
    private String region;
}
