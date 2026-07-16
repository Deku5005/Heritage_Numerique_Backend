package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.entite.Conversation;
import com.heritage.entite.Message;
import com.heritage.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Gestion des conversations et messages")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Créer un groupe de discussion")
    @PostMapping("/groupe")
    public Conversation createGroupe(@RequestBody CreateGroupeDTO dto) {

        return chatService.creerGroupe(
                dto.getFamilleId(),
                dto.getNom(),
                dto.getCreateurId(),
                dto.getParticipantsIds()
        );
    }

    @Operation(summary = "Créer une conversation privée")
    @PostMapping("/prive")
    public Conversation createPrive(@RequestBody CreatePriveDTO dto) {

        return chatService.creerPrive(
                dto.getUser1(),
                dto.getUser2(),
                dto.getFamilleId()
        );
    }

    @Operation(summary = "Envoyer un message")
    @PostMapping("/message")
    public Message sendMessage(@RequestBody SendMessageDTO dto) {

        return chatService.envoyerMessage(
                dto.getConversationId(),
                dto.getExpediteurId(),
                dto.getContenu(),
                dto.getType(),
                dto.getUrlMedia()
        );
    }

    @Operation(summary = "Lire les messages d'une conversation")
    @GetMapping("/messages/{conversationId}")
    public Page<Message> getMessages(
            @PathVariable Long conversationId,
            @RequestParam int page,
            @RequestParam int size
    ) {

        return chatService.lireMessages(
                conversationId,
                page,
                size
        );
    }

    @Operation(summary = "Récupérer toutes les conversations d'un utilisateur")
    @GetMapping("/conversations/{userId}")
    public List<Conversation> getConversations(@PathVariable Long userId) {

        return chatService.conversationsUtilisateur(userId);
    }

}