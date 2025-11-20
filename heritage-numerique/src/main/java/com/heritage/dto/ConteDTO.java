package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un conte avec ses informations détaillées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConteDTO {
    
    private Long id;
    private String titre;
    private String description;
    private String nomAuteur;
    private String prenomAuteur;
    private String emailAuteur;
    private String roleAuteur; // ADMIN, EDITEUR, LECTEUR
    private String lienParenteAuteur;
    private LocalDateTime dateCreation;
    private String statut; // BROUILLON, PUBLIE, EN_ATTENTE_VALIDATION
    private String urlFichier; // URL du fichier PDF/TXT si uploadé
    private String urlPhoto; // URL de la photo du conte
    private String contenuFichier; // Contenu textuel du fichier (PDF/TXT/DOC) extrait
    private String lieu;
    private String region;
    private Long idFamille;
    private String nomFamille;
    private QuizDTO quiz; // Quiz associé au conte
}
