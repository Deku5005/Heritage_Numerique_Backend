package com.heritage.controller;

import com.heritage.dto.SendMessageDTO;
import com.heritage.entite.Message;
import com.heritage.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageDTO dto) {

        Message message = chatService.envoyerMessage(
                dto.getConversationId(),
                dto.getExpediteurId(),
                dto.getContenu(),
                dto.getType(),
                dto.getUrlMedia()
        );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + dto.getConversationId(),
                message
        );
    }
}
