package com.heritage.repository;

import com.heritage.entite.Conversation;
import com.heritage.entite.TypeConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Toutes les conversations d'une famille
    List<Conversation> findByFamilleId(Long familleId);

    // Conversations par type
    List<Conversation> findByType(TypeConversation type);

    // Conversation privée entre deux users
    @Query("""
        SELECT c FROM Conversation c
        JOIN ParticipantConversation p1 ON p1.conversation = c
        JOIN ParticipantConversation p2 ON p2.conversation = c
        WHERE c.type = 'PRIVE'
        AND p1.utilisateur.id = :u1
        AND p2.utilisateur.id = :u2
    """)
    Optional<Conversation> findPrivateConversation(Long u1, Long u2);
}
