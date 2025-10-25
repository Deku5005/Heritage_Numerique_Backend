package com.heritage.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses d'erreur uniformes.
 * Permet de retourner des erreurs structurées en JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Code de statut HTTP.
     */
    private int status;
    
    /**
     * Message d'erreur lisible.
     */
    private String message;
    
    /**
     * Chemin de la requête qui a causé l'erreur.
     */
    private String path;
    
    /**
     * Timestamp de l'erreur.
     */
    private LocalDateTime timestamp;
    
    /**
     * Détails supplémentaires (optionnel).
     */
    private String details;
}


