package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les informations d'un utilisateur avec son rôle dans une famille spécifique.
 * Ne contient JAMAIS le mot de passe pour des raisons de sécurité.
 * Utilisé pour afficher un utilisateur dans le contexte d'une famille particulière.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurAvecRoleFamilleDTO {

    // Informations de l'utilisateur
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String numeroTelephone;
    private String ethnie;
    private String role; // Rôle global (ROLE_ADMIN, ROLE_MEMBRE)
    private Boolean actif;
    private LocalDateTime dateCreation;
    
    // Informations du rôle dans la famille spécifique
    private Long idFamille;
    private String nomFamille;
    private String roleFamille; // ADMIN, EDITEUR, LECTEUR
    private String lienParente; // Lien de parenté au sein de la famille
    private LocalDateTime dateAjoutFamille; // Date d'ajout à cette famille
}

