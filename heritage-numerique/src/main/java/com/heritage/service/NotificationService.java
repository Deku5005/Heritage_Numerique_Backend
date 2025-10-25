package com.heritage.service;

import com.heritage.entite.Notification;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.NotificationRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des notifications.
 * 
 * Responsabilités :
 * - Envoi de notifications (EMAIL, SMS, IN_APP)
 * - Gestion de l'historique des notifications
 * - Marquage des notifications comme lues
 * 
 * Types de notifications supportés :
 * - INVITATION : Invitation à rejoindre une famille
 * - ACCEPTATION : Acceptation d'une invitation
 * - CONTENU_PUBLIE : Contenu rendu public
 * - QUIZ_CREE : Nouveau quiz créé
 * - DEMANDE_PUBLICATION : Demande de publication d'un contenu
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Envoie une notification à un utilisateur.
     * 
     * Workflow :
     * 1. Vérifier que le destinataire existe
     * 2. Créer la notification avec le canal approprié
     * 3. Sauvegarder dans l'historique
     * 4. Envoyer effectivement (EMAIL/SMS selon le canal)
     * 
     * @param destinataireId ID du destinataire
     * @param type Type de notification
     * @param titre Titre de la notification
     * @param message Message de la notification
     * @param canal Canal d'envoi (EMAIL, SMS, IN_APP)
     * @param lien Lien vers la ressource concernée (optionnel)
     */
    @Transactional
    public void envoyerNotification(
            Long destinataireId,
            String type,
            String titre,
            String message,
            String canal,
            String lien) {
        
        // 1. Vérifier que le destinataire existe
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new NotFoundException("Utilisateur destinataire non trouvé"));

        // 2. Créer la notification
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setType(type);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setCanal(canal);
        notification.setLu(false);
        notification.setLien(lien);

        // 3. Sauvegarder l'historique
        notificationRepository.save(notification);

        // 4. Envoyer effectivement selon le canal
        switch (canal) {
            case "EMAIL":
                envoyerEmail(destinataire.getEmail(), titre, message);
                break;
            case "SMS":
                // TODO: Implémenter l'envoi de SMS
                envoyerSMS(destinataire, message);
                break;
            case "IN_APP":
                // Déjà sauvegardé, visible dans l'app
                break;
            default:
                // IN_APP par défaut
                break;
        }
    }

    /**
     * Envoie une notification d'invitation.
     * Utilisé lorsqu'un admin invite un membre à rejoindre une famille.
     * 
     * @param destinataireId ID du destinataire
     * @param nomFamille Nom de la famille
     * @param codeInvitation Code d'invitation
     */
    @Transactional
    public void notifierInvitation(Long destinataireId, String nomFamille, String codeInvitation) {
        String titre = "Invitation à rejoindre une famille";
        String message = String.format(
                "Vous avez été invité à rejoindre la famille '%s'. " +
                "Utilisez le code %s lors de votre inscription.",
                nomFamille, codeInvitation
        );

        envoyerNotification(destinataireId, "INVITATION", titre, message, "EMAIL", null);
    }

    /**
     * Envoie une notification d'acceptation d'invitation.
     * Utilisé lorsqu'un membre accepte une invitation.
     * 
     * @param adminId ID de l'administrateur de la famille
     * @param nomMembre Nom du membre qui a accepté
     * @param nomFamille Nom de la famille
     */
    @Transactional
    public void notifierAcceptationInvitation(Long adminId, String nomMembre, String nomFamille) {
        String titre = "Invitation acceptée";
        String message = String.format(
                "%s a accepté votre invitation et rejoint la famille '%s'.",
                nomMembre, nomFamille
        );

        envoyerNotification(adminId, "ACCEPTATION", titre, message, "IN_APP", null);
    }

    /**
     * Envoie une notification de contenu publié.
     * Utilisé lorsqu'un contenu privé devient public.
     * 
     * @param destinataireId ID du créateur du contenu
     * @param titreContenu Titre du contenu
     * @param contenuId ID du contenu
     */
    @Transactional
    public void notifierContenuPublie(Long destinataireId, String titreContenu, Long contenuId) {
        String titre = "Contenu publié";
        String message = String.format(
                "Votre contenu '%s' a été approuvé et est maintenant public.",
                titreContenu
        );
        String lien = "/api/contenu/" + contenuId;

        envoyerNotification(destinataireId, "CONTENU_PUBLIE", titre, message, "IN_APP", lien);
    }

    /**
     * Envoie une notification de quiz créé.
     * Utilisé lorsqu'un nouveau quiz est créé pour la famille.
     * 
     * @param destinataireId ID du membre de la famille
     * @param titreQuiz Titre du quiz
     * @param quizId ID du quiz
     */
    @Transactional
    public void notifierQuizCree(Long destinataireId, String titreQuiz, Long quizId) {
        String titre = "Nouveau quiz disponible";
        String message = String.format(
                "Un nouveau quiz '%s' a été créé pour votre famille. Testez vos connaissances !",
                titreQuiz
        );
        String lien = "/api/quiz/" + quizId;

        envoyerNotification(destinataireId, "QUIZ_CREE", titre, message, "IN_APP", lien);
    }

    /**
     * Récupère toutes les notifications d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des notifications
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long utilisateurId) {
        return notificationRepository.findByDestinataireIdOrderByDateEnvoiDesc(utilisateurId);
    }

    /**
     * Récupère les notifications non lues d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des notifications non lues
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsNonLues(Long utilisateurId) {
        return notificationRepository.findByDestinataireIdAndLu(utilisateurId, false);
    }

    /**
     * Marque une notification comme lue.
     * 
     * @param notificationId ID de la notification
     * @param utilisateurId ID de l'utilisateur (pour vérification)
     */
    @Transactional
    public void marquerCommeLue(Long notificationId, Long utilisateurId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification non trouvée"));

        // Vérifier que la notification appartient bien à l'utilisateur
        if (!notification.getDestinataire().getId().equals(utilisateurId)) {
            throw new NotFoundException("Notification non trouvée");
        }

        notification.setLu(true);
        notification.setDateLecture(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Compte le nombre de notifications non lues d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Nombre de notifications non lues
     */
    @Transactional(readOnly = true)
    public Long compterNotificationsNonLues(Long utilisateurId) {
        return notificationRepository.countByDestinataireIdAndLu(utilisateurId, false);
    }

    /**
     * Envoie un email.
     * TODO: Implémenter avec un service d'envoi d'emails (JavaMailSender, SendGrid, etc.)
     * 
     * @param email Email du destinataire
     * @param titre Titre de l'email
     * @param message Corps de l'email
     */
    private void envoyerEmail(String email, String titre, String message) {
        // TODO: Implémenter l'envoi d'email
        System.out.println("Envoi d'email à " + email);
        System.out.println("Titre: " + titre);
        System.out.println("Message: " + message);
    }

    /**
     * Envoie un SMS.
     * TODO: Implémenter avec un service d'envoi de SMS (Twilio, AWS SNS, etc.)
     * 
     * @param utilisateur Utilisateur destinataire
     * @param message Message du SMS
     */
    private void envoyerSMS(Utilisateur utilisateur, String message) {
        // TODO: Implémenter l'envoi de SMS
        System.out.println("Envoi de SMS à " + utilisateur.getEmail());
        System.out.println("Message: " + message);
    }
}

