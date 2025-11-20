package com.heritage.service;

import com.heritage.dto.CategorieDTO;
import com.heritage.dto.DemandePublicationDTO;
import com.heritage.dto.StatistiquesDTO;
import com.heritage.entite.Categorie;
import com.heritage.entite.DemandePublication;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour les fonctionnalités du super-admin.
 * 
 * Responsabilités :
 * - Visualiser les statistiques globales de la plateforme
 * - Créer/supprimer des catégories pour les contenus
 * - Créer des quiz pour tous les contenus publics
 * - Gérer les utilisateurs et familles (optionnel)
 * 
 * Règles métier :
 * - Seul le ROLE_ADMIN peut accéder à ces fonctionnalités
 */
@Service
public class SuperAdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final FamilleRepository familleRepository;
    private final ContenuRepository contenuRepository;
    private final QuizRepository quizRepository;
    private final CategorieRepository categorieRepository;
    private final InvitationRepository invitationRepository;
    private final NotificationRepository notificationRepository;
    private final DemandePublicationRepository demandePublicationRepository;

    public SuperAdminService(
            UtilisateurRepository utilisateurRepository,
            FamilleRepository familleRepository,
            ContenuRepository contenuRepository,
            QuizRepository quizRepository,
            CategorieRepository categorieRepository,
            InvitationRepository invitationRepository,
            NotificationRepository notificationRepository,
            DemandePublicationRepository demandePublicationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.familleRepository = familleRepository;
        this.contenuRepository = contenuRepository;
        this.quizRepository = quizRepository;
        this.categorieRepository = categorieRepository;
        this.invitationRepository = invitationRepository;
        this.notificationRepository = notificationRepository;
        this.demandePublicationRepository = demandePublicationRepository;
    }

    /**
     * Récupère les statistiques globales de la plateforme.
     * 
     * Sécurité : Seul le ROLE_ADMIN peut accéder.
     * 
     * @param adminId ID de l'admin
     * @return DTO des statistiques
     */
    @Transactional(readOnly = true)
    public StatistiquesDTO getStatistiquesGlobales(Long adminId) {
        // 1. Vérifier que l'utilisateur est ROLE_ADMIN
        verifierSuperAdmin(adminId);

        // 2. Calculer les statistiques
        long nombreUtilisateurs = utilisateurRepository.count();
        long nombreFamilles = familleRepository.count();
        long nombreContenus = contenuRepository.count();
        long nombreContenusPublics = contenuRepository.findByStatut("PUBLIE").size();
        long nombreQuiz = quizRepository.count();
        long nombreCategories = categorieRepository.count();
        long nombreInvitations = invitationRepository.findByStatut("EN_ATTENTE").size();
        long nombreNotifications = notificationRepository.count();

        return StatistiquesDTO.builder()
                .nombreUtilisateurs(nombreUtilisateurs)
                .nombreFamilles(nombreFamilles)
                .nombreContenus(nombreContenus)
                .nombreContenusPublics(nombreContenusPublics)
                .nombreQuiz(nombreQuiz)
                .nombreCategories(nombreCategories)
                .nombreInvitationsEnAttente(nombreInvitations)
                .nombreNotificationsEnvoyees(nombreNotifications)
                .build();
    }

    /**
     * Crée une nouvelle catégorie de contenu.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est ROLE_ADMIN
     * 2. Vérifier que la catégorie n'existe pas déjà
     * 3. Créer la catégorie
     * 
     * @param nom Nom de la catégorie
     * @param description Description
     * @param icone Icône
     * @param adminId ID de l'admin
     * @return DTO de la catégorie créée
     */
    @Transactional
    public CategorieDTO createCategorie(String nom, String description, String icone, Long adminId) {
        // 1. Vérifier que l'utilisateur est ROLE_ADMIN
        verifierSuperAdmin(adminId);

        // 2. Vérifier que la catégorie n'existe pas déjà
        if (categorieRepository.findByNom(nom).isPresent()) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }

        // 3. Créer la catégorie
        Categorie categorie = new Categorie();
        categorie.setNom(nom);
        categorie.setDescription(description);
        categorie.setIcone(icone);

        categorie = categorieRepository.save(categorie);

        return convertCategorieToDTO(categorie);
    }

    /**
     * Supprime une catégorie.
     * 
     * Attention : La suppression échouera si des contenus utilisent cette catégorie
     * (contrainte de clé étrangère).
     * 
     * @param categorieId ID de la catégorie
     * @param adminId ID de l'admin
     */
    @Transactional
    public void deleteCategorie(Long categorieId, Long adminId) {
        // 1. Vérifier que l'utilisateur est ROLE_ADMIN
        verifierSuperAdmin(adminId);

        // 2. Vérifier que la catégorie existe
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // 3. Supprimer la catégorie
        // Note : Échouera si des contenus utilisent cette catégorie (FK constraint)
        categorieRepository.delete(categorie);
    }

    /**
     * Récupère toutes les catégories.
     * 
     * @return Liste des catégories
     */
    @Transactional(readOnly = true)
    public List<CategorieDTO> getAllCategories() {
        return categorieRepository.findAll().stream()
                .map(this::convertCategorieToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie que l'utilisateur est super-admin (ROLE_ADMIN).
     * 
     * @param utilisateurId ID de l'utilisateur
     * @throws UnauthorizedException si l'utilisateur n'est pas super-admin
     */
    private void verifierSuperAdmin(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!"ROLE_ADMIN".equals(utilisateur.getRole())) {
            throw new UnauthorizedException("Accès réservé aux super-administrateurs");
        }
    }

    /**
     * Récupère toutes les demandes de publication en attente.
     * Utilisé par le super-admin pour voir les demandes à valider.
     * 
     * @param adminId ID du super-admin
     * @return Liste des demandes en attente
     */
    @Transactional(readOnly = true)
    public List<DemandePublicationDTO> getDemandesPublicationEnAttente(Long adminId) {
        // Vérifier que l'utilisateur est super-admin
        verifierSuperAdmin(adminId);

        List<DemandePublication> demandes = demandePublicationRepository
                .findByStatut("EN_ATTENTE");

        return demandes.stream()
                .map(this::convertDemandeToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Categorie en DTO.
     * 
     * @param categorie Entité à convertir
     * @return DTO
     */
    private CategorieDTO convertCategorieToDTO(Categorie categorie) {
        CategorieDTO dto = new CategorieDTO();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        dto.setDescription(categorie.getDescription());
        dto.setIcone(categorie.getIcone());
        dto.setDateCreation(categorie.getDateCreation());
        return dto;
    }

    /**
     * Convertit une entité DemandePublication en DTO.
     * 
     * @param demande Entité à convertir
     * @return DTO
     */
    private DemandePublicationDTO convertDemandeToDTO(DemandePublication demande) {
        com.heritage.entite.Contenu contenu = demande.getContenu();
        com.heritage.entite.Famille famille = contenu.getFamille();
        
        return DemandePublicationDTO.builder()
                .id(demande.getId())
                .idContenu(contenu.getId())
                .titreContenu(contenu.getTitre())
                .typeContenu(contenu.getTypeContenu())
                .idFamille(famille != null ? famille.getId() : null)
                .nomFamille(famille != null ? famille.getNom() : null)
                .idDemandeur(demande.getDemandeur().getId())
                .nomDemandeur(demande.getDemandeur().getNom() + " " + demande.getDemandeur().getPrenom())
                .idValideur(demande.getValideur() != null ? demande.getValideur().getId() : null)
                .nomValideur(demande.getValideur() != null ? 
                        demande.getValideur().getNom() + " " + demande.getValideur().getPrenom() : null)
                .statut(demande.getStatut())
                .commentaire(demande.getCommentaire())
                .dateDemande(demande.getDateDemande())
                .dateTraitement(demande.getDateTraitement())
                .build();
    }
}

