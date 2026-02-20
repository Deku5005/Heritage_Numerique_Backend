package com.heritage.repository;

import com.heritage.entite.MessageLu;
import com.heritage.entite.MessageLuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageLuRepository
        extends JpaRepository<MessageLu, MessageLuId> {

    // Vérifier lecture
    boolean existsById(MessageLuId id);

    // Messages lus par user
    List<MessageLu> findByUtilisateurId(Long utilisateurId);

    // Supprimer lectures d’un message
    void deleteByMessageId(Long messageId);
}
