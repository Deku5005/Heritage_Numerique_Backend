package com.heritage.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DjeliaTranslationService {

    private final WebClient webClient;

    @Value("${djelia.api.key}")  // Ta clé depuis application.properties
    private String apiKey;

    public DjeliaTranslationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://djelia.cloud/api/v1").build();
    }

    /**
     * Traduit un texte via Djelia API
     * @param text Texte à traduire
     * @param source Langue source ('fr', 'en', 'bm')
     * @param target Langue cible ('fr', 'en', 'bm')
     * @return Texte traduit ou original si erreur
     */
    public Mono<String> translate(String text, String source, String target) {
        if (text == null || text.trim().isEmpty()) {
            return Mono.just(text);
        }

        return webClient.post()
                .uri("/translate")  // Endpoint confirmé par doc ReDoc
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-api-key", apiKey)
                .bodyValue(Map.of(
                        "text", text.trim(),
                        "source", source,
                        "target", target,
                        "version", "v1"  // Optionnel mais recommandé
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response.containsKey("translated_text")) {
                        return (String) response.get("translated_text");
                    }
                    return text;  // Fallback
                })
                .onErrorReturn(text);  // Erreur → fallback
    }
}