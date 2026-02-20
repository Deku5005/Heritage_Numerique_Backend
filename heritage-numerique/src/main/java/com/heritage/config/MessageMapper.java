package com.heritage.config;

import com.heritage.dto.MessageResponseDTO;
import com.heritage.entite.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toDTO(Message m){
        MessageResponseDTO dto = new MessageResponseDTO();

        dto.setId(m.getId());
        dto.setConversationId(m.getConversation().getId());
        dto.setExpediteurId(m.getExpediteur().getId());
        dto.setContenu(m.getContenu());
        dto.setType(m.getType().name());
        dto.setUrlMedia(m.getUrlMedia());
        dto.setModifie(m.getModifie());
        dto.setSupprime(m.getSupprime());
        dto.setDateEnvoi(m.getDateEnvoi());

        return dto;
    }
}
