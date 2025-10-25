package com.heritage.repository;

import com.heritage.entite.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Invitation.
 * Gère les invitations à rejoindre une famille.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    /**
     * Recherche une invitation par son code unique.
     * Utilisé lors de l'inscription avec code d'invitation.
     * 
     * @param codeInvitation Code de l'invitation
     * @return Optional contenant l'invitation si trouvée
     */
    Optional<Invitation> findByCodeInvitation(String codeInvitation);

    /**
     * Recherche une invitation valide (non expirée, en attente) par code et email.
     * Sécurité : vérifie que l'email correspond pour éviter l'utilisation frauduleuse.
     * 
     * @param codeInvitation Code de l'invitation
     * @param emailInvite Email de la personne invitée
     * @return Optional contenant l'invitation si valide
     */
    Optional<Invitation> findByCodeInvitationAndEmailInvite(String codeInvitation, String emailInvite);

    /**
     * Recherche toutes les invitations d'une famille.
     * 
     * @param familleId ID de la famille
     * @return Liste des invitations
     */
    List<Invitation> findByFamilleId(Long familleId);

    /**
     * Recherche toutes les invitations envoyées par un utilisateur.
     * 
     * @param emetteurId ID de l'émetteur
     * @return Liste des invitations
     */
    List<Invitation> findByEmetteurId(Long emetteurId);

    /**
     * Recherche les invitations expirées (date d'expiration dépassée et statut EN_ATTENTE).
     * Utilisé par le job d'expiration automatique.
     * 
     * @param now Date/heure actuelle
     * @param statut Statut à rechercher (EN_ATTENTE)
     * @return Liste des invitations expirées
     */
    @Query("SELECT i FROM Invitation i WHERE i.dateExpiration < :now AND i.statut = :statut")
    List<Invitation> findExpiredInvitations(@Param("now") LocalDateTime now, @Param("statut") String statut);

    /**
     * Vérifie si un code d'invitation existe déjà.
     * Utilisé lors de la génération du code pour garantir l'unicité.
     * 
     * @param codeInvitation Code à vérifier
     * @return true si le code existe
     */
    boolean existsByCodeInvitation(String codeInvitation);

    /**
     * Recherche toutes les invitations pour un email donné.
     * Utilisé pour afficher les invitations en attente dans le dashboard personnel.
     * 
     * @param emailInvite Email de la personne invitée
     * @return Liste des invitations
     */
    List<Invitation> findByEmailInvite(String emailInvite);

    /**
     * Recherche les invitations en attente pour un email donné.
     * 
     * @param emailInvite Email de la personne invitée
     * @param statut Statut recherché (EN_ATTENTE)
     * @return Liste des invitations en attente
     */
    List<Invitation> findByEmailInviteAndStatut(String emailInvite, String statut);



    List<Invitation> findByStatut( String statut);
}

