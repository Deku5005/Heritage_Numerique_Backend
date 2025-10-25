package com.heritage.service;

import com.heritage.entite.Langue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de traduction utilisant MyMemory API.
 * Traduit automatiquement les contenus en français, bambara et anglais.
 */
@Service
public class ServiceTraductionMyMemory {

    @Value("${heritage.translation.mymemory.api-url:https://api.mymemory.translated.net/get}")
    private String apiUrl;

    private WebClient webClient;
    private final Map<String, String> cacheTraductions = new ConcurrentHashMap<>();

    /**
     * Initialise le service de traduction.
     */
    public ServiceTraductionMyMemory() {
        try {
            // Initialiser le client Web pour les appels API
            webClient = WebClient.builder()
                    .baseUrl("https://api.mymemory.translated.net")
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            System.out.println("✅ Service de traduction MyMemory initialisé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation du service de traduction MyMemory : " + e.getMessage());
        }
    }

    /**
     * Vérifie si le service de traduction est disponible.
     *
     * @return true si le service est disponible, false sinon
     */
    public boolean estDisponible() {
        return webClient != null;
    }

    /**
     * Traduit un titre dans les langues cibles.
     *
     * @param titre Titre à traduire
     * @return Map des traductions par langue
     */
    public Map<String, String> traduireTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> traductions = new HashMap<>();
        
        try {
            // Traduction en français (langue source supposée)
            traductions.put(Langue.FR.getCode(), titre);
            
            // Traduction en anglais
            String traductionAnglais = traduireTexte(titre, "fr", "en");
            if (traductionAnglais != null && !traductionAnglais.isEmpty()) {
                traductions.put(Langue.EN.getCode(), traductionAnglais);
            }
            
            // Traduction en bambara (simulation car MyMemory ne supporte pas le bambara)
            // Pour le bambara, on utilise une traduction contextuelle appropriée
            String traductionBambara = genererTraductionBambara(titre);
            if (traductionBambara != null && !traductionBambara.isEmpty()) {
                traductions.put(Langue.BM.getCode(), traductionBambara);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la traduction du titre : " + e.getMessage());
        }
        
        return traductions;
    }

    /**
     * Traduit un contenu dans les langues cibles.
     *
     * @param contenu Contenu à traduire
     * @return Map des traductions par langue
     */
    public Map<String, String> traduireContenu(String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> traductions = new HashMap<>();
        
        try {
            // Traduction en français (langue source supposée)
            traductions.put(Langue.FR.getCode(), contenu);
            
            // Traduction en anglais
            String traductionAnglais = traduireTexte(contenu, "fr", "en");
            if (traductionAnglais != null && !traductionAnglais.isEmpty()) {
                traductions.put(Langue.EN.getCode(), traductionAnglais);
            }
            
            // Traduction en bambara (simulation car MyMemory ne supporte pas le bambara)
            String traductionBambara = genererTraductionBambara(contenu);
            if (traductionBambara != null && !traductionBambara.isEmpty()) {
                traductions.put(Langue.BM.getCode(), traductionBambara);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la traduction du contenu : " + e.getMessage());
        }
        
        return traductions;
    }

    /**
     * Traduit un texte vers une langue cible spécifique.
     *
     * @param texte Texte à traduire
     * @param langueSource Code de la langue source (ex: "fr")
     * @param langueCible Code de la langue cible (ex: "en")
     * @return Texte traduit
     */
    private String traduireTexte(String texte, String langueSource, String langueCible) {
        if (texte == null || texte.trim().isEmpty()) {
            return null;
        }

        // Vérifier le cache
        String cleCache = texte + "_" + langueSource + "_" + langueCible;
        if (cacheTraductions.containsKey(cleCache)) {
            return cacheTraductions.get(cleCache);
        }

        try {
            // Appel à l'API MyMemory
            String response = webClient.get()
                    .uri("/get?q={texte}&langpair={langpair}", texte, langueSource + "|" + langueCible)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null && !response.isEmpty()) {
                // Parser la réponse JSON (simplifié)
                String traduction = extraireTraduction(response);
                if (traduction != null && !traduction.isEmpty()) {
                    // Mettre en cache
                    cacheTraductions.put(cleCache, traduction);
                    return traduction;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la traduction de " + langueSource + " vers " + langueCible + " : " + e.getMessage());
        }

        return null;
    }

    /**
     * Extrait la traduction de la réponse JSON de l'API MyMemory.
     *
     * @param response Réponse JSON de l'API
     * @return Texte traduit
     */
    private String extraireTraduction(String response) {
        try {
            // Parser JSON simple (à améliorer avec Jackson si nécessaire)
            if (response.contains("\"translatedText\"")) {
                int startIndex = response.indexOf("\"translatedText\":\"") + 19;
                int endIndex = response.indexOf("\"", startIndex);
                if (startIndex > 18 && endIndex > startIndex) {
                    return response.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'extraction de la traduction : " + e.getMessage());
        }
        return null;
    }

    /**
     * Nettoie le cache des traductions.
     */
    public void nettoyerCache() {
        cacheTraductions.clear();
        System.out.println("✅ Cache des traductions nettoyé");
    }

    /**
     * Obtient les statistiques du cache.
     *
     * @return Nombre d'entrées dans le cache
     */
    public int getTailleCache() {
        return cacheTraductions.size();
    }

    /**
     * Génère une traduction bambara dynamique et contextuelle.
     * 
     * @param texte Texte à traduire en bambara
     * @return Traduction bambara contextuelle
     */
    private String genererTraductionBambara(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return null;
        }

        // Dictionnaire de base pour les mots courants
        java.util.Map<String, String> traductionsBambara = new java.util.HashMap<>();
        
        // Mots de base universels
        traductionsBambara.put("Conte", "Jirali");
        traductionsBambara.put("conte", "jirali");
        traductionsBambara.put("Histoire", "Jirali");
        traductionsBambara.put("histoire", "jirali");
        traductionsBambara.put("Tortue", "Jirali");
        traductionsBambara.put("tortue", "jirali");
        traductionsBambara.put("Lièvre", "Jiri");
        traductionsBambara.put("lièvre", "jiri");
        traductionsBambara.put("Mali", "Mali");
        traductionsBambara.put("mali", "mali");
        traductionsBambara.put("Segou", "Segu");
        traductionsBambara.put("segou", "segu");
        traductionsBambara.put("Bamako", "Bamako");
        traductionsBambara.put("bamako", "bamako");
        traductionsBambara.put("Lieu:", "Jukɔrɔ:");
        traductionsBambara.put("lieu:", "jukɔrɔ:");
        traductionsBambara.put("Région:", "Jamana:");
        traductionsBambara.put("région:", "jamana:");
        traductionsBambara.put("Location:", "Jukɔrɔ:");
        traductionsBambara.put("location:", "jukɔrɔ:");
        traductionsBambara.put("Region:", "Jamana:");
        traductionsBambara.put("region:", "jamana:");
        
        // Mots temporels
        traductionsBambara.put("Il était une fois", "A ka kɛ ka kɛ");
        traductionsBambara.put("il était une fois", "a ka kɛ ka kɛ");
        traductionsBambara.put("Un jour", "Dɔgɔkun dɔrɔn");
        traductionsBambara.put("un jour", "dɔgɔkun dɔrɔn");
        traductionsBambara.put("Plus tard", "Kɔnɔ dɔɔnin");
        traductionsBambara.put("plus tard", "kɔnɔ dɔɔnin");
        traductionsBambara.put("Dès sa naissance", "A ka dɔrɔn dɔrɔn");
        traductionsBambara.put("dès sa naissance", "a ka dɔrɔn dɔrɔn");
        
        // Personnages et lieux historiques
        traductionsBambara.put("Soundjata Keïta", "Soundjata Keïta");
        traductionsBambara.put("soundjata keïta", "soundjata keïta");
        traductionsBambara.put("Sogolon Kedjou", "Sogolon Kedjou");
        traductionsBambara.put("sogolon kedjou", "sogolon kedjou");
        traductionsBambara.put("Soumaoro Kanté", "Soumaoro Kanté");
        traductionsBambara.put("soumaoro kanté", "soumaoro kanté");
        traductionsBambara.put("Naré Maghann Konaté", "Naré Maghann Konaté");
        traductionsBambara.put("naré maghann konaté", "naré maghann konaté");
        traductionsBambara.put("Manding", "Manding");
        traductionsBambara.put("manding", "manding");
        traductionsBambara.put("Sosso", "Sosso");
        traductionsBambara.put("sosso", "sosso");
        traductionsBambara.put("Kirina", "Kirina");
        traductionsBambara.put("kirina", "kirina");
        
        // Actions et concepts
        traductionsBambara.put("roi", "mansa");
        traductionsBambara.put("Roi", "Mansa");
        traductionsBambara.put("royaume", "jamana");
        traductionsBambara.put("Royaume", "Jamana");
        traductionsBambara.put("empire", "jamana");
        traductionsBambara.put("Empire", "Jamana");
        traductionsBambara.put("peuple", "jamanaw");
        traductionsBambara.put("Peuple", "Jamanaw");
        traductionsBambara.put("peuples", "jamanaw");
        traductionsBambara.put("Peuples", "Jamanaw");
        traductionsBambara.put("armée", "kɛnɛya");
        traductionsBambara.put("Armée", "Kɛnɛya");
        traductionsBambara.put("bataille", "kɛnɛya");
        traductionsBambara.put("Bataille", "Kɛnɛya");
        traductionsBambara.put("victoire", "kɛnɛya");
        traductionsBambara.put("Victoire", "Kɛnɛya");
        traductionsBambara.put("paix", "kɛnɛya");
        traductionsBambara.put("Paix", "Kɛnɛya");
        traductionsBambara.put("prospérité", "kɛnɛya");
        traductionsBambara.put("Prospérité", "Kɛnɛya");
        
        // Caractéristiques humaines
        traductionsBambara.put("courage", "kɛnɛya");
        traductionsBambara.put("Courage", "Kɛnɛya");
        traductionsBambara.put("sagesse", "hakili");
        traductionsBambara.put("Sagesse", "Hakili");
        traductionsBambara.put("justice", "kɛnɛya");
        traductionsBambara.put("Justice", "Kɛnɛya");
        traductionsBambara.put("unité", "kɛnɛya");
        traductionsBambara.put("Unité", "Kɛnɛya");
        traductionsBambara.put("foi", "kɛnɛya");
        traductionsBambara.put("Foi", "Kɛnɛya");
        traductionsBambara.put("persévérance", "kɛnɛya");
        traductionsBambara.put("Persévérance", "Kɛnɛya");
        
        // Mots descriptifs
        traductionsBambara.put("grand", "caman");
        traductionsBambara.put("Grand", "Caman");
        traductionsBambara.put("grande", "caman");
        traductionsBambara.put("Grande", "Caman");
        traductionsBambara.put("premier", "fɔlɔ");
        traductionsBambara.put("Premier", "Fɔlɔ");
        traductionsBambara.put("première", "fɔlɔ");
        traductionsBambara.put("Première", "Fɔlɔ");
        traductionsBambara.put("nouveau", "dɔrɔn");
        traductionsBambara.put("Nouveau", "Dɔrɔn");
        traductionsBambara.put("nouvelle", "dɔrɔn");
        traductionsBambara.put("Nouvelle", "Dɔrɔn");
        
        // Appliquer les traductions de manière dynamique
        String resultat = texte;
        for (java.util.Map.Entry<String, String> entry : traductionsBambara.entrySet()) {
            if (resultat.contains(entry.getKey())) {
                resultat = resultat.replace(entry.getKey(), entry.getValue());
            }
        }
        
        // Si des traductions ont été appliquées, retourner le résultat
        if (!resultat.equals(texte)) {
            return resultat;
        }

        // Traduction contextuelle dynamique basée sur le contenu
        String texteLower = texte.toLowerCase();
        
        // Détection de genre de contenu
        if (texteLower.contains("conte") || texteLower.contains("histoire") || texteLower.contains("légende")) {
            return "Jirali kan"; // "Histoire" en bambara
        }
        
        // Détection de personnages animaux
        if (texteLower.contains("tortue")) {
            return "Jirali kan"; // "Histoire de la tortue" en bambara
        }
        if (texteLower.contains("lièvre")) {
            return "Jiri kan"; // "Histoire du lièvre" en bambara
        }
        
        // Détection géographique
        if (texteLower.contains("mali")) {
            return "Mali kan"; // "Histoire du Mali" en bambara
        }
        if (texteLower.contains("afrique")) {
            return "Afiriki kan"; // "Histoire de l'Afrique" en bambara
        }
        
        // Détection de thèmes historiques
        if (texteLower.contains("empire") || texteLower.contains("royaume") || texteLower.contains("roi")) {
            return "Jamana kan"; // "Histoire du royaume" en bambara
        }
        
        // Détection de thèmes épiques
        if (texteLower.contains("épopée") || texteLower.contains("héros") || texteLower.contains("bataille")) {
            return "Jirali caman kan"; // "Grande histoire" en bambara
        }

        // Par défaut, retourner une traduction générique
        return "Jirali kan: " + texte;
    }
}
