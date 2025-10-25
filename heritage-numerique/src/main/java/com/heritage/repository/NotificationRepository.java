package com.heritage.repository;

import com.heritage.entite.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Notification.
 * Gère l'historique des notifications envoyées aux utilisateurs.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Recherche toutes les notifications d'un utilisateur.
     * 
     * @param destinataireId ID de l'utilisateur
     * @return Liste des notifications ordonnées par date décroissante
     */
    List<Notification> findByDestinataireIdOrderByDateEnvoiDesc(Long destinataireId);

    /**
     * Recherche les notifications non lues d'un utilisateur.
     * 
     * @param destinataireId ID de l'utilisateur
     * @param lu Statut de lecture (false pour non lues)
     * @return Liste des notifications non lues
     */
    List<Notification> findByDestinataireIdAndLu(Long destinataireId, Boolean lu);

    /**
     * Compte le nombre de notifications non lues d'un utilisateur.
     * 
     * @param destinataireId ID de l'utilisateur
     * @param lu Statut de lecture
     * @return Nombre de notifications
     */
    Long countByDestinataireIdAndLu(Long destinataireId, Boolean lu);

    /**
     * Recherche les notifications par type.
     * 
     * @param type Type de notification
     * @return Liste des notifications
     */
    List<Notification> findByType(String type);
}

