package com.heritage.util;

import com.heritage.securite.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitaire pour récupérer les informations d'authentification.
 */
public class AuthenticationHelper {

    /**
     * Récupère l'ID de l'utilisateur connecté depuis le SecurityContext.
     * 
     * @return ID de l'utilisateur connecté
     * @throws IllegalStateException si aucun utilisateur n'est connecté
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié");
        }
        
        // Le principal contient l'ID de l'utilisateur (défini dans JwtAuthenticationFilter)
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Format d'ID utilisateur invalide: " + principal);
            }
        } else if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        } else {
            throw new IllegalStateException("Type de principal non supporté: " + principal.getClass());
        }
    }

    /**
     * Récupère l'email de l'utilisateur connecté depuis le SecurityContext.
     * 
     * @return Email de l'utilisateur connecté
     * @throws IllegalStateException si aucun utilisateur n'est connecté
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié");
        }
        
        // L'email est stocké dans les détails de l'authentification
        Object details = authentication.getDetails();
        if (details instanceof String) {
            return (String) details;
        }
        
        throw new IllegalStateException("Email utilisateur non trouvé dans l'authentification");
    }

    /**
     * Vérifie si un utilisateur est authentifié.
     * 
     * @return true si un utilisateur est authentifié, false sinon
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
}