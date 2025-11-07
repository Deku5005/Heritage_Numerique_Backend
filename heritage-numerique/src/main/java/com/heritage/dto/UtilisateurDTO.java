package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les informations publiques d'un utilisateur.
 * Ne contient JAMAIS le mot de passe pour des raisons de sécurité.
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
    private LocalDateTime dateModification;
}


