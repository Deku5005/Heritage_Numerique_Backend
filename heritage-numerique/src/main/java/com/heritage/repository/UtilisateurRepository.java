package com.heritage.repository;

import com.heritage.entite.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur.
 * Fournit les opérations CRUD de base et des requêtes personnalisées.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par email.
     * Utilisé pour l'authentification et la vérification d'unicité.
     * 
     * @param email Email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec cet email.
     * Utilisé pour éviter les doublons lors de l'inscription.
     * 
     * @param email Email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);
}

