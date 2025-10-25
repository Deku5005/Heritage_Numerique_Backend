package com.heritage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête d'inscription d'un nouvel utilisateur.
 * Contient les informations nécessaires pour créer un compte,
 * avec optionnellement un code d'invitation pour rejoindre une famille.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String numeroTelephone;

    @Size(max = 100, message = "L'ethnie ne peut pas dépasser 100 caractères")
    private String ethnie;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    /**
     * Code d'invitation optionnel pour rejoindre une famille existante.
     * Si fourni, doit être un code valide et non expiré.
     */
    @Size(min = 8, max = 8, message = "Le code d'invitation doit contenir exactement 8 caractères")
    private String codeInvitation;
}


