package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanatJsonRequest {
    private Long idFamille; // Optionnel pour contenus publics

    private Long idCategorie; // Optionnel, sera cherché par nom

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    private MultipartFile photoArtisanat;
    private MultipartFile videoArtisanat;

    private String lieu;
    private String region;
}
