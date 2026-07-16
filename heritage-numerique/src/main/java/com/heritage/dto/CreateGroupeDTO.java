package com.heritage.dto;

import java.util.List;

public class CreateGroupeDTO {

    private Long familleId;
    private String nom;
    private Long createurId;
    private List<Long> participantsIds;

    public Long getFamilleId() {
        return familleId;
    }

    public void setFamilleId(Long familleId) {
        this.familleId = familleId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getCreateurId() {
        return createurId;
    }

    public void setCreateurId(Long createurId) {
        this.createurId = createurId;
    }

    public List<Long> getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(List<Long> participantsIds) {
        this.participantsIds = participantsIds;
    }
}
