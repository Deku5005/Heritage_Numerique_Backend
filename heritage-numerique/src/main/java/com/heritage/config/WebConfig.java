package com.heritage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure les ressources statiques pour mapper le chemin /uploads/**
 * au répertoire physique où les images sont stockées.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mappe l'URL /uploads/** à l'emplacement physique 'file:uploads/'
        // L'URL file:uploads/ est relative au répertoire de travail de l'application Spring Boot.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
