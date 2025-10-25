package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DTO pour la création de contenu avec types spécifiques.
 * Supporte les types : CONTE, DEVINETTE, ARTISANAT, PROVERBE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreationContenuRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long idCategorie;

    @NotBlank(message = "Le type de contenu est obligatoire")
    private String typeContenu; // CONTE, DEVINETTE, ARTISANAT, PROVERBE

    // ===== CHAMPS COMMUNS =====
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    // ===== CHAMPS SPÉCIFIQUES AU CONTE =====
    private String texteConte; // Texte du conte si pas de fichier
    private MultipartFile fichierConte; // Fichier PDF ou TXT du conte
    private MultipartFile photoConte; // Photo du conte

    // ===== CHAMPS SPÉCIFIQUES À L'ARTISANAT =====
    private List<MultipartFile> photosArtisanat; // Plusieurs photos
    private MultipartFile videoArtisanat; // Vidéo optionnelle

    // ===== CHAMPS SPÉCIFIQUES AU PROVERBE =====
    private String origineProverbe; // Origine du proverbe
    private String significationProverbe; // Signification du proverbe
    private String texteProverbe; // Le proverbe lui-même
    private MultipartFile photoProverbe; // Photo du proverbe

    // ===== CHAMPS SPÉCIFIQUES À LA DEVINETTE =====
    private String texteDevinette; // La devinette
    private String reponseDevinette; // La réponse/signification
    private MultipartFile photoDevinette; // Photo de la devinette

    // ===== CHAMPS OPTIONNELS =====
    private String lieu;
    private String region;
}
