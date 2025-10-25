package com.heritage.repository;

import com.heritage.entite.DemandePublication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité DemandePublication.
 * Gère les demandes de publication de contenus.
 */
@Repository
public interface DemandePublicationRepository extends JpaRepository<DemandePublication, Long> {

    /**
     * Recherche toutes les demandes d'un contenu.
     * 
     * @param contenuId ID du contenu
     * @return Liste des demandes
     */
    List<DemandePublication> findByContenuId(Long contenuId);

    /**
     * Recherche les demandes par statut.
     * 
     * @param statut Statut recherché (EN_ATTENTE, APPROUVEE, REJETEE)
     * @return Liste des demandes
     */
    List<DemandePublication> findByStatut(String statut);

    /**
     * Vérifie s'il existe une demande pour un contenu avec un statut donné.
     * 
     * @param contenuId ID du contenu
     * @param statut Statut à vérifier
     * @return true si une demande existe
     */
    boolean existsByContenuIdAndStatut(Long contenuId, String statut);

    /**
     * Recherche les demandes d'une famille via les contenus.
     * 
     * @param familleId ID de la famille
     * @return Liste des demandes
     */
    List<DemandePublication> findByContenuFamilleId(Long familleId);
}

