package com.heritage.entite;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_lu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageLu {

    @EmbeddedId
    private MessageLuId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idMessage")
    @JoinColumn(name = "id_message")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUtilisateur")
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    private LocalDateTime dateLecture;

    @PrePersist
    void init() {
        dateLecture = LocalDateTime.now();
    }
}
