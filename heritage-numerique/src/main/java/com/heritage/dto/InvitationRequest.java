package com.heritage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une invitation à rejoindre une famille.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotBlank(message = "Le nom de la personne invitée est obligatoire")
    private String nomInvite;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String emailInvite;

    private String telephoneInvite;

    @NotBlank(message = "Le lien de parenté est obligatoire")
    private String lienParente;
}