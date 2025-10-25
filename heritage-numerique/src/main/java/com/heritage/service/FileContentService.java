package com.heritage.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service pour lire le contenu réel des fichiers uploadés.
 * Utilise Apache Tika pour extraire le texte de tous types de fichiers.
 */
@Service
public class FileContentService {

    private final Tika tika = new Tika();

    /**
     * Lit le contenu d'un fichier et retourne le texte extrait.
     * 
     * @param urlFichier URL du fichier (ex: /uploads/conte/fichier.pdf)
     * @return Le contenu textuel du fichier
     */
    public String lireContenuFichier(String urlFichier) {
        if (urlFichier == null || urlFichier.trim().isEmpty()) {
            return "Aucun fichier spécifié";
        }

        try {
            // Construire le chemin complet du fichier
            String cheminComplet = System.getProperty("user.dir") + urlFichier;
            File fichier = new File(cheminComplet);
            
            if (!fichier.exists()) {
                System.err.println("❌ Fichier non trouvé: " + cheminComplet);
                return "Fichier non trouvé: " + urlFichier;
            }

            // Détecter le type de fichier
            String typeMime = tika.detect(fichier);
            System.out.println("📄 Type de fichier détecté: " + typeMime);

            // Extraire le contenu textuel
            String contenu = tika.parseToString(fichier);
            
            if (contenu == null || contenu.trim().isEmpty()) {
                return "Aucun contenu textuel trouvé dans le fichier";
            }

            // Nettoyer le contenu (supprimer les espaces multiples, etc.)
            contenu = contenu.trim()
                    .replaceAll("\\s+", " ")  // Remplacer les espaces multiples par un seul
                    .replaceAll("\\n\\s*\\n", "\n\n");  // Nettoyer les sauts de ligne multiples

            System.out.println("✅ Contenu extrait avec succès (" + contenu.length() + " caractères)");
            return contenu;

        } catch (IOException e) {
            System.err.println("❌ Erreur de lecture du fichier: " + e.getMessage());
            return "Erreur lors de la lecture du fichier: " + e.getMessage();
        } catch (TikaException e) {
            System.err.println("❌ Erreur d'extraction Tika: " + e.getMessage());
            return "Erreur lors de l'extraction du contenu: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
            return "Erreur inattendue lors de la lecture du fichier: " + e.getMessage();
        }
    }

    /**
     * Lit le contenu d'un fichier texte simple (pour les fichiers .txt).
     * 
     * @param urlFichier URL du fichier
     * @return Le contenu du fichier texte
     */
    public String lireFichierTexte(String urlFichier) {
        try {
            String cheminComplet = System.getProperty("user.dir") + urlFichier;
            return Files.readString(Paths.get(cheminComplet));
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la lecture du fichier texte: " + e.getMessage());
            return "Erreur lors de la lecture du fichier texte: " + e.getMessage();
        }
    }

    /**
     * Vérifie si un fichier existe.
     * 
     * @param urlFichier URL du fichier
     * @return true si le fichier existe, false sinon
     */
    public boolean fichierExiste(String urlFichier) {
        if (urlFichier == null || urlFichier.trim().isEmpty()) {
            return false;
        }
        
        String cheminComplet = System.getProperty("user.dir") + urlFichier;
        File fichier = new File(cheminComplet);
        return fichier.exists();
    }

    /**
     * Obtient la taille d'un fichier en bytes.
     * 
     * @param urlFichier URL du fichier
     * @return La taille du fichier en bytes, ou -1 si le fichier n'existe pas
     */
    public long getTailleFichier(String urlFichier) {
        if (urlFichier == null || urlFichier.trim().isEmpty()) {
            return -1;
        }
        
        String cheminComplet = System.getProperty("user.dir") + urlFichier;
        File fichier = new File(cheminComplet);
        
        if (!fichier.exists()) {
            return -1;
        }
        
        return fichier.length();
    }
}
