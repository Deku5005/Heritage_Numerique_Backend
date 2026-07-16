package com.heritage.service;


import com.heritage.entite.Conversation;
import com.heritage.entite.Message;
import com.heritage.entite.TypeMessage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {

    // conversation famille
    Conversation creerConversationFamille(Long familleId, Long createurId);

    // groupe
    Conversation creerGroupe(
            Long familleId,
            String nom,
            Long createurId,
            List<Long> participantsIds
    );

    // conversation privée
    Conversation creerPrive(
            Long user1,
            Long user2,
            Long familleId
    );

    // envoyer message
    Message envoyerMessage(
            Long conversationId,
            Long expediteurId,
            String contenu,
            TypeMessage type,
            String urlMedia
    );

    // lire messages
    Page<Message> lireMessages(
            Long conversationId,
            int page,
            int size
    );

    // message lu
    void marquerCommeLu(
            Long messageId,
            Long utilisateurId
    );

    // liste conversations utilisateur
    List<Conversation> conversationsUtilisateur(Long utilisateurId);
}