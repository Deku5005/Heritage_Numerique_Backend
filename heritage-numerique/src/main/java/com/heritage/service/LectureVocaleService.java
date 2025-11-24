package com.heritage.service;

import com.heritage.entite.Contenu;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.ContenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour gérer la lecture vocale des contenus.
 * Combine le texte du contenu avec le contenu extrait des fichiers pour la lecture.
 */
@Service
public class LectureVocaleService {

    private final ContenuRepository contenuRepository;
    private final FileContentService fileContentService;
    private final DjeliaTextToSpeechService djeliaTextToSpeechService;
    private final DjeliaTranslationService djeliaTranslationService;

    // Langue source par défaut (français)
    private static final String LANGUE_SOURCE_DEFAUT = "fra_Latn";

    public LectureVocaleService(ContenuRepository contenuRepository,
                                FileContentService fileContentService,
                                DjeliaTextToSpeechService djeliaTextToSpeechService,
                                DjeliaTranslationService djeliaTranslationService) {
        this.contenuRepository = contenuRepository;
        this.fileContentService = fileContentService;
        this.djeliaTextToSpeechService = djeliaTextToSpeechService;
        this.djeliaTranslationService = djeliaTranslationService;
    }

    /**
     * Récupère le texte complet d'un contenu pour la lecture vocale.
     * Inclut le titre, la description, le contenu textuel et le contenu extrait du fichier.
     * 
     * @param contenuId ID du contenu
     * @return Texte complet pour la lecture vocale
     */
    @Transactional(readOnly = true)
    public String getTexteCompletPourLecture(Long contenuId) {
        Contenu contenu = contenuRepository.findById(contenuId)
                .orElseThrow(() -> new NotFoundException("Contenu non trouvé"));

        StringBuilder texteComplet = new StringBuilder();

        // 1. Ajouter le titre
        if (contenu.getTitre() != null && !contenu.getTitre().trim().isEmpty()) {
            texteComplet.append(contenu.getTitre()).append(". ");
        }

        // 2. Ajouter la description
        if (contenu.getDescription() != null && !contenu.getDescription().trim().isEmpty()) {
            texteComplet.append(contenu.getDescription()).append(". ");
        }

        // 3. Ajouter le contenu spécifique selon le type
        String typeContenu = contenu.getTypeContenu();
        if ("PROVERBE".equals(typeContenu)) {
            if (contenu.getTexteProverbe() != null && !contenu.getTexteProverbe().trim().isEmpty()) {
                texteComplet.append("Proverbe: ").append(contenu.getTexteProverbe()).append(". ");
            }
            if (contenu.getSignificationProverbe() != null && !contenu.getSignificationProverbe().trim().isEmpty()) {
                texteComplet.append("Signification: ").append(contenu.getSignificationProverbe()).append(". ");
            }
            if (contenu.getOrigineProverbe() != null && !contenu.getOrigineProverbe().trim().isEmpty()) {
                texteComplet.append("Origine: ").append(contenu.getOrigineProverbe()).append(". ");
            }
        }
        // Pour les devinettes, CONTE, ARTISANAT, etc., le contenu principal est dans la description
        // qui a déjà été ajouté ci-dessus

        // 4. Ajouter les informations contextuelles (lieu, région) si disponibles
        if (contenu.getLieu() != null && !contenu.getLieu().trim().isEmpty()) {
            texteComplet.append("Lieu: ").append(contenu.getLieu()).append(". ");
        }
        if (contenu.getRegion() != null && !contenu.getRegion().trim().isEmpty()) {
            texteComplet.append("Région: ").append(contenu.getRegion()).append(". ");
        }

        // 5. Ajouter le contenu extrait du fichier si disponible
        if (contenu.getUrlFichier() != null && !contenu.getUrlFichier().trim().isEmpty()) {
            try {
                String contenuFichier = fileContentService.lireContenuFichier(contenu.getUrlFichier());
                if (contenuFichier != null && !contenuFichier.trim().isEmpty() 
                    && !contenuFichier.startsWith("Erreur") 
                    && !contenuFichier.startsWith("Aucun")) {
                    texteComplet.append("Contenu du fichier: ").append(contenuFichier);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la lecture du fichier pour la lecture vocale: " + e.getMessage());
                // Continuer sans le contenu du fichier
            }
        }

        String texteFinal = texteComplet.toString().trim();
        if (texteFinal.isEmpty()) {
            return "Aucun contenu disponible pour la lecture.";
        }

        return texteFinal;
    }

    /**
     * Génère l'audio pour un contenu dans une langue spécifique.
     * Si la langue demandée est différente du français (langue source), le texte est traduit avant la lecture.
     * 
     * @param contenuId ID du contenu
     * @param language Langue pour la lecture (fr, en, bm ou codes Djelia)
     * @return Bytes de l'audio généré, ou null en cas d'erreur
     */
    @Transactional(readOnly = true)
    public byte[] genererAudio(Long contenuId, String language) {
        // Récupérer le texte complet (texte original en français de la base de données)
        String texte = getTexteCompletPourLecture(contenuId);
        
        if (texte == null || texte.trim().isEmpty()) {
            System.err.println("❌ Aucun texte disponible pour la génération audio");
            return null;
        }

        // Mapper la langue vers le code Djelia
        String djeliaLangCible = djeliaTextToSpeechService.mapToDjeliaLanguageCode(language);
        
        // Si la langue cible est différente du français, traduire le texte
        if (!djeliaLangCible.equals(LANGUE_SOURCE_DEFAUT)) {
            try {
                System.out.println("🌐 Traduction du texte de " + LANGUE_SOURCE_DEFAUT + " vers " + djeliaLangCible + " pour la lecture vocale");
                String texteTraduit = djeliaTranslationService.translate(texte, LANGUE_SOURCE_DEFAUT, djeliaLangCible);
                if (texteTraduit != null && !texteTraduit.trim().isEmpty() && !texteTraduit.equals(texte)) {
                    texte = texteTraduit;
                    System.out.println("✅ Texte traduit avec succès (" + texte.length() + " caractères)");
                } else {
                    System.out.println("⚠️ Traduction non disponible ou identique, utilisation du texte original");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la traduction pour la lecture vocale: " + e.getMessage());
                System.err.println("   Utilisation du texte original en français");
                // Continuer avec le texte original en cas d'erreur de traduction
            }
        }

        // Générer l'audio dans la langue demandée (texte traduit ou original)
        return djeliaTextToSpeechService.textToSpeech(texte, djeliaLangCible);
    }

    /**
     * Vérifie si un contenu existe et est accessible.
     * 
     * @param contenuId ID du contenu
     * @return true si le contenu existe
     */
    @Transactional(readOnly = true)
    public boolean contenuExiste(Long contenuId) {
        return contenuRepository.existsById(contenuId);
    }


    /**
     * Vérifie si un contenu existe et est public (statut PUBLIE).
     * 
     * @param contenuId ID du contenu
     * @return true si le contenu existe et est public
     */
    @Transactional(readOnly = true)
    public boolean contenuEstPublic(Long contenuId) {
        return contenuRepository.findById(contenuId)
                .map(contenu -> "PUBLIE".equals(contenu.getStatut()))
                .orElse(false);
    }
}

