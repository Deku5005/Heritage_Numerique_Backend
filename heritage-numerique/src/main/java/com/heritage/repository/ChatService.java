package com.heritage.repository;

import com.heritage.entite.Conversation;
import com.heritage.entite.Message;
import com.heritage.entite.TypeMessage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {

    Conversation creerConversationFamille(Long familleId, Long createurId);

    Conversation creerGroupe(
            Long familleId,
            String nom,
            Long createurId,
            List<Long> participantsIds
    );

    Conversation creerPrive(Long user1, Long user2, Long familleId);

    Message envoyerMessage(
            Long conversationId,
            Long expediteurId,
            String contenu,
            TypeMessage type,
            String urlMedia
    );

    Page<Message> lireMessages(
            Long conversationId,
            int page,
            int size
    );

    void marquerCommeLu(Long messageId, Long utilisateurId);

    List<Conversation> conversationsUtilisateur(Long utilisateurId);
}
