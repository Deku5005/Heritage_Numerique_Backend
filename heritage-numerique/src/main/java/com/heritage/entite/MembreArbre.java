package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un membre (personne) dans un arbre généalogique.
 * Peut être lié à un utilisateur réel ou être une personne historique.
 */
@Entity
@Table(name = "membre_arbre", indexes = {
    @Index(name = "idx_arbre", columnList = "id_arbre"),
    @Index(name = "idx_pere", columnList = "id_pere"),
    @Index(name = "idx_mere", columnList = "id_mere"),
    @Index(name = "idx_utilisateur_lie", columnList = "id_utilisateur_lie")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembreArbre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arbre", nullable = false)
    private ArbreGenealogique arbre;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "sexe", length = 10)
    private String sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_deces")
    private LocalDate dateDeces;

    @Column(name = "lieu_naissance", length = 255)
    private String lieuNaissance;

    @Column(name = "lieu_deces", length = 255)
    private String lieuDeces;

    /**
     * Référence au père dans l'arbre généalogique.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pere")
    private MembreArbre pere;

    /**
     * Référence à la mère dans l'arbre généalogique.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mere")
    private MembreArbre mere;

    /**
     * Lien optionnel vers un utilisateur réel de l'application.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur_lie")
    private Utilisateur utilisateurLie;

    @Column(name = "biographie", columnDefinition = "TEXT")
    private String biographie;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "relation_familiale", length = 100)
    private String relationFamiliale;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    /**
     * Méthode utilitaire pour obtenir le nom complet.
     * @return Le nom complet (nom + prénom)
     */
    public String getNomComplet() {
        if (prenom == null || prenom.isEmpty()) {
            return nom;
        }
        return nom + " " + prenom;
    }
}

