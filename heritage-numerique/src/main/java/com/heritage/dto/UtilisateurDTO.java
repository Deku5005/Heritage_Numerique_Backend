package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les informations publiques d'un utilisateur.
 * Ne contient JAMAIS le mot de passe pour des raisons de sécurité.
 * Inclut les familles auxquelles appartient l'utilisateur avec leurs rôles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String numeroTelephone;
    private String ethnie;
    private String role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private List<FamilleUtilisateurDTO> familles; // Liste des familles avec les rôles
}


