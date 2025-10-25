package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la connexion avec code d'invitation.
 * Utilisé quand un utilisateur existant veut rejoindre une famille via un code d'invitation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWithCodeRequest {

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    @NotBlank(message = "Le code d'invitation est obligatoire")
    private String codeInvitation;
}
