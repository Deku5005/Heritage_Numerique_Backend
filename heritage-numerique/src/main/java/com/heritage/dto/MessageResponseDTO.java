package com.heritage.dto;

import java.time.LocalDateTime;

public class MessageResponseDTO {

    private Long id;
    private Long conversationId;
    private Long expediteurId;
    private String contenu;
    private String type;
    private String urlMedia;
    private Boolean modifie;
    private Boolean supprime;
    private LocalDateTime dateEnvoi;

    public void setId(Long id) {
    }

    public void setConversationId(Long id) {
    }

    public Long getId() {
        return id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getExpediteurId() {
        return expediteurId;
    }

    public String getContenu() {
        return contenu;
    }

    public String getType() {
        return type;
    }

    public String getUrlMedia() {
        return urlMedia;
    }

    public Boolean getModifie() {
        return modifie;
    }

    public Boolean getSupprime() {
        return supprime;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setExpediteurId(Long expediteurId) {
        this.expediteurId = expediteurId;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrlMedia(String urlMedia) {
        this.urlMedia = urlMedia;
    }

    public void setModifie(Boolean modifie) {
        this.modifie = modifie;
    }

    public void setSupprime(Boolean supprime) {
        this.supprime = supprime;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
