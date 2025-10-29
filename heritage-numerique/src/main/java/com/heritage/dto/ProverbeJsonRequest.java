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
public class ProverbeJsonRequest {
    private Long idFamille; // Optionnel pour contenus publics

    private Long idCategorie; // Optionnel, sera cherché par nom

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
    private MultipartFile photoProverbe;

    private String lieu;
    private String region;
}
