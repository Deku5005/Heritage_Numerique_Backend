package com.heritage.securite;

/**
 * Énumération des rôles utilisateurs dans l'application.
 * 
 * ROLE_ADMIN : Administrateur système avec tous les privilèges
 * ROLE_MEMBRE : Utilisateur standard
 * 
 * Note : Les rôles de famille (ADMIN, CONTRIBUTEUR, LECTEUR) sont gérés
 * séparément dans la table membre_famille.
 */
public enum Role {
    ROLE_ADMIN,
    ROLE_MEMBRE
}

