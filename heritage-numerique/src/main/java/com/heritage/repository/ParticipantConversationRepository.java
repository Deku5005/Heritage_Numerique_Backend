package com.heritage.repository;

import com.heritage.entite.ParticipantConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantConversationRepository
        extends JpaRepository<ParticipantConversation, Long> {

    // Participants d'une conversation
    List<ParticipantConversation> findByConversationId(Long conversationId);

    // Conversations d'un utilisateur
    List<ParticipantConversation> findByUtilisateurId(Long utilisateurId);

    // Vérifier appartenance
    boolean existsByConversationIdAndUtilisateurId(
            Long conversationId,
            Long utilisateurId
    );

    // Supprimer participant
    void deleteByConversationIdAndUtilisateurId(
            Long conversationId,
            Long utilisateurId
    );
}
