package com.heritage.repository;

import com.heritage.entite.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Categorie.
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    /**
     * Recherche une catégorie par son nom.
     * 
     * @param nom Nom de la catégorie
     * @return Optional contenant la catégorie si trouvée
     */
    Optional<Categorie> findByNom(String nom);
}


