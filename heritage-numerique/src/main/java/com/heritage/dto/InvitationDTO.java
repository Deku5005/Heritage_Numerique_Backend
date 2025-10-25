package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter une invitation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {

    private Long id;
    private Long idFamille;
    private String nomFamille;
    private Long idEmetteur;
    private String nomEmetteur;
    private String nomAdmin;
    private String nomInvite;
    private String emailInvite;
    private String telephoneInvite;
    private String lienParente;
    private String codeInvitation;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration;
    private LocalDateTime dateUtilisation;
}