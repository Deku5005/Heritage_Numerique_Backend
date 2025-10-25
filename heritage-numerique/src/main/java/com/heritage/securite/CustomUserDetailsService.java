package com.heritage.securite;

import com.heritage.entite.Utilisateur;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service personnalisé pour charger les détails d'un utilisateur pour Spring Security.
 * Implémente UserDetailsService pour l'authentification.
 * 
 * Responsabilités :
 * - Charger un utilisateur par email (username)
 * - Convertir l'entité Utilisateur en UserDetails pour Spring Security
 * - Définir les autorités (rôles) de l'utilisateur
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Charge un utilisateur par son email.
     * Appelé par Spring Security lors de l'authentification.
     * 
     * Retourne un UserPrincipal personnalisé qui contient l'ID utilisateur,
     * permettant d'accéder facilement à l'ID dans les controllers.
     * 
     * @param email Email de l'utilisateur (utilisé comme username)
     * @return UserDetails (UserPrincipal) contenant les informations d'authentification
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche l'utilisateur dans la base de données
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));

        // Vérifie si le compte est actif
        if (!utilisateur.getActif()) {
            throw new UsernameNotFoundException("Compte utilisateur désactivé : " + email);
        }

        // Retourne un UserPrincipal avec l'ID inclus
        // Cela permet de récupérer l'ID directement depuis l'Authentication
        // sans avoir à faire une requête en BDD
        return new UserPrincipal(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(), // Déjà hashé avec BCrypt
                utilisateur.getRole(),
                utilisateur.getActif()
        );
    }

    /**
     * Charge un utilisateur par son ID et retourne l'entité complète.
     * Utile pour récupérer les informations complètes après authentification.
     * 
     * @param userId ID de l'utilisateur
     * @return Entité Utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    public Utilisateur loadUserById(Long userId) throws UsernameNotFoundException {
        return utilisateurRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'ID : " + userId));
    }
}


