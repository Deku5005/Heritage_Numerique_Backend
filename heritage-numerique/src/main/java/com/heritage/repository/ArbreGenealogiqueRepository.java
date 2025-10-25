package com.heritage.repository;

import com.heritage.entite.ArbreGenealogique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité ArbreGenealogique.
 */
@Repository
public interface ArbreGenealogiqueRepository extends JpaRepository<ArbreGenealogique, Long> {

    /**
     * Recherche tous les arbres d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des arbres généalogiques
     */
    List<ArbreGenealogique> findByFamilleId(Long familleId);

    /**
     * Recherche tous les arbres créés par un utilisateur.
     * 
     * @param createurId ID du créateur
     * @return Liste des arbres
     */
    List<ArbreGenealogique> findByCreateurId(Long createurId);
}

