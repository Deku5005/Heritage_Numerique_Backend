package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO hiérarchique pour représenter un arbre généalogique.
 * Structure optimisée pour l'affichage dans Flutter (style MyHeritage).
 * 
 * L'arbre est représenté comme une liste de nœuds racines (membres sans parents),
 * chaque nœud contenant récursivement ses enfants.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbreGenealogiqueHierarchiqueDTO {

    private Long id;
    private Long idFamille;
    private String nomFamille;
    private String nom;
    private String description;
    private Long idCreateur;
    private String nomCreateur;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    /**
     * Liste des nœuds racines (membres sans parents connus).
     * Chaque nœud contient récursivement ses descendants.
     */
    private List<NoeudArbreDTO> racines;
    
    /**
     * Nombre total de membres dans l'arbre
     */
    private int nombreMembres;
    
    /**
     * Nombre de générations dans l'arbre
     */
    private int nombreGenerations;
    
    /**
     * ID du membre racine principal (pour centrer l'affichage)
     * Si null, utiliser le premier membre de la liste racines
     */
    private Long idMembreRacinePrincipal;
}

