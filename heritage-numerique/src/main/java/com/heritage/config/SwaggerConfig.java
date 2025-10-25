package com.heritage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI 3 pour la documentation automatique de l'API.
 * 
 * Cette configuration génère automatiquement la documentation interactive
 * accessible via http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuration de l'API OpenAPI 3.
     * 
     * @return Configuration OpenAPI complète
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.1")
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    /**
     * Informations générales sur l'API.
     * 
     * @return Informations de l'API
     */
    private Info apiInfo() {
        return new Info()
                .title("Heritage Numérique API")
                .description("""
                        API REST pour la gestion du patrimoine familial numérique.
                        
                        ## Fonctionnalités principales :
                        - **Authentification** : Inscription, connexion, gestion des rôles
                        - **Gestion des familles** : Création, invitation de membres, gestion des rôles
                        - **Contenus culturels** : Contes, artisanats, proverbes, devinettes
                        - **Arbre généalogique** : Gestion des relations familiales
                        - **Quiz interactifs** : Quiz basés sur les contenus culturels
                        - **Traduction automatique** : Support multilingue (Français, Bambara, Anglais)
                        - **Dashboard** : Statistiques personnelles et familiales
                        
                        ## Authentification :
                        L'API utilise JWT (JSON Web Token) pour l'authentification.
                        Incluez le token dans l'en-tête Authorization : `Bearer {token}`
                        
                        ## Rôles :
                        - **ROLE_ADMIN** : Super-administrateur (accès global)
                        - **ROLE_MEMBRE** : Utilisateur standard
                        - **ADMIN** : Administrateur de famille
                        - **EDITEUR** : Éditeur de contenu familial
                        - **LECTEUR** : Lecteur de contenu familial
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Équipe Heritage Numérique")
                        .email("support@heritage-numerique.com")
                        .url("https://heritage-numerique.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Configuration des serveurs.
     * 
     * @return Liste des serveurs disponibles
     */
    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Serveur de développement local"),
                new Server()
                        .url("https://api.heritage-numerique.com")
                        .description("Serveur de production")
        );
    }

    /**
     * Configuration du schéma de sécurité JWT.
     * 
     * @return Schéma de sécurité pour JWT
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("""
                        Authentification JWT requise.
                        
                        Pour utiliser l'API :
                        1. Inscrivez-vous ou connectez-vous via `/api/auth/register` ou `/api/auth/login`
                        2. Copiez le token JWT de la réponse
                        3. Cliquez sur le bouton "Authorize" en haut à droite
                        4. Entrez : `Bearer {votre_token}`
                        5. Cliquez sur "Authorize"
                        
                        Le token est valide pendant 10 heures.
                        """);
    }
}
