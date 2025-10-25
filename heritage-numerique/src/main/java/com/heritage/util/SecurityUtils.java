package com.heritage.util;

import com.heritage.securite.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Classe utilitaire pour la sécurité.
 * 
 * Responsabilités :
 * - Récupérer l'ID de l'utilisateur connecté depuis le JWT
 * - Récupérer l'email de l'utilisateur connecté
 * - Vérifier les rôles
 */
@Component
public class SecurityUtils {

    private final JwtService jwtService;

    public SecurityUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Récupère l'ID de l'utilisateur connecté depuis le JWT.
     * 
     * Workflow :
     * 1. Récupérer l'authentication depuis SecurityContext
     * 2. Extraire le token JWT depuis la requête
     * 3. Extraire le userId depuis les claims du JWT
     * 
     * Note : Cette méthode suppose que le JwtAuthenticationFilter a déjà
     * validé le token et défini l'authentication dans le SecurityContext.
     * 
     * @param authentication Authentication Spring Security
     * @return ID de l'utilisateur connecté
     * @throws RuntimeException si l'utilisateur n'est pas authentifié
     */
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        // Récupérer l'email (username) depuis l'authentication
        String email = authentication.getName();

        // Pour récupérer l'ID, on doit extraire le token de la requête HTTP
        // Comme on n'a pas accès direct à la requête ici, on utilise une autre approche
        
        // Alternative : Stocker l'ID dans le Principal personnalisé
        // Pour l'instant, on va chercher l'utilisateur par email
        // TODO: Améliorer en créant un UserPrincipal avec l'ID
        
        return null; // À implémenter avec UserPrincipal
    }

    /**
     * Récupère l'email de l'utilisateur connecté.
     * 
     * @return Email de l'utilisateur
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return authentication.getName();
    }

    /**
     * Récupère l'ID utilisateur depuis le token JWT directement.
     * Cette méthode nécessite le token JWT en paramètre.
     * 
     * @param token Token JWT
     * @return ID de l'utilisateur
     */
    public Long getUserIdFromToken(String token) {
        return jwtService.extractUserId(token);
    }
}

