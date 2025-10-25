package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une demande de publication de contenu.
 * Implémente un workflow de validation avant publication.
 */
@Entity
@Table(name = "demande_publication", indexes = {
    @Index(name = "idx_contenu", columnList = "id_contenu"),
    @Index(name = "idx_demandeur", columnList = "id_demandeur"),
    @Index(name = "idx_statut", columnList = "statut")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandePublication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contenu", nullable = false)
    private Contenu contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Utilisateur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_valideur")
    private Utilisateur valideur;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "EN_ATTENTE";

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @CreationTimestamp
    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;
}

