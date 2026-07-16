package com.heritage.service;

import com.heritage.entite.*;
import com.heritage.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepo;
    private final ParticipantConversationRepository participantRepo;
    private final MessageRepository messageRepo;
    private final MessageLuRepository messageLuRepo;

    private final UtilisateurRepository utilisateurRepo;
    private final FamilleRepository familleRepo;


    //Creation conversation famille
    @Override
    public Conversation creerConversationFamille(Long familleId, Long createurId) {

        Famille famille = familleRepo.findById(familleId).orElseThrow();
        Utilisateur createur = utilisateurRepo.findById(createurId).orElseThrow();

        Conversation conv = conversationRepo.save(
                Conversation.builder()
                        .famille(famille)
                        .creePar(createur)
                        .type(TypeConversation.FAMILLE)
                        .nom("Général")
                        .build()
        );

        // Ajouter tous les membres famille
        List<MembreFamille> membres = famille.getMembres();

        membres.forEach(m ->
                participantRepo.save(
                        ParticipantConversation.builder()
                                .conversation(conv)
                                .utilisateur(m.getUtilisateur())
                                .role(RoleParticipant.MEMBRE)
                                .build()
                )
        );

        return conv;
    }

    //Creation de groupe
    @Override
    public Conversation creerGroupe(
            Long familleId,
            String nom,
            Long createurId,
            List<Long> participantsIds
    ) {

        Famille famille = familleRepo.findById(familleId).orElseThrow();
        Utilisateur createur = utilisateurRepo.findById(createurId).orElseThrow();

        Conversation conv = conversationRepo.save(
                Conversation.builder()
                        .famille(famille)
                        .creePar(createur)
                        .type(TypeConversation.GROUPE)
                        .nom(nom)
                        .build()
        );

        // Ajouter créateur admin
        participantRepo.save(
                ParticipantConversation.builder()
                        .conversation(conv)
                        .utilisateur(createur)
                        .role(RoleParticipant.ADMIN)
                        .build()
        );

        // Ajouter participants
        participantsIds.forEach(id -> {
            Utilisateur u = utilisateurRepo.findById(id).orElseThrow();

            participantRepo.save(
                    ParticipantConversation.builder()
                            .conversation(conv)
                            .utilisateur(u)
                            .build()
            );
        });

        return conv;
    }


    //Creation de groupe privée
    @Override
    public Conversation creerPrive(Long u1, Long u2, Long familleId) {

        Optional<Conversation> existing =
                conversationRepo.findPrivateConversation(u1, u2);

        if(existing.isPresent())
            return existing.get();

        Famille famille = familleRepo.findById(familleId).orElseThrow();

        Conversation conv = conversationRepo.save(
                Conversation.builder()
                        .famille(famille)
                        .type(TypeConversation.PRIVE)
                        .nom(null)
                        .creePar(utilisateurRepo.findById(u1).orElseThrow())
                        .build()
        );

        participantRepo.save(
                ParticipantConversation.builder()
                        .conversation(conv)
                        .utilisateur(utilisateurRepo.getReferenceById(u1))
                        .build()
        );

        participantRepo.save(
                ParticipantConversation.builder()
                        .conversation(conv)
                        .utilisateur(utilisateurRepo.getReferenceById(u2))
                        .build()
        );

        return conv;
    }


    //Envoi de message
    @Override
    public Message envoyerMessage(
            Long conversationId,
            Long expediteurId,
            String contenu,
            TypeMessage type,
            String urlMedia
    ) {

        if (!participantRepo.existsByConversationIdAndUtilisateurId(
                conversationId, expediteurId))
            throw new RuntimeException("Utilisateur non autorisé");

        Message msg = messageRepo.save(
                Message.builder()
                        .conversation(conversationRepo.getReferenceById(conversationId))
                        .expediteur(utilisateurRepo.getReferenceById(expediteurId))
                        .contenu(contenu)
                        .type(type)
                        .urlMedia(urlMedia)
                        .build()
        );

        return msg;
    }

    //Lire message paginé
    @Override
    public Page<Message> lireMessages(
            Long conversationId,
            int page,
            int size
    ) {
        return messageRepo.findByConversationIdOrderByDateEnvoiDesc(
                conversationId,
                (Pageable) PageRequest.of(page,size)
        );
    }


    //Marquer comme lu
    @Override
    public void marquerCommeLu(Long messageId, Long userId) {

        MessageLuId id = new MessageLuId(messageId,userId);

        if(messageLuRepo.existsById(id))
            return;

        messageLuRepo.save(
                MessageLu.builder()
                        .id(id)
                        .message(messageRepo.getReferenceById(messageId))
                        .utilisateur(utilisateurRepo.getReferenceById(userId))
                        .build()
        );
    }

    //Conversation Utilsateur
    @Override
    public List<Conversation> conversationsUtilisateur(Long utilisateurId) {

        return participantRepo.findByUtilisateurId(utilisateurId)
                .stream()
                .map(ParticipantConversation::getConversation)
                .toList();
    }
}