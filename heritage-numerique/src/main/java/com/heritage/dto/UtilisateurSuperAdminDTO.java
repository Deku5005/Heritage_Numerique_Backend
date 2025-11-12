package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour afficher les utilisateurs dans le dashboard super-admin.
 * Le nom affiché combine les initiales et le nom complet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurSuperAdminDTO {
    
    private Long id;
    
    /**
     * Format: "P.N. Prenom Nom"
     * Exemple: "A.T. Amadou Traoré"
     */
    private String nomComplet;
    
    private String role;
    private String telephone;
    private String email;
    private LocalDateTime dateAjout;
    private Boolean actif;
}

