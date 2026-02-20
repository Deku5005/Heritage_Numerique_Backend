package com.heritage.entite;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conversation")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_expediteur")
    private Utilisateur expediteur;

    @Lob
    private String contenu;

    @Enumerated(EnumType.STRING)
    private TypeMessage type;

    private String urlMedia;

    private Boolean modifie = false;
    private Boolean supprime = false;

    private LocalDateTime dateEnvoi;

    @PrePersist
    void prePersist() {
        dateEnvoi = LocalDateTime.now();
    }

    public Message() {}

    public Message(Long id, Conversation conversation, Utilisateur expediteur, String contenu, TypeMessage type, String urlMedia, Boolean modifie, Boolean supprime, LocalDateTime dateEnvoi) {
        this.id = id;
        this.conversation = conversation;
        this.expediteur = expediteur;
        this.contenu = contenu;
        this.type = type;
        this.urlMedia = urlMedia;
        this.modifie = modifie;
        this.supprime = supprime;
        this.dateEnvoi = dateEnvoi;
    }
}
