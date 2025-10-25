package com.heritage.service;

import com.heritage.dto.DashboardPersonnelDTO;
import com.heritage.dto.FamilleDTO;
import com.heritage.dto.InvitationDTO;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard personnel de l'utilisateur.
 * 
 * Responsabilités :
 * - Afficher les invitations en attente de l'utilisateur
 * - Afficher les familles de l'utilisateur
 * - Afficher les statistiques personnelles
 */
@Service
public class DashboardPersonnelService {

    private final UtilisateurRepository utilisateurRepository;
    private final InvitationService invitationService;
    private final FamilleService familleService;
    private final ContenuRepository contenuRepository;
    private final QuizRepository quizRepository;
    private final NotificationRepository notificationRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final InvitationRepository invitationRepository;

    public DashboardPersonnelService(
            UtilisateurRepository utilisateurRepository,
            InvitationService invitationService,
            FamilleService familleService,
            ContenuRepository contenuRepository,
            QuizRepository quizRepository,
            NotificationRepository notificationRepository,
            MembreFamilleRepository membreFamilleRepository,
            InvitationRepository invitationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.invitationService = invitationService;
        this.familleService = familleService;
        this.contenuRepository = contenuRepository;
        this.quizRepository = quizRepository;
        this.notificationRepository = notificationRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * Récupère le dashboard personnel de l'utilisateur.
     * 
     * Workflow :
     * 1. Récupérer les informations utilisateur
     * 2. Récupérer les invitations en attente (par email)
     * 3. Récupérer les familles de l'utilisateur avec détails
     * 4. Calculer les statistiques personnelles
     * 5. Récupérer les invitations avec détails complets
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Dashboard personnel
     */
    @Transactional(readOnly = true)
    public DashboardPersonnelDTO getDashboardPersonnel(Long utilisateurId) {
        // 1. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 2. Récupérer les invitations en attente pour cet email
        List<InvitationDTO> invitationsEnAttente = invitationService
                .getInvitationsEnAttente(utilisateur.getEmail());

        // 3. Récupérer les familles de l'utilisateur avec détails
        List<FamilleDTO> familles = getFamillesAvecDetails(utilisateurId);

        // 4. Calculer les statistiques
        int nombreContenus = contenuRepository.findByAuteurId(utilisateurId).size();
        int nombreQuiz = getNombreQuizDansFamilles(utilisateurId);
        Long nombreNotifications = notificationRepository
                .countByDestinataireIdAndLu(utilisateurId, false);

        // 5. Récupérer les invitations avec détails complets
        List<InvitationDTO> invitationsAvecDetails = getInvitationsAvecDetails(utilisateur.getEmail());

        // 6. Construire le DTO
        return DashboardPersonnelDTO.builder()
                .userId(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .nombreFamillesAppartenance(familles.size())
                .nombreInvitationsRecues(invitationsAvecDetails.size())
                .nombreContenusCreés(nombreContenus)
                .nombreQuizCreés(nombreQuiz)
                .nombreNotificationsNonLues(Math.toIntExact(nombreNotifications))
                .invitationsEnAttente(invitationsEnAttente)
                .familles(familles)
                .invitationsRecues(invitationsAvecDetails)
                .build();
    }

    /**
     * Récupère les invitations en attente de l'utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des invitations en attente
     */
    @Transactional(readOnly = true)
    public Object getInvitationsEnAttente(Long utilisateurId) {
        // 1. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 2. Récupérer les invitations en attente pour cet email
        return invitationService.getInvitationsEnAttente(utilisateur.getEmail());
    }

    /**
     * Récupère les familles de l'utilisateur avec détails complets.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des familles avec nom, admin et date de création
     */
    @Transactional(readOnly = true)
    private List<FamilleDTO> getFamillesAvecDetails(Long utilisateurId) {
        return membreFamilleRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(membreFamille -> {
                    FamilleDTO dto = new FamilleDTO();
                    dto.setId(membreFamille.getFamille().getId());
                    dto.setNom(membreFamille.getFamille().getNom());
                    dto.setDescription(membreFamille.getFamille().getDescription());
                    dto.setEthnie(membreFamille.getFamille().getEthnie());
                    dto.setRegion(membreFamille.getFamille().getRegion());
                    dto.setDateCreation(membreFamille.getFamille().getDateCreation());
                    
                    // Récupérer l'admin de la famille
                    membreFamilleRepository.findByFamilleId(membreFamille.getFamille().getId())
                            .stream()
                            .filter(mf -> "ADMIN".equals(mf.getRoleFamille().toString()))
                            .findFirst()
                            .ifPresent(admin -> {
                                dto.setNomAdmin(admin.getUtilisateur().getNom() + " " + admin.getUtilisateur().getPrenom());
                            });
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcule le nombre total de quiz créés dans toutes les familles de l'utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Nombre total de quiz
     */
    @Transactional(readOnly = true)
    private int getNombreQuizDansFamilles(Long utilisateurId) {
        // Récupérer les IDs des familles de l'utilisateur
        List<Long> familleIds = membreFamilleRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(mf -> mf.getFamille().getId())
                .collect(Collectors.toList());
        
        // Compter les quiz dans ces familles
        return familleIds.stream()
                .mapToInt(familleId -> quizRepository.findByFamilleId(familleId).size())
                .sum();
    }

    /**
     * Récupère toutes les invitations reçues par l'utilisateur avec détails complets.
     * 
     * @param email Email de l'utilisateur
     * @return Liste des invitations avec nom de famille, admin et date
     */
    @Transactional(readOnly = true)
    private List<InvitationDTO> getInvitationsAvecDetails(String email) {
        return invitationRepository.findByEmailInvite(email)
                .stream()
                .map(invitation -> {
                    InvitationDTO dto = new InvitationDTO();
                    dto.setId(invitation.getId());
                    dto.setCodeInvitation(invitation.getCodeInvitation());
                    dto.setEmailInvite(invitation.getEmailInvite());
                    dto.setStatut(invitation.getStatut());
                    dto.setDateCreation(invitation.getDateCreation());
                    dto.setDateExpiration(invitation.getDateExpiration());
                    
                    // Informations de la famille
                    dto.setNomFamille(invitation.getFamille().getNom());
                    dto.setIdFamille(invitation.getFamille().getId());
                    
                    // Récupérer l'admin de la famille
                    membreFamilleRepository.findByFamilleId(invitation.getFamille().getId())
                            .stream()
                            .filter(mf -> "ADMIN".equals(mf.getRoleFamille().toString()))
                            .findFirst()
                            .ifPresent(admin -> {
                                dto.setNomAdmin(admin.getUtilisateur().getNom() + " " + admin.getUtilisateur().getPrenom());
                            });
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

