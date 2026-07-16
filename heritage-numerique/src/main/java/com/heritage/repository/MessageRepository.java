package com.heritage.repository;


import com.heritage.entite.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Messages d'une conversation (pagination)
    Page<Message> findByConversationIdOrderByDateEnvoiDesc(
            Long conversationId,
            Pageable pageable
    );

    // Dernier message
    Optional<Message> findTopByConversationIdOrderByDateEnvoiDesc(
            Long conversationId
    );

    // Nombre messages non supprimés
    long countByConversationIdAndSupprimeFalse(Long conversationId);


    @Query("""
SELECT COUNT(m)
FROM Message m
WHERE m.conversation.id = :convId
AND m.id NOT IN (
   SELECT ml.message.id
   FROM MessageLu ml
   WHERE ml.utilisateur.id = :userId
)
""")
    long countUnreadMessages(Long convId, Long userId);
}
