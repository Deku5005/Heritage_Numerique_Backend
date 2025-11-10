package com.heritage.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure un Resource Handler pour mapper l'URL publique /uploads/**, /images/**, et /photo/**
 * vers l'emplacement physique où les fichiers sont stockés sur le serveur.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Injecte le chemin de base défini dans application.properties
    // Ex: file.upload-dir=C:/.../heritage-numerique/src/main/java/com/heritage/uploads
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Le chemin public standard (ex: /uploads/)
    private static final String PUBLIC_URL_PATH = "/uploads/";

    // Le chemin que Flutter va utiliser directement pour les images (ex: /images/uuid.jpg)
    private static final String IMAGES_URL_PATH = "/images/";

    // *** NOUVEAU CHEMIN : Ajout pour supporter le chemin "/photo/" vu dans l'erreur API ***
    private static final String PHOTO_URL_PATH = "/photo/";


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Assure que le chemin physique est formaté correctement (file:///)
        // Important: Ajout d'un '/' à la fin pour s'assurer que c'est un répertoire (C:/.../uploads/)
        String fileLocationUri = "file:///" + uploadDir.replace("\\", "/") + "/";

        // --- 1. Gestionnaire pour le chemin original /uploads/** ---
        registry
                .addResourceHandler(PUBLIC_URL_PATH + "**")
                .addResourceLocations(fileLocationUri)
                .setCachePeriod(3600);

        // --- 2. Gestionnaire pour /images/** (Chemin physique : file:///[uploadDir]/images/) ---
        registry
                .addResourceHandler(IMAGES_URL_PATH + "**")
                .addResourceLocations(fileLocationUri + "images/")
                .setCachePeriod(3600);

        // --- 3. 🔑 NOUVEAU GESTIONNAIRE : Pour /photo/** ---
        // Si l'API renvoie des chemins du type /photo/..., nous devons le mapper.
        // Hypothèse: les photos sont stockées dans [uploadDir]/photo/
        registry
                .addResourceHandler(PHOTO_URL_PATH + "**")
                .addResourceLocations(fileLocationUri + "photo/")
                .setCachePeriod(3600);


        System.out.println("Resource Handler Configured: URL Path '" + PUBLIC_URL_PATH + "**' maps to Physical Location '" + fileLocationUri + "'");
        System.out.println("Resource Handler Corrigé: URL Path '" + IMAGES_URL_PATH + "**' maps to Physical Location '" + fileLocationUri + "images/'");
        System.out.println("Resource Handler NOUVEAU: URL Path '" + PHOTO_URL_PATH + "**' maps to Physical Location '" + fileLocationUri + "photo/'");
    }
}