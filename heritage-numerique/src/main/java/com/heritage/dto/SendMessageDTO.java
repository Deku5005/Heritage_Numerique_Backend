package com.heritage.dto;

import com.heritage.entite.TypeMessage;

public class SendMessageDTO {

    private Long conversationId;
    private Long expediteurId;
    private String contenu;
    private TypeMessage type;
    private String urlMedia;
}
