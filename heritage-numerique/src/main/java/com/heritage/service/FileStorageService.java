package com.heritage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service pour gérer le stockage physique des fichiers uploadés.
 * Assure que les fichiers sont enregistrés dans le répertoire défini par 'file.upload-dir'.
 */
@Service
public class FileStorageService {

    // Injecte le chemin de base défini dans application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Sauvegarde un fichier et retourne son chemin d'accès relatif (URL publique).
     * * @param file Le fichier à sauvegarder.
     * @param subDirectory Le sous-répertoire (ex: "photo", "pdf") pour organiser les fichiers.
     * @return Le chemin relatif public à stocker en base de données (ex: /uploads/photo/image_uuid.png)
     * @throws IOException Si la sauvegarde échoue.
     */
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {

        // 1. Déterminer le répertoire cible physique absolu
        // Concatène le chemin de base avec le sous-répertoire.
        Path targetLocation = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();

        // 2. Créer les répertoires s'ils n'existent pas (ex: le dossier 'photo' dans 'uploads')
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
            System.out.println("Création du répertoire d'upload : " + targetLocation);
        }

        // 3. Générer un nom de fichier unique et sécurisé (UUID)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // 4. Résoudre le chemin complet du nouveau fichier
        Path filePath = targetLocation.resolve(newFilename);

        // 5. Copier les octets du fichier dans le nouveau chemin (SAUVEGARDE PHYSIQUE)
        // Utilise l'option REPLACE_EXISTING si un fichier avec le même nom existait,
        // mais avec l'UUID, ce cas est très improbable.
        Files.copy(file.getInputStream(), filePath);

        // 6. Retourner l'URL relative publique pour la base de données
        // Cette URL doit correspondre au pattern géré par WebConfig : /uploads/**
        return "/uploads/" + subDirectory + "/" + newFilename;
    }
}
