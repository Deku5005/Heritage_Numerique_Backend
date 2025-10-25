package com.heritage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails.
 * 
 * Responsabilités :
 * - Envoi d'emails d'invitation
 * - Envoi de codes de connexion
 * - Gestion des templates d'emails
 * 
 * Configuration :
 * - Utilise JavaMailSender pour l'envoi
 * - Configuration SMTP dans application.properties
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${app.name:Heritage Numérique}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email d'invitation pour un nouvel utilisateur.
     * 
     * @param email Email du destinataire
     * @param nomInvite Nom de la personne invitée
     * @param nomFamille Nom de la famille
     * @param codeInvitation Code d'invitation
     * @param nomEmetteur Nom de l'émetteur
     * @param lienParente Lien de parenté
     */
    public void envoyerInvitationNouvelUtilisateur(
            String email,
            String nomInvite,
            String nomFamille,
            String codeInvitation,
            String nomEmetteur,
            String lienParente) {
        
        String sujet = "Invitation à rejoindre la famille " + nomFamille;
        String message = String.format("""
            Bonjour %s,
            
            %s vous invite à rejoindre la famille "%s" en tant que %s.
            
            Pour accepter cette invitation, veuillez vous inscrire sur l'application Heritage Numérique
            en utilisant le code d'invitation suivant :
            
            Code d'invitation : %s
            
            Ce code est valide pendant 48 heures.
            
            Cordialement,
            L'équipe %s
            """, nomInvite, nomEmetteur, nomFamille, lienParente, codeInvitation, appName);
        
        envoyerEmail(email, sujet, message);
    }

    /**
     * Envoie un code de connexion pour un utilisateur existant.
     * 
     * @param email Email du destinataire
     * @param nomInvite Nom de la personne invitée
     * @param nomFamille Nom de la famille
     * @param codeInvitation Code d'invitation
     * @param nomEmetteur Nom de l'émetteur
     * @param lienParente Lien de parenté
     */
    public void envoyerCodeConnexion(
            String email,
            String nomInvite,
            String nomFamille,
            String codeInvitation,
            String nomEmetteur,
            String lienParente) {
        
        String sujet = "Invitation à rejoindre la famille " + nomFamille;
        String message = String.format("""
            Bonjour %s,
            
            %s vous invite à rejoindre la famille "%s" en tant que %s.
            
            Vous avez déjà un compte sur Heritage Numérique. Pour accepter cette invitation,
            connectez-vous avec votre compte existant et utilisez le code d'invitation suivant :
            
            Code d'invitation : %s
            
            Ce code est valide pendant 48 heures.
            
            Cordialement,
            L'équipe %s
            """, nomInvite, nomEmetteur, nomFamille, lienParente, codeInvitation, appName);
        
        envoyerEmail(email, sujet, message);
    }

    /**
     * Envoie un email générique.
     * 
     * @param email Email du destinataire
     * @param sujet Sujet de l'email
     * @param message Corps de l'email
     */
    private void envoyerEmail(String email, String sujet, String message) {
        try {
            // Mode simulation pour le développement
            if (isSimulationMode()) {
                System.out.println("=== MODE SIMULATION EMAIL ===");
                System.out.println("Destinataire: " + email);
                System.out.println("Sujet: " + sujet);
                System.out.println("Message:");
                System.out.println(message);
                System.out.println("=== FIN SIMULATION ===");
                return;
            }
            
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(sujet);
            mailMessage.setText(message);
            mailMessage.setFrom("dolooumar60@gmail.com");
            
            mailSender.send(mailMessage);
            System.out.println("Email envoyé avec succès à : " + email);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + email + " : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
    
    /**
     * Vérifie si le mode simulation est activé.
     * Active le mode simulation si les credentials email ne sont pas configurés.
     */
    private boolean isSimulationMode() {
        // Mode simulation désactivé - envoi d'emails réels
        return false;
    }
}