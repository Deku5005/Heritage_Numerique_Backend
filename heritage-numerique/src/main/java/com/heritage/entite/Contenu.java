package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un contenu multimédia partagé dans une famille.
 * Peut être une photo, vidéo, document, audio ou texte.
 */
@Entity
@Table(name = "contenu", indexes = {
    @Index(name = "idx_famille", columnList = "id_famille"),
    @Index(name = "idx_auteur", columnList = "id_auteur"),
    @Index(name = "idx_categorie", columnList = "id_categorie"),
    @Index(name = "idx_statut", columnList = "statut"),
    @Index(name = "idx_date_evenement", columnList = "date_evenement")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = true)
    private Famille famille;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_auteur", nullable = false)
    private Utilisateur auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie", nullable = false)
    private Categorie categorie;

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type_contenu", nullable = false, length = 20)
    private String typeContenu;

    @Column(name = "url_fichier", length = 500)
    private String urlFichier;

    @Column(name = "url_photo", length = 500)
    private String urlPhoto;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    @Column(name = "duree")
    private Integer duree;

    @Column(name = "date_evenement")
    private LocalDate dateEvenement;

    @Column(name = "lieu", length = 255)
    private String lieu;

    @Column(name = "region", length = 100)
    private String region;


    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "BROUILLON";


    @Column(name = "texte_proverbe", columnDefinition = "TEXT")
    private String texteProverbe;


    @Column(name = "signification_proverbe", columnDefinition = "TEXT")
    private String significationProverbe;


    @Column(name = "origine_proverbe", length = 255)
    private String origineProverbe;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    // Note: Les traductions sont maintenant gérées via l'API HuggingFace NLLB-200
    // Plus besoin de l'entité TraductionContenu

    @OneToMany(mappedBy = "contenu", cascade = CascadeType.ALL)
    private List<DemandePublication> demandesPublication = new ArrayList<>();

    // Méthode manquante pour compatibilité
    public String getContenu() {
        return this.description;
    }
}

