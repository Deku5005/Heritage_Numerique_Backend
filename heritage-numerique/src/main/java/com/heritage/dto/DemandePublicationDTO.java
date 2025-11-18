package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour une demande de publication de contenu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandePublicationDTO {

    private Long id;
    private Long idContenu;
    private String titreContenu;
    private String typeContenu;
    private Long idFamille;
    private String nomFamille;
    private Long idDemandeur;
    private String nomDemandeur;
    private Long idValideur;
    private String nomValideur;
    private String statut; // EN_ATTENTE, APPROUVEE, REJETEE
    private String commentaire;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
}

