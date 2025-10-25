package com.heritage.repository;

import com.heritage.entite.MembreFamille;
import com.heritage.entite.RoleFamille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité MembreFamille.
 * Gère les relations entre utilisateurs et familles.
 */
@Repository
public interface MembreFamilleRepository extends JpaRepository<MembreFamille, Long> {

    /**
     * Recherche tous les membres d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des membres
     */
    List<MembreFamille> findByFamilleId(Long familleId);

    /**
     * Recherche toutes les familles d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des appartenances aux familles
     */
    List<MembreFamille> findByUtilisateurId(Long utilisateurId);

    /**
     * Recherche l'appartenance d'un utilisateur à une famille spécifique.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param familleId ID de la famille
     * @return Optional contenant le membre si trouvé
     */
    Optional<MembreFamille> findByUtilisateurIdAndFamilleId(Long utilisateurId, Long familleId);

    /**
     * Vérifie si un utilisateur est membre d'une famille.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param familleId ID de la famille
     * @return true si l'utilisateur est membre
     */
    boolean existsByUtilisateurIdAndFamilleId(Long utilisateurId, Long familleId);

    /**
     * Recherche les membres d'une famille avec un rôle spécifique.
     * 
     * @param familleId ID de la famille
     * @param roleFamille Rôle à rechercher
     * @return Liste des membres avec ce rôle
     */
    List<MembreFamille> findByFamilleIdAndRoleFamille(Long familleId, RoleFamille roleFamille);

    /**
     * Compte le nombre de membres d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Nombre de membres
     */
    long countByFamilleId(Long familleId);
}


