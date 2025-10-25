package com.heritage.repository;

import com.heritage.entite.Contenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Contenu.
 */
@Repository
public interface ContenuRepository extends JpaRepository<Contenu, Long> {

    /**
     * Recherche tous les contenus d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des contenus
     */
    List<Contenu> findByFamilleId(Long familleId);

    /**
     * Recherche tous les contenus créés par un utilisateur.
     * 
     * @param auteurId ID de l'auteur
     * @return Liste des contenus
     */
    List<Contenu> findByAuteurId(Long auteurId);

    /**
     * Recherche tous les contenus d'une catégorie.
     * 
     * @param categorieId ID de la catégorie
     * @return Liste des contenus
     */
    List<Contenu> findByCategorieId(Long categorieId);

    /**
     * Recherche tous les contenus d'une famille avec un statut donné.
     * 
     * @param familleId ID de la famille
     * @param statut Statut du contenu (BROUILLON, PUBLIE, ARCHIVE)
     * @return Liste des contenus
     */
    List<Contenu> findByFamilleIdAndStatut(Long familleId, String statut);

    /**
     * Recherche tous les contenus par statut.
     * 
     * @param statut Statut du contenu
     * @return Liste des contenus
     */
    List<Contenu> findByStatut(String statut);

    /**
     * Recherche tous les contenus d'une famille sauf un statut donné.
     * 
     * @param familleId ID de la famille
     * @param statut Statut à exclure
     * @return Liste des contenus
     */
    List<Contenu> findByFamilleIdAndStatutNot(Long familleId, String statut);

    /**
     * Recherche tous les contenus d'une famille par type de contenu.
     * 
     * @param familleId ID de la famille
     * @param typeContenu Type de contenu (CONTE, ARTISANAT, PROVERBE, DEVINETTE)
     * @return Liste des contenus
     */
    List<Contenu> findByFamilleIdAndTypeContenu(Long familleId, String typeContenu);

    /**
     * Compte le nombre de contenus par type de contenu.
     * 
     * @param typeContenu Type de contenu
     * @return Nombre de contenus
     */
    long countByTypeContenu(String typeContenu);

    /**
     * Recherche tous les contenus d'une famille par auteur et type de contenu.
     * 
     * @param familleId ID de la famille
     * @param auteurId ID de l'auteur
     * @param typeContenu Type de contenu (CONTE, ARTISANAT, PROVERBE, DEVINETTE)
     * @return Liste des contenus
     */
    List<Contenu> findByFamilleIdAndAuteurIdAndTypeContenu(Long familleId, Long auteurId, String typeContenu);

    /**
     * Recherche tous les contenus par statut et type de contenu.
     * 
     * @param statut Statut du contenu
     * @param typeContenu Type de contenu (CONTE, ARTISANAT, PROVERBE, DEVINETTE)
     * @return Liste des contenus
     */
    List<Contenu> findByStatutAndTypeContenu(String statut, String typeContenu);

    /**
     * Recherche les 10 contenus les plus récents.
     * 
     * @return Liste des contenus les plus récents
     */
    List<Contenu> findTop10ByOrderByDateCreationDesc();

    /**
     * Recherche tous les contenus par type de contenu.
     * 
     * @param typeContenu Type de contenu
     * @return Liste des contenus
     */
    List<Contenu> findByTypeContenu(String typeContenu);
}

