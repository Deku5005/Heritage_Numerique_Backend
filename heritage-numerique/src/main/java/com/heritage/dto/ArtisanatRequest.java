package com.heritage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DTO pour la création d'un artisanat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanatRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @Schema(description = "Photo de l'artisanat", type = "string", format = "binary")
    private MultipartFile photoArtisanat; // Photo de l'artisanat
    
    @Schema(description = "Vidéo de l'artisanat", type = "string", format = "binary")
    private MultipartFile videoArtisanat; // Vidéo optionnelle

    private String lieu;
    private String region;
}
