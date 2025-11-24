package com.heritage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

/**
 * Service de synthèse vocale (Text-to-Speech) utilisant l'API Djelia (https://djelia.cloud).
 * Ce service convertit du texte en audio via l'API Djelia.
 */
@Service
public class DjeliaTextToSpeechService {

    private final WebClient webClient;
    
    // Codes de langue Djelia pour TTS - format: {langue}_{script}
    // - Français: fra_Latn
    // - Anglais: eng_Latn
    // - Bambara: bam_Latn

    @Value("${djelia.api.key}")  // Clé depuis application.properties
    private String apiKey;

    public DjeliaTextToSpeechService(WebClient.Builder webClientBuilder) {
        // Configuration du WebClient avec support SSL (ignorer la validation pour contourner les certificats expirés)
        // ATTENTION: Ceci est temporaire et devrait être retiré une fois que Djelia renouvelle son certificat SSL
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> {
                    try {
                        sslContextSpec.sslContext(
                                io.netty.handler.ssl.SslContextBuilder
                                        .forClient()
                                        .trustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE)
                                        .build()
                        );
                    } catch (SSLException e) {
                        System.err.println("⚠️ Erreur lors de la configuration SSL: " + e.getMessage());
                    }
                });

        // Configuration de la stratégie d'échange pour augmenter la limite de buffer
        // L'audio peut être volumineux, donc on augmente la limite à 10 MB (10 * 1024 * 1024 bytes)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        // La Base URL est le préfixe de l'API : https://djelia.cloud/api/v1
        this.webClient = webClientBuilder
                .baseUrl("https://djelia.cloud/api/v1") // Base URL pour l'API version 1
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * Convertit un texte en audio (synthèse vocale).
     * Cet appel est BLOQUANT (synchrone).
     * 
     * Documentation Djelia API TTS (supposée similaire à l'API de traduction):
     * - Endpoint: POST /api/v1/models/tts ou /api/v1/models/text-to-speech
     * - Header: x-api-key (requis)
     * - Body: JSON avec "text", "language" (optionnel: "voice", "speed", etc.)
     * - Response: Audio file (MP3, WAV, etc.) en bytes
     *
     * @param text Texte à convertir en audio.
     * @param language Langue du texte (codes Djelia: 'fra_Latn', 'eng_Latn', 'bam_Latn').
     * @return Bytes de l'audio généré (MP3 ou WAV), ou null en cas d'erreur.
     */
    public byte[] textToSpeech(String text, String language) {
        if (text == null || text.trim().isEmpty()) {
            System.err.println("⚠️ Texte vide pour la synthèse vocale");
            return null;
        }

        // Validation de l'API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("❌ Clé API Djelia non configurée dans application.properties");
            return null;
        }

        try {
            // Construction du body selon la documentation Djelia TTS
            // Note: L'endpoint exact peut varier selon la documentation Djelia
            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("text", text.trim());
            requestBody.put("language", language);
            // Options supplémentaires possibles:
            // requestBody.put("voice", "default"); // Voix spécifique
            // requestBody.put("speed", 1.0); // Vitesse de lecture
            // requestBody.put("format", "mp3"); // Format audio

            // Essayer d'abord l'endpoint /models/tts
            byte[] audioBytes = tryTtsEndpoint("/models/tts", requestBody);
            if (audioBytes != null) {
                return audioBytes;
            }

            // Si échec, essayer /models/text-to-speech
            audioBytes = tryTtsEndpoint("/models/text-to-speech", requestBody);
            if (audioBytes != null) {
                return audioBytes;
            }

            // Si les deux échouent, essayer /tts
            audioBytes = tryTtsEndpoint("/tts", requestBody);
            if (audioBytes != null) {
                return audioBytes;
            }

            System.err.println("❌ Aucun endpoint TTS Djelia n'a fonctionné");
            System.err.println("⚠️ NOTE: L'API Djelia TTS pourrait ne pas être disponible ou avoir un endpoint différent.");
            System.err.println("   Veuillez vérifier la documentation Djelia pour l'endpoint TTS correct.");
            return null;

        } catch (Exception e) {
            System.err.println("❌ Erreur générale lors de la synthèse vocale (" + language + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Essaie un endpoint TTS spécifique.
     */
    private byte[] tryTtsEndpoint(String endpoint, java.util.Map<String, Object> requestBody) {
        try {
            byte[] audioBytes = webClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        System.err.println("❌ Erreur Djelia TTS HTTP: " + clientResponse.statusCode() + " pour " + endpoint);
                        return clientResponse.createException();
                    })
                    .bodyToMono(byte[].class)
                    .block(Duration.ofSeconds(60)); // Timeout augmenté à 60 secondes pour la génération audio (TTS peut être long)

            if (audioBytes != null && audioBytes.length > 0) {
                System.out.println("✅ Audio généré avec succès (" + audioBytes.length + " bytes) via " + endpoint);
                return audioBytes;
            }

            return null;

        } catch (WebClientResponseException e) {
            // Si c'est une 404, c'est normal (endpoint n'existe pas), on essaie le suivant
            if (e.getStatusCode().value() == 404) {
                System.out.println("⚠️ Endpoint " + endpoint + " non trouvé (404), essai d'un autre endpoint...");
                return null;
            }

            // Si c'est un 200 mais avec une erreur de buffer, c'est que la réponse est trop grande
            if (e.getStatusCode().value() == 200 && 
                e.getMessage() != null && e.getMessage().contains("DataBufferLimitException")) {
                System.err.println("⚠️ Réponse trop grande pour le buffer (limite dépassée)");
                System.err.println("   La réponse audio est probablement très volumineuse");
                System.err.println("   Essayez de réduire la taille du texte ou contactez le support Djelia");
                return null;
            }

            // Pour les autres erreurs, afficher les détails
            System.err.println("❌ Erreur HTTP Djelia TTS (" + endpoint + "):");
            System.err.println("   Code: " + e.getRawStatusCode());
            System.err.println("   Status: " + e.getStatusCode());
            System.err.println("   Message: " + e.getMessage());

            try {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    System.err.println("   Body: " + responseBody);
                }
            } catch (Exception ignored) {
                // Ignorer si on ne peut pas lire le body
            }

            return null;

        } catch (org.springframework.core.io.buffer.DataBufferLimitException e) {
            // Limite de buffer dépassée - la réponse audio est trop grande
            System.err.println("⚠️ Limite de buffer dépassée pour " + endpoint);
            System.err.println("   La réponse audio est probablement très volumineuse (> 10 MB)");
            System.err.println("   Message: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // Vérifier si c'est un timeout
            Throwable cause = e.getCause();
            String message = e.getMessage();
            
            // Vérifier si c'est une erreur de buffer limit
            if (cause instanceof org.springframework.core.io.buffer.DataBufferLimitException ||
                (message != null && message.contains("DataBufferLimitException"))) {
                System.err.println("⚠️ Limite de buffer dépassée pour " + endpoint);
                System.err.println("   La réponse audio est probablement très volumineuse");
                return null;
            }
            
            if (cause instanceof java.util.concurrent.TimeoutException || 
                (message != null && (message.contains("Timeout") || message.contains("timeout")))) {
                System.err.println("⏱️ Timeout lors de l'appel à " + endpoint + ": " + message);
                System.err.println("   L'endpoint existe peut-être mais la génération audio prend trop de temps");
                return null;
            }
            
            // Vérifier si c'est une exception réactive avec un timeout
            if (cause != null && (cause instanceof java.util.concurrent.TimeoutException ||
                (cause.getMessage() != null && cause.getMessage().contains("Timeout")))) {
                System.err.println("⏱️ Timeout lors de l'appel à " + endpoint + ": " + cause.getMessage());
                return null;
            }
            
            System.err.println("❌ Erreur lors de l'appel à " + endpoint + ": " + message);
            if (cause != null) {
                System.err.println("   Cause: " + cause.getMessage());
            }
            return null;
        }
    }

    /**
     * Mappe les codes de langue courts (fr, en, bm) vers les codes Djelia (fra_Latn, eng_Latn, bam_Latn).
     * 
     * @param langCode Code de langue court
     * @return Code de langue Djelia correspondant
     */
    public String mapToDjeliaLanguageCode(String langCode) {
        if (langCode == null) {
            return "fra_Latn"; // Par défaut: français
        }
        switch (langCode.toLowerCase()) {
            case "fr":
            case "fra":
            case "fra_latn":
                return "fra_Latn";
            case "bm":
            case "bam":
            case "bam_latn":
                return "bam_Latn";
            case "en":
            case "eng":
            case "eng_latn":
                return "eng_Latn";
            default:
                // Si c'est déjà un code Djelia ou inconnu, retourner tel quel ou par défaut
                return langCode.contains("_") ? langCode : "fra_Latn";
        }
    }
}

