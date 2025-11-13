package com.heritage.config;

import com.heritage.entite.Famille;
import com.heritage.entite.Utilisateur;
import com.heritage.repository.FamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initialise un superAdmin par défaut au démarrage de l'application.
 * Crée également une famille virtuelle "PUBLIC" pour les contenus publics.
 */
@Component
public class SuperAdminInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private FamilleRepository familleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer le superAdmin s'il n'existe pas
        if (!utilisateurRepository.findByEmail("diakitetenin99@gmail.com").isPresent()) {
            Utilisateur superAdmin = new Utilisateur();
            superAdmin.setNom("Diakité");
            superAdmin.setPrenom("Niakalé");
            superAdmin.setEmail("diakitetenin99@gmail.com");
            superAdmin.setMotDePasse(passwordEncoder.encode("admin123"));
            superAdmin.setRole("ROLE_ADMIN");
            superAdmin.setActif(true);
            superAdmin.setDateCreation(LocalDateTime.now());
            superAdmin.setDateModification(LocalDateTime.now());

            utilisateurRepository.save(superAdmin);
            System.out.println("✓ SuperAdmin créé avec succès");
            System.out.println("  Email: diakitetenin99@gmail.com");
            System.out.println("  Mot de passe: admin123");
        }
    }
}

