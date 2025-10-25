package com.heritage.repository;

import com.heritage.entite.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Quiz.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /**
     * Recherche tous les quiz d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des quiz
     */
    List<Quiz> findByFamilleId(Long familleId);

    /**
     * Recherche tous les quiz actifs d'une famille.
     * 
     * @param familleId ID de la famille
     * @param actif Statut actif
     * @return Liste des quiz actifs
     */
    List<Quiz> findByFamilleIdAndActif(Long familleId, Boolean actif);

    /**
     * Recherche tous les quiz créés par un utilisateur.
     * 
     * @param createurId ID du créateur
     * @return Liste des quiz
     */
    List<Quiz> findByCreateurId(Long createurId);


}


