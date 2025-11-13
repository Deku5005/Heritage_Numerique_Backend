package com.heritage.repository;

import com.heritage.entite.MembreArbre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité MembreArbre.
 * Gère les membres dans les arbres généalogiques.
 */
@Repository
public interface MembreArbreRepository extends JpaRepository<MembreArbre, Long> {

    /**
     * Recherche tous les membres d'un arbre.
     * 
     * @param arbreId ID de l'arbre généalogique
     * @return Liste des membres
     */
    List<MembreArbre> findByArbreId(Long arbreId);

    /**
     * Recherche tous les membres d'un arbre triés par date de naissance (du plus ancien au plus récent).
     * 
     * @param arbreId ID de l'arbre généalogique
     * @return Liste des membres triés par âge (du plus grand au plus petit)
     */
    List<MembreArbre> findByArbreIdOrderByDateNaissanceAsc(Long arbreId);

    /**
     * Recherche tous les enfants d'un membre (où ce membre est le père).
     * 
     * @param pereId ID du père
     * @return Liste des enfants
     */
    List<MembreArbre> findByPereId(Long pereId);

    /**
     * Recherche tous les enfants d'un membre (où ce membre est la mère).
     * 
     * @param mereId ID de la mère
     * @return Liste des enfants
     */
    List<MembreArbre> findByMereId(Long mereId);

    /**
     * Recherche un membre lié à un utilisateur réel.
     * 
     * @param utilisateurLieId ID de l'utilisateur lié
     * @return Liste des membres liés à cet utilisateur
     */
    List<MembreArbre> findByUtilisateurLieId(Long utilisateurLieId);

    /**
     * Recherche tous les enfants d'un membre (où ce membre est soit le père, soit la mère).
     * 
     * @param pereId ID du père
     * @param mereId ID de la mère (même valeur que pereId)
     * @return Liste des enfants
     */
    List<MembreArbre> findByPereIdOrMereId(Long pereId, Long mereId);
}

