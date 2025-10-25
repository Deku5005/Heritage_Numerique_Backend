package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour représenter les traductions d'un conte.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraductionConteDTO {
    
    private Long idConte;
    private String titreOriginal;
    private String descriptionOriginale;
    private String lieuOriginal;
    private String regionOriginale;
    private Map<String, String> traductionsTitre;
    private Map<String, String> traductionsContenu;
    private Map<String, String> traductionsDescription;
    private Map<String, String> traductionsLieu;
    private Map<String, String> traductionsRegion;
    private Map<String, String> traductionsCompletes;
    private java.util.Set<String> languesDisponibles;
    private String langueSource;
    private String statutTraduction; // SUCCES, ERREUR, EN_COURS
    private String messageErreur;
}
