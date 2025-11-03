package com.heritage.service;

import com.heritage.dto.*;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files; // Ajouté
import java.nio.file.Path; // Ajouté
import java.nio.file.Paths; // Ajouté
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Service pour la création de contenu avec types spécifiques.
 * Supporte les types : CONTE, DEVINETTE, ARTISANAT, PROVERBE
 * La logique de stockage physique des fichiers est déléguée à FileStorageService,
 * sauf pour les fichiers de Conte qui utilisent une méthode locale pour autoriser PDF/TXT.
 */
@Service
public class ContenuCreationService {

    // Ensemble des types de fichiers considérés comme des images/photos (pour déterminer le sous-dossier 'images')
    private static final Set<String> IMAGE_TYPES = Set.of("photo", "proverbe", "devinette", "artisanat");

    private final ContenuRepository contenuRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    // 🚨 NOUVELLE DÉPENDANCE : DÉLÉGATION DE LA GESTION DE FICHIERS
    private final FileStorageService fileStorageService;

    public ContenuCreationService(
            ContenuRepository contenuRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            CategorieRepository categorieRepository,
            MembreFamilleRepository membreFamilleRepository,
            FileStorageService fileStorageService) { // Injection du nouveau service
        this.contenuRepository = contenuRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.fileStorageService = fileStorageService; // Initialisation
    }

    /**
     * Crée un conte.
     */
    @Transactional
    public Contenu creerConte(ConteRequest request, Long auteurId) {
        // Vérifications de base
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // Vérifier les permissions
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        if (!"EDITEUR".equals(membreFamille.getRoleFamille().toString()) && !"ADMIN".equals(membreFamille.getRoleFamille().toString())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setTypeContenu("CONTE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("BROUILLON");
        contenu.setDateCreation(LocalDateTime.now());

        // Gérer les fichiers
        if (request.getFichierConte() != null && !request.getFichierConte().isEmpty()) {
            // 🚨 LOGIQUE RESTAURÉE : Utilisation de la méthode de sauvegarde LOCALE
            // pour contourner le blocage PDF/TXT du FileStorageService.
            String urlFichier = sauvegarderFichierLocal(request.getFichierConte(), "conte");
            contenu.setUrlFichier(urlFichier);
            contenu.setTailleFichier(request.getFichierConte().getSize());
        } else if (request.getTexteConte() != null && !request.getTexteConte().isEmpty()) {
            // Stocker le texte dans la description si pas de fichier
            contenu.setDescription(request.getTexteConte());
        }

        if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
            // Utilisation du type "photo" qui passe toujours par handleFileUpload (et donc FileStorageService)
            String urlPhoto = handleFileUpload(request.getPhotoConte(), "photo");
            contenu.setUrlPhoto(urlPhoto);
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Crée un artisanat.
     */
    @Transactional
    public Contenu creerArtisanat(ArtisanatRequest request, Long auteurId) {
        // Vérifications de base
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // Vérifier les permissions
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        if (!"EDITEUR".equals(membreFamille.getRoleFamille().toString()) && !"ADMIN".equals(membreFamille.getRoleFamille().toString())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setTypeContenu("ARTISANAT");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("BROUILLON");
        contenu.setDateCreation(LocalDateTime.now());

        // Gérer les fichiers
        if (request.getPhotoArtisanat() != null && !request.getPhotoArtisanat().isEmpty()) {
            // Utilisation du type "artisanat" (redirigé vers 'images' par handleFileUpload)
            String urlPhoto = handleFileUpload(request.getPhotoArtisanat(), "artisanat");
            contenu.setUrlPhoto(urlPhoto);
        }

        if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
            // Utilisation du type "video" qui passe toujours par handleFileUpload (et donc FileStorageService)
            String urlVideo = handleFileUpload(request.getVideoArtisanat(), "video");
            contenu.setUrlFichier(urlVideo);
            contenu.setTailleFichier(request.getVideoArtisanat().getSize());
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Crée un proverbe.
     */
    @Transactional
    public Contenu creerProverbe(ProverbeRequest request, Long auteurId) {
        // Vérifications de base
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // Vérifier les permissions
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        if (!"EDITEUR".equals(membreFamille.getRoleFamille().toString()) && !"ADMIN".equals(membreFamille.getRoleFamille().toString())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getSignificationProverbe());
        contenu.setTypeContenu("PROVERBE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("BROUILLON");
        contenu.setDateCreation(LocalDateTime.now());

        // Stocker les informations spécifiques au proverbe
        String descriptionComplete = String.format("Origine: %s\nProverbe: %s\nSignification: %s",
                request.getOrigineProverbe(), request.getTexteProverbe(), request.getSignificationProverbe());
        contenu.setDescription(descriptionComplete);

        if (request.getPhotoProverbe() != null && !request.getPhotoProverbe().isEmpty()) {
            // Utilisation du type "proverbe" qui passe toujours par handleFileUpload
            String urlPhoto = handleFileUpload(request.getPhotoProverbe(), "proverbe");
            contenu.setUrlPhoto(urlPhoto);
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Crée une devinette.
     */
    @Transactional
    public Contenu creerDevinette(DevinetteRequest request, Long auteurId) {
        // Vérifications de base
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // Vérifier les permissions
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        if (!"EDITEUR".equals(membreFamille.getRoleFamille().toString()) && !"ADMIN".equals(membreFamille.getRoleFamille().toString())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getTexteDevinette());
        contenu.setTypeContenu("DEVINETTE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("BROUILLON");
        contenu.setDateCreation(LocalDateTime.now());

        // Stocker la réponse dans un champ séparé (si disponible dans l'entité)
        // Pour l'instant, on l'ajoute à la description
        String descriptionComplete = String.format("Devinette: %s\nRéponse: %s",
                request.getTexteDevinette(), request.getReponseDevinette());
        contenu.setDescription(descriptionComplete);

        if (request.getPhotoDevinette() != null && !request.getPhotoDevinette().isEmpty()) {
            // Utilisation du type "devinette" qui passe toujours par handleFileUpload
            String urlPhoto = handleFileUpload(request.getPhotoDevinette(), "devinette");
            contenu.setUrlPhoto(urlPhoto);
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Crée un contenu générique (méthode de compatibilité).
     */
    @Transactional
    public Contenu creerContenu(CreationContenuRequest request, Long auteurId) {
        // Vérifications de base
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // Vérifier les permissions
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        if (!"EDITEUR".equals(membreFamille.getRoleFamille().toString()) && !"ADMIN".equals(membreFamille.getRoleFamille().toString())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setTypeContenu(request.getTypeContenu());
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("BROUILLON");
        contenu.setDateCreation(LocalDateTime.now());

        // Gérer les fichiers selon le type
        switch (request.getTypeContenu()) {
            case "CONTE":
                if (request.getFichierConte() != null && !request.getFichierConte().isEmpty()) {
                    // 🚨 LOGIQUE RESTAURÉE : Utilisation de la méthode de sauvegarde LOCALE
                    String urlFichier = sauvegarderFichierLocal(request.getFichierConte(), "conte");
                    contenu.setUrlFichier(urlFichier);
                    contenu.setTailleFichier(request.getFichierConte().getSize());
                }
                if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
                    // Utilisation du type "photo" qui passe par handleFileUpload
                    String urlPhoto = handleFileUpload(request.getPhotoConte(), "photo");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
            case "ARTISANAT":
                if (request.getPhotosArtisanat() != null && !request.getPhotosArtisanat().isEmpty()) {
                    // Utilisation du type "artisanat" qui passe par handleFileUpload
                    String urlPhoto = handleFileUpload(request.getPhotosArtisanat().get(0), "artisanat");
                    contenu.setUrlPhoto(urlPhoto);
                }
                if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
                    // Utilisation du type "video" qui passe par handleFileUpload
                    String urlVideo = handleFileUpload(request.getVideoArtisanat(), "video");
                    contenu.setUrlFichier(urlVideo);
                    contenu.setTailleFichier(request.getVideoArtisanat().getSize());
                }
                break;
            case "PROVERBE":
                if (request.getPhotoProverbe() != null && !request.getPhotoProverbe().isEmpty()) {
                    // Utilisation du type "proverbe" qui passe par handleFileUpload
                    String urlPhoto = handleFileUpload(request.getPhotoProverbe(), "proverbe");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
            case "DEVINETTE":
                if (request.getPhotoDevinette() != null && !request.getPhotoDevinette().isEmpty()) {
                    // Utilisation du type "devinette" qui passe par handleFileUpload
                    String urlPhoto = handleFileUpload(request.getPhotoDevinette(), "devinette");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Sauvegarde un fichier uploadé localement (méthode de l'ancienne logique).
     * Utilisée spécifiquement pour les fichiers de Conte (PDF, TXT, etc.) qui ne doivent pas être validés.
     */
    private String sauvegarderFichierLocal(MultipartFile fichier, String type) {
        try {
            String nomFichier = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
            // Utilise java.nio.file pour le stockage direct dans le dossier "uploads"
            Path cheminFichier = Paths.get("uploads/" + type + "/" + nomFichier);
            Files.createDirectories(cheminFichier.getParent());
            Files.copy(fichier.getInputStream(), cheminFichier);
            return "/uploads/" + type + "/" + nomFichier;
        } catch (IOException e) {
            throw new BadRequestException("Erreur lors de la sauvegarde locale du fichier: " + e.getMessage());
        }
    }

    /**
     * Gère la logique de détermination du sous-dossier et appelle le service de stockage (avec validation).
     * Utilisée pour tous les fichiers qui nécessitent une validation de type de fichier (images, vidéos).
     */
    private String handleFileUpload(MultipartFile fichier, String type) {
        // 1. Déterminer le sous-dossier de destination (Logique métier)
        String sousDossier;
        if (IMAGE_TYPES.contains(type)) {
            sousDossier = "images"; // Toutes les photos/images vont dans le dossier 'images'
        } else {
            // Les fichiers de contenu (vidéos) utilisent le type spécifié
            sousDossier = type;
        }

        // 2. Déléger l'opération de stockage au service externe (Contrat technique)
        try {
            // L'appel à storeFile() dans FileStorageService va bloquer les PDF/TXT.
            return fileStorageService.storeFile(fichier, sousDossier);
        } catch (IOException e) {
            // 3. Gérer l'exception de stockage selon les règles du service métier
            throw new BadRequestException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }
}
