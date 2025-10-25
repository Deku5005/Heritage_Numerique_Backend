package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un membre de famille avec ses informations détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembreFamilleDTO {
    
    private Long id;
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ethnie;
    private String roleFamille; // ADMIN, EDITEUR, LECTEUR
    private String lienParente;
    private LocalDateTime dateAjout;
    private String statut; // EN_ATTENTE, ACCEPTE
    private Long idFamille;
    private String nomFamille;
}
