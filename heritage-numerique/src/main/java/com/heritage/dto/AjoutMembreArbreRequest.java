package com.heritage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * DTO pour ajouter un membre à l'arbre généalogique.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AjoutMembreArbreRequest {

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotBlank(message = "La date de naissance est obligatoire")
    private String dateNaissance;

    @NotBlank(message = "Le lieu de naissance est obligatoire")
    private String lieuNaissance;

    @NotBlank(message = "La relation familiale est obligatoire")
    private String relationFamiliale;

    private MultipartFile photo;

    private String telephone;

    @Email(message = "L'email doit être valide")
    private String email;

    private String biographie;

    private Long parent1Id;

    private Long parent2Id;

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    // Méthode manquante pour compatibilité
    public Long getIdFamille() {
        return this.idFamille;
    }

    public void setIdFamille(Long idFamille) {
        this.idFamille = idFamille;
    }
}