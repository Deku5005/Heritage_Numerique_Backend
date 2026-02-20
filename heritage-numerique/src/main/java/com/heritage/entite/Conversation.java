package com.heritage.entite;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
@Getter
@Setter
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Enumerated(EnumType.STRING)
    private TypeConversation type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = false)
    private Famille famille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par", nullable = false)
    private Utilisateur creePar;

    private LocalDateTime dateCreation;

    @PrePersist
    void prePersist() {
        dateCreation = LocalDateTime.now();
    }

    public Conversation(){

    }

    public Conversation(Long id, String nom, TypeConversation type, Famille famille, Utilisateur creePar, LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.famille = famille;
        this.creePar = creePar;
        this.dateCreation = dateCreation;
    }
}
