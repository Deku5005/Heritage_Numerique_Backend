package com.heritage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour ajouter manuellement un membre à une famille.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjoutMembreRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String telephone;

    @Size(max = 100, message = "L'ethnie ne peut pas dépasser 100 caractères")
    private String ethnie;

    @NotBlank(message = "Le lien de parenté est obligatoire")
    @Size(max = 50, message = "Le lien de parenté ne peut pas dépasser 50 caractères")
    private String lienParente;

    @NotBlank(message = "Le rôle dans la famille est obligatoire")
    private String roleFamille; // ADMIN, EDITEUR, LECTEUR
}
