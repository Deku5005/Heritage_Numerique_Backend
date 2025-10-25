package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO pour la création d'un proverbe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProverbeRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @NotBlank(message = "L'origine est obligatoire")
    @Size(max = 100, message = "L'origine ne peut pas dépasser 100 caractères")
    private String origineProverbe; // Origine du proverbe

    @NotBlank(message = "La signification est obligatoire")
    @Size(max = 500, message = "La signification ne peut pas dépasser 500 caractères")
    private String significationProverbe; // Signification du proverbe

    @NotBlank(message = "Le proverbe est obligatoire")
    @Size(max = 200, message = "Le proverbe ne peut pas dépasser 200 caractères")
    private String texteProverbe; // Le proverbe lui-même

    private MultipartFile photoProverbe; // Photo du proverbe

    private String lieu;
    private String region;
}
