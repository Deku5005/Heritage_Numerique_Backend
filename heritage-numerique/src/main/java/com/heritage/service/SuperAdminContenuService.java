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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service pour gérer les contenus publics créés par le super-admin.
 * Ces contenus sont accessibles à tous les utilisateurs et appartiennent à la famille virtuelle "PUBLIC".
 */
@Service
public class SuperAdminContenuService {


    private final ContenuRepository contenuRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;


    public SuperAdminContenuService(
            ContenuRepository contenuRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            CategorieRepository categorieRepository) {
        this.contenuRepository = contenuRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
    }


    /**
     * Vérifie que l'utilisateur est super-admin.
     */
    private void verifierSuperAdmin(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));


        if (!"ROLE_ADMIN".equals(utilisateur.getRole())) {
            throw new UnauthorizedException("Accès réservé aux super-administrateurs");
        }
    }


    /**
     * Récupère un contenu existant et vérifie son type et son statut (PUBLIC).
     */
    private Contenu getAndVerifyContenuPublic(Long contenuId, String expectedType) {
        Contenu contenu = contenuRepository.findById(contenuId)
                .orElseThrow(() -> new NotFoundException(expectedType + " non trouvé avec l'ID " + contenuId));


        if (!expectedType.equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new BadRequestException("Le contenu avec l'ID " + contenuId + " n'est pas un " + expectedType);
        }


        // Vérification que le contenu appartient à la famille publique
        Famille famillePublic = getFamillePublic();
        if (!famillePublic.getId().equals(contenu.getFamille().getId())) {
            throw new UnauthorizedException("Ce contenu n'est pas un contenu public modifiable par le Super-Admin.");
        }


        return contenu;
    }


    /**
     * Récupère ou crée la famille virtuelle PUBLIC.
     */
    private Famille getFamillePublic() {
        return familleRepository.findByNom("PUBLIC_HERITAGE")
                .orElseGet(() -> {
                    // Créer la famille virtuelle si elle n'existe pas
                    Utilisateur admin = utilisateurRepository.findByEmail("oumardolo27@gmail.com")
                            .orElseThrow(() -> new NotFoundException("SuperAdmin non trouvé"));


                    Famille famillePublic = new Famille();
                    famillePublic.setNom("PUBLIC_HERITAGE");
                    famillePublic.setDescription("Contenus publics du patrimoine culturel malien - accessibles à tous");
                    famillePublic.setEthnie("MALIEN");
                    famillePublic.setRegion("TOUT_MALI");
                    famillePublic.setCreateur(admin);
                    famillePublic.setDateCreation(LocalDateTime.now());
                    famillePublic.setDateModification(LocalDateTime.now());


                    return familleRepository.save(famillePublic);
                });
    }


    /**
     * Crée un conte public.
     */
    @Transactional
    public ContenuDTO creerContePublic(ConteJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Utilisateur auteur = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));


        Categorie categorie = categorieRepository.findByNom("Contes")
                .orElseThrow(() -> new NotFoundException("Catégorie 'Contes' non trouvée"));


        Famille famillePublic = getFamillePublic();


        Contenu contenu = new Contenu();
        contenu.setFamille(famillePublic);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription() != null ? request.getDescription() : request.getTexteConte());
        contenu.setTypeContenu("CONTE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("PUBLIE"); // Directement publié
        contenu.setDateCreation(LocalDateTime.now());


        // Gérer les fichiers uploadés
        if (request.getFichierConte() != null && !request.getFichierConte().isEmpty()) {
            String urlFichier = sauvegarderFichier(request.getFichierConte(), "conte");
            contenu.setUrlFichier(urlFichier);
            contenu.setTailleFichier(request.getFichierConte().getSize());
        }


        if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoConte(), "photo");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }


    /**
     * Modifie un conte public existant.
     */
    @Transactional
    public ContenuDTO modifierContePublic(Long id, ConteJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Contenu contenu = getAndVerifyContenuPublic(id, "CONTE");


        contenu.setTitre(request.getTitre());
        // La description peut être mise à jour indépendamment ou via le texte du conte
        contenu.setDescription(request.getDescription() != null ? request.getDescription() : contenu.getDescription());
        if (request.getTexteConte() != null) {
            // Dans ce cas, nous assumons que le texte du conte est mis dans la description si pas de fichier
            contenu.setDescription(request.getTexteConte());
        }


        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setDateModification(LocalDateTime.now());


        // Gérer la mise à jour des fichiers
        if (request.getFichierConte() != null && !request.getFichierConte().isEmpty()) {
            String urlFichier = sauvegarderFichier(request.getFichierConte(), "conte");
            contenu.setUrlFichier(urlFichier);
            contenu.setTailleFichier(request.getFichierConte().getSize());
        }


        if (request.getPhotoConte() != null && !request.getPhotoConte().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoConte(), "photo");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }


    /**
     * Crée un proverbe public.
     */
    @Transactional
    public ContenuDTO creerProverbePublic(ProverbeJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Utilisateur auteur = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));


        Categorie categorie = categorieRepository.findByNom("Proverbes")
                .orElseThrow(() -> new NotFoundException("Catégorie 'Proverbes' non trouvée"));


        Famille famillePublic = getFamillePublic();


        // 🧩 Création du texte descriptif combiné pour affichage
        String description = String.format(
                "Origine: %s\nProverbe: %s\nSignification: %s",
                request.getOrigineProverbe(),
                request.getTexteProverbe(),
                request.getSignificationProverbe()
        );


        Contenu contenu = new Contenu();
        contenu.setFamille(famillePublic);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(description);
        contenu.setTypeContenu("PROVERBE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("PUBLIE");
        contenu.setDateCreation(LocalDateTime.now());


        // ✅ Enregistrer aussi dans les champs spécifiques
        contenu.setTexteProverbe(request.getTexteProverbe());
        contenu.setSignificationProverbe(request.getSignificationProverbe());
        contenu.setOrigineProverbe(request.getOrigineProverbe());


        // 🖼️ Upload de la photo
        if (request.getPhotoProverbe() != null && !request.getPhotoProverbe().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoProverbe(), "proverbes");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }




    /**
     * Modifie un proverbe public existant.
     */
    @Transactional
    public ContenuDTO modifierProverbePublic(Long id, ProverbeJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Contenu contenu = getAndVerifyContenuPublic(id, "PROVERBE");


        String description = String.format(
                "Origine: %s\nProverbe: %s\nSignification: %s",
                request.getOrigineProverbe(),
                request.getTexteProverbe(),
                request.getSignificationProverbe()
        );


        contenu.setTitre(request.getTitre());
        contenu.setDescription(description);
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setDateModification(LocalDateTime.now());


        // ✅ Mettre à jour les champs dédiés
        contenu.setTexteProverbe(request.getTexteProverbe());
        contenu.setSignificationProverbe(request.getSignificationProverbe());
        contenu.setOrigineProverbe(request.getOrigineProverbe());


        // 🖼️ Mise à jour de la photo
        if (request.getPhotoProverbe() != null && !request.getPhotoProverbe().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoProverbe(), "proverbes");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }




    /**
     * Crée une devinette publique.
     */
    @Transactional
    public ContenuDTO creerDevinettePublic(DevinetteJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Utilisateur auteur = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));


        Categorie categorie = categorieRepository.findByNom("Devinettes")
                .orElseThrow(() -> new NotFoundException("Catégorie 'Devinettes' non trouvée"));


        Famille famillePublic = getFamillePublic();


        String description = String.format("Devinette: %s\nRéponse: %s",
                request.getTexteDevinette(), request.getReponseDevinette());


        Contenu contenu = new Contenu();
        contenu.setFamille(famillePublic);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(description);
        contenu.setTypeContenu("DEVINETTE");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("PUBLIE");
        contenu.setDateCreation(LocalDateTime.now());


        if (request.getPhotoDevinette() != null && !request.getPhotoDevinette().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoDevinette(), "devinette");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }


    /**
     * Modifie une devinette publique existante.
     */
    @Transactional
    public ContenuDTO modifierDevinettePublic(Long id, DevinetteJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Contenu contenu = getAndVerifyContenuPublic(id, "DEVINETTE");


        String description = String.format("Devinette: %s\nRéponse: %s",
                request.getTexteDevinette(), request.getReponseDevinette());


        contenu.setTitre(request.getTitre());
        contenu.setDescription(description); // Mise à jour de la description combinée
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setDateModification(LocalDateTime.now());


        // Gérer la mise à jour de la photo
        if (request.getPhotoDevinette() != null && !request.getPhotoDevinette().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoDevinette(), "devinette");
            contenu.setUrlPhoto(urlPhoto);
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }




    /**
     * Crée un artisanat public.
     */
    @Transactional
    public ContenuDTO creerArtisanatPublic(ArtisanatJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Utilisateur auteur = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));


        Categorie categorie = categorieRepository.findByNom("Artisanats")
                .orElseThrow(() -> new NotFoundException("Catégorie 'Artisanats' non trouvée"));


        Famille famillePublic = getFamillePublic();


        Contenu contenu = new Contenu();
        contenu.setFamille(famillePublic);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setTypeContenu("ARTISANAT");
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setStatut("PUBLIE");
        contenu.setDateCreation(LocalDateTime.now());


        if (request.getPhotoArtisanat() != null && !request.getPhotoArtisanat().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoArtisanat(), "artisanat");
            contenu.setUrlPhoto(urlPhoto);
        }


        if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
            String urlVideo = sauvegarderFichier(request.getVideoArtisanat(), "video");
            contenu.setUrlFichier(urlVideo);
            contenu.setTailleFichier(request.getVideoArtisanat().getSize());
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }


    /**
     * Modifie un artisanat public existant.
     */
    @Transactional
    public ContenuDTO modifierArtisanatPublic(Long id, ArtisanatJsonRequest request, Long adminId) {
        verifierSuperAdmin(adminId);


        Contenu contenu = getAndVerifyContenuPublic(id, "ARTISANAT");


        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        contenu.setDateModification(LocalDateTime.now());


        // Gérer la mise à jour des fichiers
        if (request.getPhotoArtisanat() != null && !request.getPhotoArtisanat().isEmpty()) {
            String urlPhoto = sauvegarderFichier(request.getPhotoArtisanat(), "artisanat");
            contenu.setUrlPhoto(urlPhoto);
        }


        if (request.getVideoArtisanat() != null && !request.getVideoArtisanat().isEmpty()) {
            String urlVideo = sauvegarderFichier(request.getVideoArtisanat(), "video");
            contenu.setUrlFichier(urlVideo);
            contenu.setTailleFichier(request.getVideoArtisanat().getSize());
        }


        contenu = contenuRepository.save(contenu);
        return convertToDTO(contenu);
    }


    /**
     * Récupère tous les contenus publics.
     */
    @Transactional(readOnly = true)
    public List<ContenuDTO> getAllContenusPublics() {
        Famille famillePublic = getFamillePublic();


        List<Contenu> contenus = contenuRepository.findByFamilleIdAndStatut(famillePublic.getId(), "PUBLIE");


        return contenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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


    /**
     * Convertit un contenu en DTO.
     */
    private ContenuDTO convertToDTO(Contenu contenu) {
        ContenuDTO dto = new ContenuDTO();
        dto.setId(contenu.getId());
        dto.setIdFamille(contenu.getFamille() != null ? contenu.getFamille().getId() : null);
        dto.setIdAuteur(contenu.getAuteur().getId());
        dto.setNomAuteur(contenu.getAuteur().getNom() + " " + contenu.getAuteur().getPrenom());
        dto.setIdCategorie(contenu.getCategorie().getId());
        dto.setNomCategorie(contenu.getCategorie().getNom());
        dto.setTitre(contenu.getTitre());
        dto.setDescription(contenu.getDescription());
        dto.setTypeContenu(contenu.getTypeContenu());
        dto.setUrlFichier(contenu.getUrlFichier());
        dto.setUrlPhoto(contenu.getUrlPhoto());
        dto.setTailleFichier(contenu.getTailleFichier());
        dto.setDuree(contenu.getDuree());
        dto.setDateEvenement(contenu.getDateEvenement());
        dto.setLieu(contenu.getLieu());
        dto.setRegion(contenu.getRegion());
        dto.setStatut(contenu.getStatut());
        dto.setDateCreation(contenu.getDateCreation());
        dto.setDateModification(contenu.getDateModification());


        // ✅ Ajout des champs spécifiques au proverbe
        dto.setTexteProverbe(contenu.getTexteProverbe());
        dto.setSignificationProverbe(contenu.getSignificationProverbe());
        dto.setOrigineProverbe(contenu.getOrigineProverbe());


        return dto;
    }


}


