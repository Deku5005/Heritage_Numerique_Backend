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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service pour la création de contenu avec types spécifiques.
 * Supporte les types : CONTE, DEVINETTE, ARTISANAT, PROVERBE
 */
@Service
public class ContenuCreationService {

    private final ContenuRepository contenuRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public ContenuCreationService(
            ContenuRepository contenuRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            CategorieRepository categorieRepository,
            MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
        this.membreFamilleRepository = membreFamilleRepository;
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

        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
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
            String urlFichier = sauvegarderFichier(request.getFichierConte(), "conte");
            contenu.setUrlFichier(urlFichier);
            contenu.setTailleFichier(request.getFichierConte().getSize());
        } else if (request.getTexteConte() != null && !request.getTexteConte().isEmpty()) {
            // Stocker le texte dans la description si pas de fichier
            contenu.setDescription(request.getTexteConte());
        }

        if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoConte(), "photo");
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

        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
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
            String urlPhoto = sauvegarderFichier(request.getPhotoArtisanat(), "artisanat");
            contenu.setUrlPhoto(urlPhoto);
        }

        if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
            String urlVideo = sauvegarderFichier(request.getVideoArtisanat(), "video");
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

        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
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
            String urlPhoto = sauvegarderFichier(request.getPhotoProverbe(), "proverbe");
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

        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
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
            String urlPhoto = sauvegarderFichier(request.getPhotoDevinette(), "devinette");
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

        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
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
                    String urlFichier = sauvegarderFichier(request.getFichierConte(), "conte");
                    contenu.setUrlFichier(urlFichier);
                    contenu.setTailleFichier(request.getFichierConte().getSize());
                }
                if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
                    String urlPhoto = sauvegarderFichier(request.getPhotoConte(), "photo");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
            case "ARTISANAT":
                if (request.getPhotosArtisanat() != null && !request.getPhotosArtisanat().isEmpty()) {
                    String urlPhoto = sauvegarderFichier(request.getPhotosArtisanat().get(0), "artisanat");
                    contenu.setUrlPhoto(urlPhoto);
                }
                if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
                    String urlVideo = sauvegarderFichier(request.getVideoArtisanat(), "video");
                    contenu.setUrlFichier(urlVideo);
                    contenu.setTailleFichier(request.getVideoArtisanat().getSize());
                }
                break;
            case "PROVERBE":
                if (request.getPhotoProverbe() != null && !request.getPhotoProverbe().isEmpty()) {
                    String urlPhoto = sauvegarderFichier(request.getPhotoProverbe(), "proverbe");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
            case "DEVINETTE":
                if (request.getPhotoDevinette() != null && !request.getPhotoDevinette().isEmpty()) {
                    String urlPhoto = sauvegarderFichier(request.getPhotoDevinette(), "devinette");
                    contenu.setUrlPhoto(urlPhoto);
                }
                break;
        }

        return contenuRepository.save(contenu);
    }

    /**
     * Sauvegarde un fichier uploadé et retourne l'URL.
     */
    private String sauvegarderFichier(MultipartFile fichier, String type) {
        try {
            String nomFichier = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
            Path cheminFichier = Paths.get("uploads/" + type + "/" + nomFichier);
            Files.createDirectories(cheminFichier.getParent());
            Files.copy(fichier.getInputStream(), cheminFichier);
            return "/uploads/" + type + "/" + nomFichier;
        } catch (IOException e) {
            throw new BadRequestException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }
}