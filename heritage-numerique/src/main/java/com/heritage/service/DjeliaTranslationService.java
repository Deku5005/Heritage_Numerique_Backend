package com.heritage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service de traduction synchrone utilisant l'API Djelia (https://djelia.cloud).
 * Ce service utilise WebClient mais bloque l'appel pour s'intégrer dans une architecture synchrone existante.
 */
@Service
public class DjeliaTranslationService {

    private final WebClient webClient;
    // NOTE: Codes de langue Djelia - format: {langue}_{script}
    // - Français: fra_Latn
    // - Anglais: eng_Latn (au lieu de "en" qui pourrait ne pas être supporté)
    // - Bambara: bam_Latn
    private static final Set<String> LANGUES_CIBLES = Set.of("fra_Latn", "eng_Latn", "bam_Latn");

    @Value("${djelia.api.key}")  // Clé depuis application.properties
    private String apiKey;

    public DjeliaTranslationService(WebClient.Builder webClientBuilder) {
        // La Base URL est le préfixe de l'API : https://djelia.cloud/api/v1
        this.webClient = webClientBuilder
                .baseUrl("https://djelia.cloud/api/v1") // Base URL pour l'API version 1
                .build();
    }

    /**
     * Traduit un texte de la langue source vers une langue cible unique.
     * Cet appel est BLOQUANT (synchrone).
     * 
     * Documentation Djelia API:
     * - Endpoint: POST /api/v1/models/translate
     * - Header: x-api-key (requis)
     * - Body: JSON avec "text", "source", "target"
     * - Response: JSON avec "translated_text" ou champ similaire
     *
     * @param text Texte à traduire.
     * @param source Langue source (codes Djelia: 'fra_Latn', 'eng_Latn', 'bam_Latn').
     * @param target Langue cible (codes Djelia: 'fra_Latn', 'eng_Latn', 'bam_Latn').
     * @return Texte traduit ou le texte original en cas d'erreur.
     */
    public String translate(String text, String source, String target) {
        if (text == null || text.trim().isEmpty() || source.equals(target)) {
            return text;
        }

        // Validation de l'API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("❌ Clé API Djelia non configurée dans application.properties");
            return text; // Fallback
        }

        try {
            // Construction du body selon la documentation Djelia
            // Note: Le champ "version" peut ne pas être requis selon la doc
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text.trim());
            requestBody.put("source", source);
            requestBody.put("target", target);
            // Le champ "version" est retiré car généralement non requis si la version est dans l'URL

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri("/models/translate") // Endpoint complet: https://djelia.cloud/api/v1/models/translate
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    // Gère les erreurs HTTP (4xx, 5xx) en lançant une exception
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        System.err.println("❌ Erreur Djelia HTTP: " + clientResponse.statusCode());
                        // Essayer de lire le body d'erreur pour plus de détails
                        return clientResponse.createException();
                    })
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(3)); // Timeout augmenté à 15 secondes pour les traductions longues

            // Vérification de la réponse - Djelia peut utiliser différents noms de champs
            if (response != null) {
                // Chercher différents noms de champs possibles pour la traduction
                if (response.containsKey("translated_text")) {
                    Object translated = response.get("translated_text");
                    return translated != null ? translated.toString() : text;
                } else if (response.containsKey("text")) {
                    // Certaines APIs retournent simplement "text"
                    Object translated = response.get("text");
                    return translated != null ? translated.toString() : text;
                } else if (response.containsKey("translation")) {
                    // Alternative possible
                    Object translated = response.get("translation");
                    return translated != null ? translated.toString() : text;
                } else {
                    // Debug: afficher la structure de la réponse pour diagnostic
                    System.err.println("⚠️ Réponse Djelia inattendue - Clés disponibles: " + response.keySet());
                    System.err.println("⚠️ Réponse complète: " + response);
                }
            }
            
            System.err.println("❌ Réponse Djelia vide ou inattendue pour " + source + " -> " + target);
            return text; // Fallback
            
        } catch (WebClientResponseException e) {
            // AFFICHAGE DU CODE DE STATUT HTTP EXACT POUR LE DIAGNOSTIC
            System.err.println("❌ Erreur HTTP Djelia (" + source + " -> " + target + "):");
            System.err.println("   Code: " + e.getRawStatusCode());
            System.err.println("   Status: " + e.getStatusCode());
            System.err.println("   Message: " + e.getMessage());
            
            // Essayer d'extraire le body d'erreur pour plus de détails
            try {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    System.err.println("   Body: " + responseBody);
                }
            } catch (Exception ignored) {
                // Ignorer si on ne peut pas lire le body
            }

            // Les causes probables sont:
            // 401/403: Problème de clé API (vérifier djelia.api.key dans application.properties)
            // 400: Problème de format de données (codes de langue, corps de la requête)
            // 429: Rate limit dépassé
            // 500/502/503: Erreur serveur Djelia

            return text; // Fallback - retourne le texte original
        } catch (Exception e) {
            System.err.println("❌ Erreur générale lors de la traduction (" + source + " -> " + target + "): " + e.getMessage());
            e.printStackTrace(); // Stack trace pour diagnostic
            return text; // Fallback
        }
    }

    /**
     * Traduit un texte vers toutes les langues cibles requises.
     *
     * @param texte Texte à traduire.
     * @param langueSource Langue source du texte.
     * @return Map<String (code langue), String (texte traduit)>.
     */
    public Map<String, String> traduireTout(String texte, String langueSource) {
        Map<String, String> traductions = new HashMap<>();

        if (texte == null || texte.trim().isEmpty()) {
            return traductions;
        }

        for (String cible : LANGUES_CIBLES) {
            // Si la langue cible est la même que la source, on utilise le texte original
            if (cible.equals(langueSource)) {
                traductions.put(cible, texte);
            } else {
                String traduction = this.translate(texte, langueSource, cible);
                traductions.put(cible, traduction);
            }
        }
        return traductions;
    }
}