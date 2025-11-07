package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant une famille d'un utilisateur avec son rôle spécifique.
 * Utilisé pour afficher les familles auxquelles appartient un utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilleUtilisateurDTO {
    
    private Long idFamille;
    private String nomFamille;
    private String descriptionFamille;
    private String ethnie;
    private String region;
    private String roleFamille; // ADMIN, EDITEUR, LECTEUR
    private String lienParente; // Lien de parenté au sein de la famille
    private LocalDateTime dateAjout; // Date d'ajout à la famille
}

