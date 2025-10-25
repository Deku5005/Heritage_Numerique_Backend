package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour ajouter un membre à un arbre généalogique.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembreArbreRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @Size(max = 10, message = "Le sexe ne peut pas dépasser 10 caractères")
    private String sexe; // M, F

    private LocalDate dateNaissance;
    private LocalDate dateDeces;

    @Size(max = 200, message = "Le lieu de naissance ne peut pas dépasser 200 caractères")
    private String lieuNaissance;

    @Size(max = 200, message = "Le lieu de décès ne peut pas dépasser 200 caractères")
    private String lieuDeces;

    @Size(max = 5000, message = "La biographie ne peut pas dépasser 5000 caractères")
    private String biographie;

    @Size(max = 500, message = "L'URL de la photo ne peut pas dépasser 500 caractères")
    private String photoUrl;

    private Long idPere; // ID du père (optionnel)
    private Long idMere; // ID de la mère (optionnel)
    private Long idUtilisateurLie; // ID de l'utilisateur lié (optionnel)
}
