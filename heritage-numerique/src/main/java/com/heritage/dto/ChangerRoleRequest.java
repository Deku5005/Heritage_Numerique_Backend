package com.heritage.dto;

import com.heritage.entite.RoleFamille;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour changer le rôle d'un membre dans une famille
 * Utilisé par l'admin de famille pour modifier les permissions
 */
public class ChangerRoleRequest {
    
    @NotNull(message = "Le nouveau rôle est obligatoire")
    private RoleFamille nouveauRole;
    
    // Constructeurs
    public ChangerRoleRequest() {}
    
    public ChangerRoleRequest(RoleFamille nouveauRole) {
        this.nouveauRole = nouveauRole;
    }
    
    // Getters et Setters
    public RoleFamille getNouveauRole() {
        return nouveauRole;
    }
    
    public void setNouveauRole(RoleFamille nouveauRole) {
        this.nouveauRole = nouveauRole;
    }
    
    @Override
    public String toString() {
        return "ChangerRoleRequest{" +
                "nouveauRole=" + nouveauRole +
                '}';
    }
}
