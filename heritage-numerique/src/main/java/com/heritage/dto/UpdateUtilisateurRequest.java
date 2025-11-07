package com.heritage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la modification des informations d'un utilisateur.
 * Tous les champs sont optionnels (seuls les champs fournis seront mis à jour).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUtilisateurRequest {

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @Email(message = "Format d'email invalide")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String numeroTelephone;

    @Size(max = 100, message = "L'ethnie ne doit pas dépasser 100 caractères")
    private String ethnie;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse; // Optionnel : seulement si l'utilisateur veut changer son mot de passe
}

