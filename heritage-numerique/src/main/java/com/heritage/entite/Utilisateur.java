package com.heritage.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un utilisateur de l'application.
 * Stocke les informations d'authentification (email, mot de passe hashé)
 * et les métadonnées utilisateur (nom, prénom, rôle).
 * 
 * Sécurité : Le mot de passe est stocké hashé avec BCrypt (jamais en clair).
 */
@Entity
@Table(name = "utilisateur", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    @Column(name = "ethnie", length = 100)
    private String ethnie;

    /**
     * Mot de passe hashé avec BCrypt.
     * Ne JAMAIS stocker ni renvoyer le mot de passe en clair.
     */
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    /**
     * Rôle global de l'utilisateur dans l'application.
     * ROLE_ADMIN : administrateur système
     * ROLE_MEMBRE : utilisateur standard
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role = "ROLE_MEMBRE";

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    /**
     * Liste des appartenances aux familles.
     * Un utilisateur peut appartenir à plusieurs familles avec des rôles différents.
     */
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembreFamille> membresFamille = new ArrayList<>();

    /**
     * Invitations envoyées par cet utilisateur.
     */
    @OneToMany(mappedBy = "emetteur", cascade = CascadeType.ALL)
    private List<Invitation> invitationsEnvoyees = new ArrayList<>();

    /**
     * Contenus créés par cet utilisateur.
     */
    @OneToMany(mappedBy = "auteur")
    private List<Contenu> contenus = new ArrayList<>();

    /**
     * Quiz créés par cet utilisateur.
     */
    @OneToMany(mappedBy = "createur")
    private List<Quiz> quizCrees = new ArrayList<>();

    /**
     * Résultats de quiz de cet utilisateur.
     */
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<ResultatQuiz> resultatsQuiz = new ArrayList<>();

    // Méthodes manquantes pour compatibilité
    public int getScore() {
        return resultatsQuiz.stream()
                .mapToInt(ResultatQuiz::getScore)
                .sum();
    }
}

