package com.heritage.securite;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 * 
 * Responsabilités :
 * - Intercepter chaque requête HTTP
 * - Extraire le token JWT du header Authorization
 * - Valider le token
 * - Charger les détails de l'utilisateur
 * - Définir l'authentification dans le SecurityContext
 * 
 * Sécurité :
 * - Le token est extrait du header "Authorization: Bearer <token>"
 * - Validation stricte du token (signature, expiration)
 * - Si le token est invalide, la requête continue sans authentification (gérée par SecurityConfig)
 * 
 * Note : Ce filtre s'exécute avant UsernamePasswordAuthenticationFilter dans la chaîne de sécurité.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtre principal qui s'exécute pour chaque requête.
     * 
     * Processus :
     * 1. Extraire le token du header Authorization
     * 2. Si token présent, extraire le username (email)
     * 3. Si username extrait et pas déjà authentifié :
     *    a. Charger les UserDetails
     *    b. Valider le token
     *    c. Si valide, créer l'authentification et la définir dans SecurityContext
     * 4. Continuer la chaîne de filtres
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraire le header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérifier si le header est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pas de token, continuer sans authentification
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraire le token (enlever "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 3. Extraire le username (email) du token
            userEmail = jwtService.extractUsername(jwt);

            // 4. Si username présent et utilisateur pas déjà authentifié
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 5. Charger les détails de l'utilisateur depuis la base de données
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Valider le token
                if (jwtService.validateToken(jwt, userDetails)) {
                    
                    // 7. Créer l'objet d'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // credentials (pas besoin, déjà authentifié via JWT)
                            userDetails.getAuthorities()
                    );

                    // Ajouter les détails de la requête
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8. Définir l'authentification dans le SecurityContext
                    // À partir de ce point, Spring Security considère l'utilisateur comme authentifié
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalide ou expiré
            // Log l'erreur et continuer sans authentification
            logger.error("Erreur lors de la validation du JWT: " + e.getMessage());
        }

        // 9. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}


