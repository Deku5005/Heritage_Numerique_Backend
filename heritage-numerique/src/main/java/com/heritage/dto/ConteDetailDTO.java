package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un conte avec son contenu détaillé.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConteDetailDTO {
    
    private Long id;
    private String titre;
    private String description;
    private String nomAuteur;
    private String prenomAuteur;
    private String emailAuteur;
    private String roleAuteur;
    private String lienParenteAuteur;
    private LocalDateTime dateCreation;
    private String statut;
    private String urlFichier;
    private String urlPhoto;
    private String lieu;
    private String region;
    private Long idFamille;
    private String nomFamille;
    
    // Contenu extrait du fichier
    private String contenuTexte;
    private String typeFichier; // PDF, TXT, DOC, DOCX
    private Integer nombreMots;
    private Integer nombreCaracteres;
    private Integer nombreLignes;
}
