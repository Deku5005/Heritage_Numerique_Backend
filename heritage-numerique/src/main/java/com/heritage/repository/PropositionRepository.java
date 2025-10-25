package com.heritage.repository;

import com.heritage.entite.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des propositions de réponses aux questions de quiz.
 */
@Repository
public interface PropositionRepository extends JpaRepository<Proposition, Long> {
    
    /**
     * Récupère toutes les propositions d'une question triées par ordre.
     * 
     * @param questionId ID de la question
     * @return Liste des propositions triées par ordre
     */
    List<Proposition> findByQuestionIdOrderByOrdreAsc(Long questionId);
    
    /**
     * Récupère toutes les propositions correctes d'une question.
     * 
     * @param questionId ID de la question
     * @return Liste des propositions correctes
     */
    List<Proposition> findByQuestionIdAndEstCorrecteTrue(Long questionId);

    /**
     * Récupère toutes les propositions d'une question.
     * 
     * @param questionId ID de la question
     * @return Liste des propositions
     */
    List<Proposition> findByQuestionId(Long questionId);
}
