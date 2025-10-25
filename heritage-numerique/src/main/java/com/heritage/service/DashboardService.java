package com.heritage.service;

import com.heritage.dto.DashboardDTO;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour le tableau de bord familial.
 * 
 * Responsabilités :
 * - Afficher les statistiques d'une famille
 * - Nombre de membres, invitations, contenus, quiz, notifications
 */
@Service
public class DashboardService {

    private final FamilleRepository familleRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final InvitationRepository invitationRepository;
    private final ContenuRepository contenuRepository;
    private final QuizRepository quizRepository;
    private final NotificationRepository notificationRepository;
    private final ArbreGenealogiqueRepository arbreRepository;

    public DashboardService(
            FamilleRepository familleRepository,
            MembreFamilleRepository membreFamilleRepository,
            InvitationRepository invitationRepository,
            ContenuRepository contenuRepository,
            QuizRepository quizRepository,
            NotificationRepository notificationRepository,
            ArbreGenealogiqueRepository arbreRepository) {
        this.familleRepository = familleRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.invitationRepository = invitationRepository;
        this.contenuRepository = contenuRepository;
        this.quizRepository = quizRepository;
        this.notificationRepository = notificationRepository;
        this.arbreRepository = arbreRepository;
    }

    /**
     * Récupère les statistiques du dashboard d'une famille.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est membre de la famille
     * 2. Calculer toutes les statistiques
     * 
     * @param familleId ID de la famille
     * @param utilisateurId ID de l'utilisateur demandeur
     * @return DTO du dashboard avec les statistiques
     */
    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(Long familleId, Long utilisateurId) {
        // 1. Vérifier que la famille existe
        var famille = familleRepository.findById(familleId)
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 2. Vérifier que l'utilisateur est membre de la famille
        boolean estMembre = membreFamilleRepository
                .existsByUtilisateurIdAndFamilleId(utilisateurId, familleId);

        if (!estMembre) {
            throw new UnauthorizedException("Vous n'êtes pas membre de cette famille");
        }

        // 3. Calculer les statistiques
        
        // Nombre de membres
        int nombreMembres = membreFamilleRepository.findByFamilleId(familleId).size();

        // Nombre d'invitations en attente
        long nombreInvitations = invitationRepository
                .findByFamilleId(familleId).stream()
                .filter(inv -> "EN_ATTENTE".equals(inv.getStatut()))
                .count();

        // Nombre de contenus privés (BROUILLON + ARCHIVE)
        long nombreContenusPrives = contenuRepository.findByFamilleId(familleId).stream()
                .filter(c -> !"PUBLIE".equals(c.getStatut()))
                .count();

        // Nombre de contenus publics
        long nombreContenusPublics = contenuRepository.findByFamilleId(familleId).stream()
                .filter(c -> "PUBLIE".equals(c.getStatut()))
                .count();

        // Nombre de quiz actifs
        int nombreQuiz = quizRepository.findByFamilleIdAndActif(familleId, true).size();

        // Nombre de notifications non lues pour l'utilisateur
        Long nombreNotifications = notificationRepository
                .countByDestinataireIdAndLu(utilisateurId, false);

        // Nombre d'arbres généalogiques
        int nombreArbres = arbreRepository.findByFamilleId(familleId).size();

        // 4. Construire le DTO
        return DashboardDTO.builder()
                .idFamille(familleId)
                .nomFamille(famille.getNom())
                .nombreMembres(nombreMembres)
                .nombreInvitationsEnAttente(Math.toIntExact(nombreInvitations))
                .nombreContenusPrives(Math.toIntExact(nombreContenusPrives))
                .nombreContenusPublics(Math.toIntExact(nombreContenusPublics))
                .nombreQuizActifs(nombreQuiz)
                .nombreNotificationsNonLues(Math.toIntExact(nombreNotifications))
                .nombreArbreGenealogiques(nombreArbres)
                .build();
    }
}

