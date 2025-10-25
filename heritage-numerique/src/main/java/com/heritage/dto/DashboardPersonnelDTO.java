package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour le dashboard personnel de l'utilisateur.
 * Affiche les informations personnelles et invitations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPersonnelDTO {

    // Informations utilisateur
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String role;

    // Statistiques
    private Integer nombreFamillesAppartenance;
    private Integer nombreInvitationsEnAttente;
    private Integer nombreInvitationsRecues;
    private Integer nombreContenusCreés;
    private Integer nombreQuizCreés;
    private Integer nombreNotificationsNonLues;

    // Invitations en attente
    private List<InvitationDTO> invitationsEnAttente = new ArrayList<>();
    
    // Familles de l'utilisateur
    private List<FamilleDTO> familles = new ArrayList<>();
    
    // Toutes les invitations reçues (avec détails)
    private List<InvitationDTO> invitationsRecues = new ArrayList<>();
}

