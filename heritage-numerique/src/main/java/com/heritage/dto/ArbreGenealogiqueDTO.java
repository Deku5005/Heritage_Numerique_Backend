package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter un arbre généalogique.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbreGenealogiqueDTO {

    private Long id;
    private Long idFamille;
    private String nomFamille;
    private String nom;
    private String description;
    private Long idCreateur;
    private String nomCreateur;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private List<MembreArbreDTO> membres;
    private int nombreMembres;
}