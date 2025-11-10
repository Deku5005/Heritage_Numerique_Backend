package com.heritage.service;

import com.heritage.dto.ContenuDTO;
import com.heritage.dto.ContenuRequest;
import com.heritage.dto.DemandePublicationDTO;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des contenus familiaux.
 * 
 * Responsabilités :
 * - Création de contenus privés par l'administrateur
 * - Gestion des traductions (français, anglais, bambara)
 * - Workflow de publication publique (demande → validation → publication)
 * - Récupération des contenus publics et privés
 * 
 * Règles métier :
 * - Seul l'administrateur de famille peut créer des contenus privés
 * - Les membres peuvent demander la publication d'un contenu
 * - L'administrateur doit valider pour rendre public
 * - Chaque contenu appartient à une catégorie globale (Contes, Artisanat, etc.)
 */
@Service
public class ContenuService {

    private final ContenuRepository contenuRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CategorieRepository categorieRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final DemandePublicationRepository demandePublicationRepository;
    public ContenuService(
            ContenuRepository contenuRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            CategorieRepository categorieRepository,
            MembreFamilleRepository membreFamilleRepository,
            DemandePublicationRepository demandePublicationRepository) {
        this.contenuRepository = contenuRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.categorieRepository = categorieRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.demandePublicationRepository = demandePublicationRepository;
    }

    /**
     * Crée un nouveau contenu privé pour une famille.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est EDITEUR ou ADMIN de la famille
     * 2. Vérifier que la catégorie existe
     * 3. Créer le contenu avec statut BROUILLON
     * 4. Le contenu est privé par défaut (visible uniquement par la famille)
     * 
     * Règle métier :
     * - Les membres EDITEUR et ADMIN peuvent créer des contenus
     * - Les LECTEUR ne peuvent que consulter
     * 
     * @param request Requête de création de contenu
     * @param auteurId ID de l'utilisateur créateur
     * @return DTO du contenu créé
     */
    @Transactional
    public ContenuDTO createContenu(ContenuRequest request, Long auteurId) {
        // 1. Vérifier que l'utilisateur existe
        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 2. Vérifier que la famille existe
        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 3. Vérifier que l'utilisateur est EDITEUR ou ADMIN de la famille
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        // Règle métier : EDITEUR et ADMIN peuvent créer des contenus, pas LECTEUR
        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer des contenus");
        }

        // 4. Vérifier que la catégorie existe
        Categorie categorie = categorieRepository.findById(request.getIdCategorie())
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        // 5. Créer le contenu
        Contenu contenu = new Contenu();
        contenu.setFamille(famille);
        contenu.setAuteur(auteur);
        contenu.setCategorie(categorie);
        contenu.setTitre(request.getTitre());
        contenu.setDescription(request.getDescription());
        contenu.setTypeContenu(request.getTypeContenu());
        contenu.setUrlFichier(request.getUrlFichier());
        contenu.setTailleFichier(request.getTailleFichier());
        contenu.setDuree(request.getDuree());
        contenu.setDateEvenement(request.getDateEvenement());
        contenu.setLieu(request.getLieu());
        contenu.setRegion(request.getRegion());
        
        // Par défaut, le contenu est en BROUILLON (privé à la famille)
        contenu.setStatut(request.getStatut() != null ? request.getStatut() : "BROUILLON");

        contenu = contenuRepository.save(contenu);

        return convertToDTO(contenu);
    }

    /**
     * Demande la publication publique d'un contenu.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est ADMIN de la famille
     * 2. Vérifier que le contenu existe et appartient à la famille
     * 3. Créer une demande de publication avec statut EN_ATTENTE
     * 4. Le SUPERADMIN devra valider pour que le contenu devienne public
     * 
     * Règle métier :
     * - Seul l'ADMIN de la famille peut demander la publication
     * - Le SUPERADMIN (ROLE_ADMIN) valide la publication
     * 
     * @param contenuId ID du contenu
     * @param demandeurId ID de l'utilisateur demandeur (doit être ADMIN famille)
     * @return DemandePublicationDTO contenant le statut de la demande
     */
    @Transactional
    public DemandePublicationDTO demanderPublication(Long contenuId, Long demandeurId) {
        // 1. Vérifier que le contenu existe
        Contenu contenu = contenuRepository.findById(contenuId)
                .orElseThrow(() -> new NotFoundException("Contenu non trouvé"));

        // 2. Vérifier que l'utilisateur est ADMIN de la famille
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(demandeurId, contenu.getFamille().getId())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        // Règle métier : Seul l'ADMIN de la famille ou le SUPERADMIN peut demander la publication publique
        Utilisateur demandeur = utilisateurRepository.findById(demandeurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
        
        boolean isSuperAdmin = "ROLE_ADMIN".equals(demandeur.getRole());
        boolean isFamilleAdmin = RoleFamille.ADMIN.equals(membreFamille.getRoleFamille());
        
        // Debug pour comprendre le problème
        System.out.println("=== DEBUG DEMANDE PUBLICATION ===");
        System.out.println("Demandeur ID: " + demandeurId);
        System.out.println("Demandeur Role Global: " + demandeur.getRole());
        System.out.println("MembreFamille Role: " + membreFamille.getRoleFamille());
        System.out.println("isSuperAdmin: " + isSuperAdmin);
        System.out.println("isFamilleAdmin: " + isFamilleAdmin);
        System.out.println("==================================");
        
        if (!isSuperAdmin && !isFamilleAdmin) {
            throw new UnauthorizedException("Seul l'administrateur de la famille peut demander la publication publique. Role actuel: " + membreFamille.getRoleFamille());
        }

        // 3. Vérifier qu'il n'y a pas déjà une demande en attente
        boolean demandeExistante = demandePublicationRepository
                .existsByContenuIdAndStatut(contenuId, "EN_ATTENTE");
        
        if (demandeExistante) {
            throw new BadRequestException("Une demande de publication est déjà en attente pour ce contenu");
        }

        // 4. Créer la demande de publication
        DemandePublication demande = new DemandePublication();
        demande.setContenu(contenu);
        demande.setDemandeur(demandeur);
        demande.setStatut("EN_ATTENTE");
        
        demande = demandePublicationRepository.save(demande);
        
        // TODO: Envoyer une notification au SUPERADMIN
        // Tous les ROLE_ADMIN doivent être notifiés de la demande
        
        // 5. Retourner le DTO avec le statut de la demande
        return convertDemandeToDTO(demande);
    }

    /**
     * Valide une demande de publication et rend le contenu public.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est SUPERADMIN (ROLE_ADMIN)
     * 2. Vérifier que la demande existe et est EN_ATTENTE
     * 3. Mettre à jour le statut de la demande à APPROUVEE
     * 4. Changer le statut du contenu à PUBLIE (devient public)
     * 5. Notifier l'ADMIN de la famille qui a demandé
     * 
     * Règle métier :
     * - Seul l'ADMIN famille peut demander la publication
     * - Seul le SUPERADMIN (ROLE_ADMIN) peut valider
     * - Une fois publié, le contenu est visible par tous
     * 
     * @param demandeId ID de la demande
     * @param valideurId ID du SUPERADMIN valideur
     */
    @Transactional
    public void validerPublication(Long demandeId, Long valideurId) {
        // 1. Vérifier que la demande existe
        DemandePublication demande = demandePublicationRepository.findById(demandeId)
                .orElseThrow(() -> new NotFoundException("Demande de publication non trouvée"));

        // 2. Vérifier que la demande est EN_ATTENTE
        if (!"EN_ATTENTE".equals(demande.getStatut())) {
            throw new BadRequestException("Cette demande a déjà été traitée");
        }

        // 3. Vérifier que l'utilisateur est SUPERADMIN (ROLE_ADMIN)
        Utilisateur valideur = utilisateurRepository.findById(valideurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!"ROLE_ADMIN".equals(valideur.getRole())) {
            throw new UnauthorizedException("Seul le super-administrateur peut valider les publications publiques");
        }

        // 4. Valider la demande
        demande.setValideur(valideur);
        demande.setStatut("APPROUVEE");
        demande.setDateTraitement(java.time.LocalDateTime.now());
        demandePublicationRepository.save(demande);

        // 5. Rendre le contenu public
        Contenu contenu = demande.getContenu();
        contenu.setStatut("PUBLIE");
        contenuRepository.save(contenu);

        // TODO: Envoyer une notification à l'ADMIN de la famille qui a demandé
    }

    /**
     * Rejette une demande de publication.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est SUPERADMIN
     * 2. Mettre à jour le statut de la demande à REJETEE
     * 3. Notifier l'ADMIN de la famille
     * 
     * @param demandeId ID de la demande
     * @param valideurId ID du SUPERADMIN
     * @param commentaire Raison du rejet
     */
    @Transactional
    public void rejeterPublication(Long demandeId, Long valideurId, String commentaire) {
        // 1. Vérifier que la demande existe
        DemandePublication demande = demandePublicationRepository.findById(demandeId)
                .orElseThrow(() -> new NotFoundException("Demande de publication non trouvée"));

        // 2. Vérifier que la demande est EN_ATTENTE
        if (!"EN_ATTENTE".equals(demande.getStatut())) {
            throw new BadRequestException("Cette demande a déjà été traitée");
        }

        // 3. Vérifier que l'utilisateur est SUPERADMIN
        Utilisateur valideur = utilisateurRepository.findById(valideurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!"ROLE_ADMIN".equals(valideur.getRole())) {
            throw new UnauthorizedException("Seul le super-administrateur peut rejeter les publications");
        }

        // 4. Rejeter la demande
        demande.setValideur(valideur);
        demande.setStatut("REJETEE");
        demande.setCommentaire(commentaire);
        demande.setDateTraitement(java.time.LocalDateTime.now());
        demandePublicationRepository.save(demande);

        // TODO: Envoyer une notification à l'ADMIN de la famille
    }

    /**
     * Récupère tous les contenus publics.
     * Accessible par tous les utilisateurs (même non connectés potentiellement).
     * 
     * @return Liste des contenus publics
     */
    @Transactional(readOnly = true)
    public List<ContenuDTO> getContenusPublics() {
        List<Contenu> contenus = contenuRepository.findByStatut("PUBLIE");
        return contenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les contenus privés d'une famille.
     * Accessible uniquement par les membres de la famille.
     * 
     * @param familleId ID de la famille
     * @param utilisateurId ID de l'utilisateur demandeur
     * @return Liste des contenus privés
     */
    @Transactional(readOnly = true)
    public List<ContenuDTO> getContenusPrivesFamille(Long familleId, Long utilisateurId) {
        // Vérifier que l'utilisateur est membre de la famille
        boolean estMembre = membreFamilleRepository
                .existsByUtilisateurIdAndFamilleId(utilisateurId, familleId);

        if (!estMembre) {
            throw new UnauthorizedException("Vous n'êtes pas membre de cette famille");
        }

        // Récupérer les contenus de la famille (BROUILLON et ARCHIVE)
        List<Contenu> contenus = contenuRepository.findByFamilleIdAndStatutNot(familleId, "PUBLIE");
        
        return contenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les demandes de publication d'une famille.
     * Accessible uniquement par les membres de la famille.
     * 
     * @param familleId ID de la famille
     * @param utilisateurId ID de l'utilisateur demandeur
     * @return Liste des demandes de publication
     */
    @Transactional(readOnly = true)
    public List<DemandePublicationDTO> getDemandesPublicationFamille(Long familleId, Long utilisateurId) {
        // Vérifier que l'utilisateur est membre de la famille
        boolean estMembre = membreFamilleRepository
                .existsByUtilisateurIdAndFamilleId(utilisateurId, familleId);

        if (!estMembre) {
            throw new UnauthorizedException("Vous n'êtes pas membre de cette famille");
        }

        // Récupérer toutes les demandes de publication de la famille
        List<DemandePublication> demandes = demandePublicationRepository.findByContenuFamilleId(familleId);
        
        return demandes.stream()
                .map(this::convertDemandeToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu en DTO.
     * 
     * @param contenu Entité à convertir
     * @return DTO
     */
    private ContenuDTO convertToDTO(Contenu contenu) {
        return ContenuDTO.builder()
                .id(contenu.getId())
                .idFamille(contenu.getFamille().getId())
                .idAuteur(contenu.getAuteur().getId())
                .nomAuteur(contenu.getAuteur().getNom() + " " + contenu.getAuteur().getPrenom())
                .idCategorie(contenu.getCategorie().getId())
                .nomCategorie(contenu.getCategorie().getNom())
                .titre(contenu.getTitre())
                .description(contenu.getDescription())
                .typeContenu(contenu.getTypeContenu())
                .urlFichier(contenu.getUrlFichier())
                .urlPhoto(contenu.getUrlPhoto())
                .tailleFichier(contenu.getTailleFichier())
                .duree(contenu.getDuree())
                .dateEvenement(contenu.getDateEvenement())
                .lieu(contenu.getLieu())
                .region(contenu.getRegion())
                .statut(contenu.getStatut())
                .dateCreation(contenu.getDateCreation())
                .dateModification(contenu.getDateModification())
                .build();
    }

    /**
     * Convertit une entité DemandePublication en DTO.
     * 
     * @param demande Entité DemandePublication
     * @return DTO de la demande
     */
    private DemandePublicationDTO convertDemandeToDTO(DemandePublication demande) {
        return DemandePublicationDTO.builder()
                .id(demande.getId())
                .idContenu(demande.getContenu().getId())
                .titreContenu(demande.getContenu().getTitre())
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

