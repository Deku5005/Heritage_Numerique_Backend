package com.heritage.repository;

import com.heritage.entite.Famille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Famille.
 */
@Repository
public interface FamilleRepository extends JpaRepository<Famille, Long> {

    /**
     * Recherche toutes les familles créées par un utilisateur.
     * 
     * @param createurId ID de l'utilisateur créateur
     * @return Liste des familles
     */
    List<Famille> findByCreateurId(Long createurId);

    /**
     * Recherche les 10 familles les plus récentes.
     * 
     * @return Liste des familles les plus récentes
     */
    List<Famille> findTop10ByOrderByDateCreationDesc();
}


