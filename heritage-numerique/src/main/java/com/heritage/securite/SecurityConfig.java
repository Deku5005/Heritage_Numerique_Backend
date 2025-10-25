package com.heritage.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de sécurité Spring Security.
 * 
 * Responsabilités :
 * - Configurer l'authentification JWT
 * - Définir les endpoints publics et protégés
 * - Configurer le PasswordEncoder (BCrypt)
 * - Désactiver CSRF pour les API REST
 * - Configurer la gestion des sessions (STATELESS)
 * 
 * Sécurité :
 * - CSRF désactivé : Pour les API REST avec JWT, CSRF n'est pas nécessaire car
 *   les tokens JWT ne sont pas stockés dans les cookies (pas de vulnérabilité CSRF)
 * - Sessions STATELESS : Pas de session HTTP, authentification par JWT à chaque requête
 * - BCrypt pour le hashage des mots de passe (algorithme sécurisé avec salt automatique)
 * - JWT Filter ajouté avant UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Pour @PreAuthorize, @Secured, etc.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configuration de la chaîne de filtres de sécurité.
     * 
     * Endpoints publics :
     * - /api/auth/** : inscription et connexion (pas d'authentification requise)
     * - /swagger-ui/** : documentation Swagger (optionnel, pour dev/test)
     * - /v3/api-docs/** : documentation OpenAPI
     * 
     * Endpoints protégés :
     * - Tous les autres endpoints nécessitent une authentification
     * 
     * CSRF désactivé :
     * Pour les API REST avec authentification JWT, CSRF n'est pas nécessaire.
     * Les tokens JWT sont envoyés dans le header Authorization (pas dans un cookie),
     * donc il n'y a pas de risque d'attaque CSRF (Cross-Site Request Forgery).
     * 
     * Sessions STATELESS :
     * Pas de session HTTP côté serveur, l'authentification se fait uniquement via JWT.
     * Chaque requête doit inclure le token JWT.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF (pas nécessaire pour API REST avec JWT)
            .csrf(csrf -> csrf.disable())
            
            // Configuration CORS
            .cors(cors -> cors.disable())
            
            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics (authentification)
                .requestMatchers("/api/auth/**").permitAll()

                
                // Documentation Swagger (optionnel, à sécuriser en production)
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                
                // Endpoints admin (réservés aux ROLE_ADMIN)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Gestion des sessions : STATELESS (pas de session HTTP)
            // L'authentification se fait uniquement via JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Provider d'authentification
            .authenticationProvider(authenticationProvider())
            
            // Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provider d'authentification personnalisé.
     * Utilise CustomUserDetailsService pour charger les utilisateurs
     * et BCryptPasswordEncoder pour vérifier les mots de passe.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager pour l'authentification programmatique.
     * Utilisé dans AuthService pour la connexion.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder avec BCrypt.
     * 
     * Sécurité :
     * - BCrypt est un algorithme de hashage sécurisé pour les mots de passe
     * - Génère automatiquement un salt unique pour chaque mot de passe
     * - Résistant aux attaques par rainbow tables et brute force
     * - Paramètre de coût (work factor) : 10 par défaut (2^10 itérations)
     * 
     * Pourquoi BCrypt :
     * - Spécialement conçu pour hasher des mots de passe (lent par design)
     * - Salt automatique (pas besoin de le gérer manuellement)
     * - Résistant aux attaques GPU (algorithme adaptatif)
     * - Standard de l'industrie pour le stockage de mots de passe
     * 
     * Alternative : Argon2 (encore plus moderne, mais BCrypt est largement accepté)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


