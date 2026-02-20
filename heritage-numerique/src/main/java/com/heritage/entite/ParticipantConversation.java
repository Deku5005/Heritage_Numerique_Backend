package com.heritage.entite;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "participant_conversation",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_conversation","id_utilisateur"}
        )
)
@Getter
@Setter
@Builder
public class ParticipantConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conversation")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    private RoleParticipant role;

    private LocalDateTime dateJoin;

    @PrePersist
    void init() {
        dateJoin = LocalDateTime.now();
    }
    public ParticipantConversation() {}

    public ParticipantConversation(Long id, Conversation conversation, Utilisateur utilisateur, RoleParticipant role, LocalDateTime dateJoin) {
        this.id = id;
        this.conversation = conversation;
        this.utilisateur = utilisateur;
        this.role = role;
        this.dateJoin = dateJoin;
    }
}
