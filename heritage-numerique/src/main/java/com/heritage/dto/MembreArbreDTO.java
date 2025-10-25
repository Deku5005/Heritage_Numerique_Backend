package com.heritage.dto;

import com.heritage.entite.MembreArbre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour représenter un membre de l'arbre généalogique.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembreArbreDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String sexe;
    private LocalDate dateNaissance;
    private LocalDate dateDeces;
    private String lieuNaissance;
    private String lieuDeces;
    private String biographie;
    private String photoUrl;
    private String telephone;
    private String relationFamiliale;
    private String email;
    private Long idPere;
    private String nomPere;
    private Long idMere;
    private String nomMere;
    private Long idUtilisateurLie;
    private String nomUtilisateurLie;
    private Long idFamille;
    private String nomFamille;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    /**
     * Constructeur à partir de l'entité MembreArbre.
     */
    public MembreArbreDTO(MembreArbre membre) {
        this.id = membre.getId();
        this.nom = membre.getNom();
        this.prenom = membre.getPrenom();
        this.nomComplet = membre.getNomComplet();
        this.sexe = membre.getSexe();
        this.dateNaissance = membre.getDateNaissance();
        this.dateDeces = membre.getDateDeces();
        this.lieuNaissance = membre.getLieuNaissance();
        this.lieuDeces = membre.getLieuDeces();
        this.biographie = membre.getBiographie();
        this.photoUrl = membre.getPhotoUrl();
        this.relationFamiliale = membre.getRelationFamiliale();
        this.dateCreation = membre.getDateCreation();
        this.dateModification = membre.getDateModification();
        
        // Relations parentales
        if (membre.getPere() != null) {
            this.idPere = membre.getPere().getId();
            this.nomPere = membre.getPere().getNomComplet();
        }
        if (membre.getMere() != null) {
            this.idMere = membre.getMere().getId();
            this.nomMere = membre.getMere().getNomComplet();
        }
        
        // Utilisateur lié
        if (membre.getUtilisateurLie() != null) {
            this.idUtilisateurLie = membre.getUtilisateurLie().getId();
            this.nomUtilisateurLie = membre.getUtilisateurLie().getNom() + " " + membre.getUtilisateurLie().getPrenom();
        }
        
        // Informations de la famille
        this.idFamille = membre.getArbre().getFamille().getId();
        this.nomFamille = membre.getArbre().getFamille().getNom();
    }
}