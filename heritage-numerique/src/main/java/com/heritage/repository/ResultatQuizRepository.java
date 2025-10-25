package com.heritage.repository;

import com.heritage.entite.ResultatQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité ResultatQuiz.
 */
@Repository
public interface ResultatQuizRepository extends JpaRepository<ResultatQuiz, Long> {

    /**
     * Recherche tous les résultats d'un quiz.
     * 
     * @param quizId ID du quiz
     * @return Liste des résultats
     */
    List<ResultatQuiz> findByQuizId(Long quizId);

    /**
     * Recherche tous les résultats d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des résultats
     */
    List<ResultatQuiz> findByUtilisateurId(Long utilisateurId);

    /**
     * Recherche tous les résultats d'un utilisateur pour un quiz spécifique.
     * 
     * @param quizId ID du quiz
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des résultats
     */
    List<ResultatQuiz> findByQuizIdAndUtilisateurId(Long quizId, Long utilisateurId);

    /**
     * Vérifie si un résultat existe pour un quiz et un utilisateur donnés.
     * 
     * @param quizId ID du quiz
     * @param utilisateurId ID de l'utilisateur
     * @return true si un résultat existe, false sinon
     */
    boolean existsByQuizIdAndUtilisateurId(Long quizId, Long utilisateurId);
}


