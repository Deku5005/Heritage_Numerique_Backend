package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'authentification (inscription ou connexion).
 * Contient le token JWT et les informations de l'utilisateur.
 * 
 * Sécurité : Le mot de passe n'est JAMAIS inclus dans cette réponse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Token JWT d'accès.
     * À inclure dans le header Authorization: Bearer <token> pour les requêtes authentifiées.
     */
    private String accessToken;

    /**
     * Type de token (toujours "Bearer" pour JWT).
     */
    private String tokenType = "Bearer";

    /**
     * ID de l'utilisateur.
     */
    private Long userId;

    /**
     * Email de l'utilisateur.
     */
    private String email;

    /**
     * Nom complet de l'utilisateur.
     */
    private String nom;

    private String prenom;

    /**
     * Rôle global de l'utilisateur (ROLE_ADMIN, ROLE_MEMBRE).
     */
    private String role;
}

