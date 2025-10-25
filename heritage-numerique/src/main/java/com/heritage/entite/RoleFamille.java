package com.heritage.entite;

/**
 * Enumération des rôles possibles pour un membre dans une famille.
 * 
 * Utilisée dans l'entité MembreFamille pour définir les permissions
 * d'un utilisateur au sein d'une famille spécifique.
 */
public enum RoleFamille {
    
    /**
     * Administrateur de la famille.
     * - Peut gérer tous les aspects de la famille
     * - Peut inviter de nouveaux membres
     * - Peut changer les rôles des autres membres
     * - Peut supprimer la famille
     * - Peut demander la publication de contenus
     */
    ADMIN,
    
    /**
     * Éditeur de la famille.
     * - Peut créer et modifier des contenus
     * - Peut créer des quiz
     * - Peut ajouter des membres à l'arbre généalogique
     * - Peut consulter tous les contenus de la famille
     * - Ne peut pas inviter de nouveaux membres
     * - Ne peut pas changer les rôles
     */
    EDITEUR,
    
    /**
     * Lecteur de la famille.
     * - Peut consulter les contenus de la famille
     * - Peut passer les quiz
     * - Peut consulter l'arbre généalogique
     * - Ne peut pas créer de contenus
     * - Ne peut pas inviter de nouveaux membres
     * - Rôle par défaut lors de l'acceptation d'une invitation
     */
    LECTEUR;
    
    /**
     * Retourne le rôle par défaut lors de l'ajout d'un nouveau membre.
     * 
     * @return LECTEUR par défaut
     */
    public static RoleFamille getDefault() {
        return LECTEUR;
    }
    
    /**
     * Vérifie si le rôle a des permissions d'écriture (création/modification de contenus).
     * 
     * @return true si le rôle peut créer/modifier des contenus
     */
    public boolean canWrite() {
        return this == ADMIN || this == EDITEUR;
    }
    
    /**
     * Vérifie si le rôle a des permissions d'administration.
     * 
     * @return true si le rôle peut administrer la famille
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Vérifie si le rôle peut inviter de nouveaux membres.
     * 
     * @return true si le rôle peut envoyer des invitations
     */
    public boolean canInvite() {
        return this == ADMIN;
    }
    
    /**
     * Vérifie si le rôle peut changer les rôles des autres membres.
     * 
     * @return true si le rôle peut modifier les rôles
     */
    public boolean canManageRoles() {
        return this == ADMIN;
    }
}
