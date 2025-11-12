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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import static org.springframework.http.HttpMethod.OPTIONS;

/**
 * Configuration de sécurité Spring Security.
 * Combine la gestion JWT, les règles d'autorisation spécifiques et une configuration CORS détaillée.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // --- Configuration CORS ---

    /**
     * Définit les règles de partage de ressources (CORS) pour le navigateur.
     * Basé sur la configuration détaillée de votre deuxième fichier.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriser l'accès depuis l'application frontend (Angular/React/Vue sur localhost:4200)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Autoriser les méthodes HTTP usuelles
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Autoriser les headers nécessaires (y compris Authorization pour le JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        // Autoriser l'envoi de cookies/informations d'identification (si besoin, bien que JWT soit dans le header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Appliquer cette configuration à tous les chemins (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // --- Chaîne de Filtres de Sécurité (SecurityFilterChain) ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuration CORS : Utilise le Bean corsConfigurationSource défini ci-dessus
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Désactiver CSRF (non nécessaire pour API REST avec JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Configuration des autorisations (EndPoints)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics (authentification)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoints publics (Contenus) - Inclus du premier fichier
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/contenus/public/**").permitAll()
                        .requestMatchers("/api/superadmin/contenus-publics/contes").permitAll()
                        .requestMatchers("/api/superadmin/contenus-publics/artisanats").permitAll()
                        .requestMatchers("/api/superadmin/contenus-publics/proverbes").permitAll()
                        .requestMatchers("/api/superadmin/contenus-publics/devinettes").permitAll()

                        // Accès aux fichiers statiques (images, PDF)
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // Autoriser toutes les requêtes OPTIONS (preflight CORS) - Inclus du deuxième fichier
                        .requestMatchers(OPTIONS, "/**").permitAll()

                        // Documentation Swagger/OpenAPI
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // Endpoints admin (réservés aux ROLE_ADMIN)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints superadmin (du deuxième fichier)
                        .requestMatchers("/api/superadmin/**").hasRole("ADMIN") // Assumer que 'superadmin' utilise aussi ROLE_ADMIN

                        // Tous les autres endpoints nécessitent une authentification
                        .anyRequest().authenticated()
                )

                // 4. Gestion des sessions : STATELESS (pas de session HTTP)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Provider d'authentification
                .authenticationProvider(authenticationProvider())

                // 6. Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // --- Beans d'Authentification et de Cryptage ---

    /**
     * Provider d'authentification personnalisé.
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
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder avec BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}