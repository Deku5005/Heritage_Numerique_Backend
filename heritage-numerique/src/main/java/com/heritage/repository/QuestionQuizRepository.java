package com.heritage.repository;

import com.heritage.entite.QuestionQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des questions de quiz.
 */
@Repository
public interface QuestionQuizRepository extends JpaRepository<QuestionQuiz, Long> {
    
    /**
     * Récupère toutes les questions d'un quiz triées par ordre.
     * 
     * @param quizId ID du quiz
     * @return Liste des questions triées par ordre
     */
    List<QuestionQuiz> findByQuizIdOrderByOrdreAsc(Long quizId);

    /**
     * Récupère toutes les questions d'un quiz.
     * 
     * @param quizId ID du quiz
     * @return Liste des questions
     */
    List<QuestionQuiz> findByQuizId(Long quizId);
}
