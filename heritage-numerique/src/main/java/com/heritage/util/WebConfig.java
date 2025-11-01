package com.heritage.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure un Resource Handler pour mapper l'URL publique /uploads/**
 * vers l'emplacement physique où les fichiers sont stockés sur le serveur.
 * Le chemin physique est lu depuis 'application.properties'.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Injecte le chemin de base défini dans application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Le chemin public dans l'URL (ex: /uploads/photo/image.png)
    private static final String PUBLIC_URL_PATH = "/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Assure que le chemin physique est formaté correctement pour les URI (file:///)
        // Important: Utiliser 'file:///' et des slashes pour le chemin absolu.
        // Remplace les backslashes Windows par des slashes pour la compatibilité URI
        String fileLocationUri = "file:///" + uploadDir.replace("\\", "/");

        // Mappe l'URL publique "/uploads/**" à l'emplacement physique des fichiers.
        registry
                .addResourceHandler(PUBLIC_URL_PATH + "**")
                .addResourceLocations(fileLocationUri)
                .setCachePeriod(3600); // Mise en cache des ressources pendant 1 heure

        System.out.println("Resource Handler Configured: URL Path '" + PUBLIC_URL_PATH + "**' maps to Physical Location '" + fileLocationUri + "'");
    }
}
