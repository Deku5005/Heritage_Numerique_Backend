package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour représenter un nœud dans l'arbre généalogique hiérarchique.
 * Chaque nœud contient les informations d'un membre et la liste de ses enfants.
 * Structure optimisée pour l'affichage dans Flutter (style MyHeritage).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoeudArbreDTO {

    /**
     * Informations du membre
     */
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
    private String relationFamiliale;
    
    /**
     * Relations parentales (IDs seulement pour référence)
     */
    private Long idPere;
    private Long idMere;
    
    /**
     * Liste des enfants (structure hiérarchique récursive)
     * Cette liste permet de construire l'arbre visuel dans Flutter
     */
    private List<NoeudArbreDTO> enfants;
    
    /**
     * Nombre total d'enfants (pour affichage rapide)
     */
    private int nombreEnfants;
    
    /**
     * Niveau dans l'arbre (0 = racine, 1 = enfants de racine, etc.)
     * Utile pour le positionnement visuel
     */
    private int niveau;
    
    /**
     * Position horizontale suggérée (pour layout automatique)
     * Calculée pour éviter les chevauchements
     */
    private double positionX;
    
    /**
     * Position verticale suggérée (pour layout automatique)
     */
    private double positionY;
}

