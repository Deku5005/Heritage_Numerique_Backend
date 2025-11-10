package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.service.ContenuService;
import com.heritage.service.SuperAdminService;
import com.heritage.service.SuperAdminContenuService;
import com.heritage.service.SuperAdminQuizService;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.heritage.util.AuthenticationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour les fonctionnalités du super-admin.
 * 
 * Endpoints :
 * - GET /api/superadmin/statistiques : statistiques globales
 * - POST /api/superadmin/categories : créer une catégorie
 * - DELETE /api/superadmin/categories/{id} : supprimer une catégorie
 * - GET /api/superadmin/categories : lister les catégories
 * 
 * Sécurité : Tous les endpoints nécessitent le rôle ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/superadmin")
@CrossOrigin(origins = "*")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final ContenuService contenuService;
    private final SuperAdminContenuService superAdminContenuService;
    private final SuperAdminQuizService superAdminQuizService;

    public SuperAdminController(
            SuperAdminService superAdminService,
            ContenuService contenuService,
            SuperAdminContenuService superAdminContenuService,
            SuperAdminQuizService superAdminQuizService) {
        this.superAdminService = superAdminService;
        this.contenuService = contenuService;
        this.superAdminContenuService = superAdminContenuService;
        this.superAdminQuizService = superAdminQuizService;
    }

    /**
     * Récupère les statistiques globales de la plateforme.
     * Accessible uniquement par le super-admin (ROLE_ADMIN).
     * 
     * Statistiques :
     * - Nombre d'utilisateurs
     * - Nombre de familles
     * - Nombre de contenus (total et publics)
     * - Nombre de quiz
     * - Nombre de catégories
     * - Invitations en attente
     * - Notifications envoyées
     * 
     * @param authentication Authentification
     * @return Statistiques globales
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatistiquesDTO> getStatistiques(Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        StatistiquesDTO stats = superAdminService.getStatistiquesGlobales(adminId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Crée une nouvelle catégorie de contenu.
     * Seul le super-admin peut créer des catégories.
     * 
     * @param nom Nom de la catégorie
     * @param description Description
     * @param icone Icône
     * @param authentication Authentification
     * @return Catégorie créée
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorieDTO> createCategorie(
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icone,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        CategorieDTO categorie = superAdminService.createCategorie(nom, description, icone, adminId);
        return new ResponseEntity<>(categorie, HttpStatus.CREATED);
    }

    /**
     * Supprime une catégorie.
     * Attention : échouera si des contenus utilisent cette catégorie.
     * 
     * @param id ID de la catégorie
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategorie(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        superAdminService.deleteCategorie(id, adminId);
        return ResponseEntity.ok("Catégorie supprimée avec succès");
    }

    /**
     * Récupère toutes les catégories.
     * Accessible par tous les utilisateurs authentifiés.
     * 
     * @return Liste des catégories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        List<CategorieDTO> categories = superAdminService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupère toutes les demandes de publication en attente.
     * Seul le super-admin peut voir toutes les demandes.
     * 
     * @param authentication Authentification
     * @return Liste des demandes en attente
     */
    @GetMapping("/demandes-publication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemandePublicationDTO>> getDemandesPublicationEnAttente(
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        List<DemandePublicationDTO> demandes = superAdminService.getDemandesPublicationEnAttente(adminId);
        return ResponseEntity.ok(demandes);
    }

    /**
     * Valide une demande de publication (endpoint dans superadmin).
     * 
     * @param demandeId ID de la demande
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes-publication/{demandeId}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> validerPublication(
            @PathVariable Long demandeId,
            Authentication authentication) {
        
        Long valideurId = getUserIdFromAuth(authentication);
        contenuService.validerPublication(demandeId, valideurId);
        return ResponseEntity.ok("Contenu publié avec succès");
    }

    /**
     * Rejette une demande de publication.
     * 
     * @param demandeId ID de la demande
     * @param commentaire Raison du rejet
     * @param authentication Authentification
     * @return Message de confirmation
     */
    @PostMapping("/demandes-publication/{demandeId}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejeterPublication(
            @PathVariable Long demandeId,
            @RequestParam String commentaire,
            Authentication authentication) {
        
        Long valideurId = getUserIdFromAuth(authentication);
        contenuService.rejeterPublication(demandeId, valideurId, commentaire);
        return ResponseEntity.ok("Demande rejetée");
    }

    /**
     * Crée un conte public (accessible à tous).
     * Seul le super-admin peut créer des contenus publics.
     * 
     * @param titre Titre du conte
     * @param description Description du conte
     * @param texteConte Texte du conte (optionnel si fichier présent)
     * @param fichierConte Fichier PDF ou document du conte
     * @param photoConte Photo illustrant le conte
     * @param lieu Lieu d'origine
     * @param region Région d'origine
     * @param authentication Authentification
     * @return Conte créé
     */
    @PostMapping(value = "/contenus-publics/conte", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContenuDTO> creerContePublic(
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String texteConte,
            @RequestParam(required = false) MultipartFile fichierConte,
            @RequestParam(required = false) MultipartFile photoConte,
            @RequestParam(required = false) String lieu,
            @RequestParam(required = false) String region,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        
        ConteJsonRequest request = new ConteJsonRequest();
        request.setTitre(titre);
        request.setDescription(description);
        request.setTexteConte(texteConte);
        request.setFichierConte(fichierConte);
        request.setPhotoConte(photoConte);
        request.setLieu(lieu);
        request.setRegion(region);
        
        ContenuDTO conte = superAdminContenuService.creerContePublic(request, adminId);
        return new ResponseEntity<>(conte, HttpStatus.CREATED);
    }

    /**
     * Crée un proverbe public (accessible à tous).
     * 
     * @param titre Titre du proverbe
     * @param origineProverbe Origine du proverbe
     * @param texteProverbe Le proverbe lui-même
     * @param significationProverbe Signification du proverbe
     * @param photoProverbe Photo illustrant le proverbe
     * @param lieu Lieu d'origine
     * @param region Région d'origine
     * @param authentication Authentification
     * @return Proverbe créé
     */
    @PostMapping(value = "/contenus-publics/proverbe", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContenuDTO> creerProverbePublic(
            @RequestParam String titre,
            @RequestParam String origineProverbe,
            @RequestParam String texteProverbe,
            @RequestParam String significationProverbe,
            @RequestParam(required = false) MultipartFile photoProverbe,
            @RequestParam(required = false) String lieu,
            @RequestParam(required = false) String region,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        
        ProverbeJsonRequest request = new ProverbeJsonRequest();
        request.setTitre(titre);
        request.setOrigineProverbe(origineProverbe);
        request.setTexteProverbe(texteProverbe);
        request.setSignificationProverbe(significationProverbe);
        request.setPhotoProverbe(photoProverbe);
        request.setLieu(lieu);
        request.setRegion(region);
        
        ContenuDTO proverbe = superAdminContenuService.creerProverbePublic(request, adminId);
        return new ResponseEntity<>(proverbe, HttpStatus.CREATED);
    }

    /**
     * Crée une devinette publique (accessible à tous).
     * 
     * @param titre Titre de la devinette
     * @param texteDevinette Le texte de la devinette
     * @param reponseDevinette La réponse de la devinette
     * @param photoDevinette Photo illustrant la devinette
     * @param lieu Lieu d'origine
     * @param region Région d'origine
     * @param authentication Authentification
     * @return Devinette créée
     */
    @PostMapping(value = "/contenus-publics/devinette", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContenuDTO> creerDevinettePublic(
            @RequestParam String titre,
            @RequestParam String texteDevinette,
            @RequestParam String reponseDevinette,
            @RequestParam(required = false) MultipartFile photoDevinette,
            @RequestParam(required = false) String lieu,
            @RequestParam(required = false) String region,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        
        DevinetteJsonRequest request = new DevinetteJsonRequest();
        request.setTitre(titre);
        request.setTexteDevinette(texteDevinette);
        request.setReponseDevinette(reponseDevinette);
        request.setPhotoDevinette(photoDevinette);
        request.setLieu(lieu);
        request.setRegion(region);
        
        ContenuDTO devinette = superAdminContenuService.creerDevinettePublic(request, adminId);
        return new ResponseEntity<>(devinette, HttpStatus.CREATED);
    }

    /**
     * Crée un artisanat public (accessible à tous).
     * 
     * @param titre Titre de l'artisanat
     * @param description Description de l'artisanat
     * @param photoArtisanat Photo de l'artisanat
     * @param videoArtisanat Vidéo de l'artisanat
     * @param lieu Lieu d'origine
     * @param region Région d'origine
     * @param authentication Authentification
     * @return Artisanat créé
     */
    @PostMapping(value = "/contenus-publics/artisanat", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContenuDTO> creerArtisanatPublic(
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile photoArtisanat,
            @RequestParam(required = false) MultipartFile videoArtisanat,
            @RequestParam(required = false) String lieu,
            @RequestParam(required = false) String region,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        
        ArtisanatJsonRequest request = new ArtisanatJsonRequest();
        request.setTitre(titre);
        request.setDescription(description);
        request.setPhotoArtisanat(photoArtisanat);
        request.setVideoArtisanat(videoArtisanat);
        request.setLieu(lieu);
        request.setRegion(region);
        
        ContenuDTO artisanat = superAdminContenuService.creerArtisanatPublic(request, adminId);
        return new ResponseEntity<>(artisanat, HttpStatus.CREATED);
    }

    /**
     * Récupère tous les contenus publics.
     * Accessible à tous les utilisateurs authentifiés.
     * 
     * @return Liste des contenus publics
     */
    @GetMapping("/contenus-publics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<ContenuDTO>> getAllContenusPublics() {
        List<ContenuDTO> contenus = superAdminContenuService.getAllContenusPublics();
        return ResponseEntity.ok(contenus);
    }

    /**
     * Récupère tous les contes publics avec toutes leurs informations.
     * Accessible à tout le monde sans authentification.
     * 
     * @return Liste des contes publics
     */
    @GetMapping("/contenus-publics/contes")
    public ResponseEntity<List<ContenuDTO>> getAllContes() {
        List<ContenuDTO> contes = superAdminContenuService.getAllContes();
        return ResponseEntity.ok(contes);
    }

    /**
     * Récupère tous les artisanats publics avec toutes leurs informations.
     * Accessible à tout le monde sans authentification.
     * 
     * @return Liste des artisanats publics
     */
    @GetMapping("/contenus-publics/artisanats")
    public ResponseEntity<List<ContenuDTO>> getAllArtisanats() {
        List<ContenuDTO> artisanats = superAdminContenuService.getAllArtisanats();
        return ResponseEntity.ok(artisanats);
    }

    /**
     * Récupère tous les proverbes publics avec toutes leurs informations.
     * Accessible à tout le monde sans authentification.
     * 
     * @return Liste des proverbes publics
     */
    @GetMapping("/contenus-publics/proverbes")
    public ResponseEntity<List<ContenuDTO>> getAllProverbes() {
        List<ContenuDTO> proverbes = superAdminContenuService.getAllProverbes();
        return ResponseEntity.ok(proverbes);
    }

    /**
     * Récupère toutes les devinettes publiques avec toutes leurs informations.
     * Accessible à tout le monde sans authentification.
     * 
     * @return Liste des devinettes publiques
     */
    @GetMapping("/contenus-publics/devinettes")
    public ResponseEntity<List<ContenuDTO>> getAllDevinettes() {
        List<ContenuDTO> devinettes = superAdminContenuService.getAllDevinettes();
        return ResponseEntity.ok(devinettes);
    }

    /**
     * Crée un quiz public (accessible à tous).
     * Seul le super-admin peut créer des quiz publics.
     * 
     * @param request Requête de création de quiz
     * @param authentication Authentification
     * @return Quiz créé
     */
    @PostMapping("/quiz-publics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> creerQuizPublic(
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        QuizDTO quiz = superAdminQuizService.creerQuizPublic(request, adminId);
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    /**
     * Récupère tous les quiz publics.
     * Accessible à tous les utilisateurs authentifiés.
     * 
     * @return Liste des quiz publics
     */
    @GetMapping("/quiz-publics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<QuizDTO>> getQuizPublics() {
        List<QuizDTO> quiz = superAdminQuizService.getQuizPublics();
        return ResponseEntity.ok(quiz);
    }

    /**
     * Récupère un quiz public par son ID.
     * 
     * @param id ID du quiz
     * @return Quiz
     */
    @GetMapping("/quiz-publics/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<QuizDTO> getQuizPublicById(@PathVariable Long id) {
        QuizDTO quiz = superAdminQuizService.getQuizPublicById(id);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Ajoute une question à un quiz public.
     * 
     * @param quizId ID du quiz
     * @param request Requête de création de question
     * @param authentication Authentification
     * @return Question créée
     */
    @PostMapping("/quiz-publics/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> ajouterQuestionPublic(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        Object question = superAdminQuizService.ajouterQuestionPublic(quizId, request, adminId);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    /**
     * Ajoute une proposition à une question d'un quiz public.
     * 
     * @param questionId ID de la question
     * @param request Requête de création de proposition
     * @param authentication Authentification
     * @return Proposition créée
     */
    @PostMapping("/quiz-publics/questions/{questionId}/propositions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> ajouterPropositionPublic(
            @PathVariable Long questionId,
            @Valid @RequestBody PropositionRequest request,
            Authentication authentication) {
        Long adminId = getUserIdFromAuth(authentication);
        Object proposition = superAdminQuizService.ajouterPropositionPublic(questionId, request, adminId);
        return new ResponseEntity<>(proposition, HttpStatus.CREATED);
    }

    /**
     * Répond à un quiz public.
     * Accessible à tous les utilisateurs authentifiés.
     * 
     * @param request Requête de réponse au quiz
     * @param authentication Authentification
     * @return Résultat du quiz
     */
    @PostMapping("/quiz-publics/repondre")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> repondreQuizPublic(
            @Valid @RequestBody ReponseQuizRequest request,
            Authentication authentication) {
        Long utilisateurId = getUserIdFromAuth(authentication);
        Object resultat = superAdminQuizService.repondreQuizPublic(request, utilisateurId);
        return ResponseEntity.ok(resultat);
    }

    /**
     * Récupère l'ID de l'utilisateur depuis l'authentification.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
}

