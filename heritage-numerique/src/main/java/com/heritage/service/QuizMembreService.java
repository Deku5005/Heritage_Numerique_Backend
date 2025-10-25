package com.heritage.service;

import com.heritage.dto.*;
import com.heritage.entite.*;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des quiz des membres de famille.
 * Fournit des informations détaillées sur les quiz et la progression des membres.
 */
@Service
public class QuizMembreService {

    private final QuizRepository quizRepository;
    private final QuestionQuizRepository questionQuizRepository;
    private final ResultatQuizRepository resultatQuizRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final ContenuRepository contenuRepository;
    private final UtilisateurRepository utilisateurRepository;

    public QuizMembreService(QuizRepository quizRepository,
                            QuestionQuizRepository questionQuizRepository,
                            ResultatQuizRepository resultatQuizRepository,
                            MembreFamilleRepository membreFamilleRepository,
                            ContenuRepository contenuRepository,
                            UtilisateurRepository utilisateurRepository) {
        this.quizRepository = quizRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.resultatQuizRepository = resultatQuizRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.contenuRepository = contenuRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère tous les quiz d'une famille avec les informations de progression de chaque membre.
     * 
     * @param familleId ID de la famille
     * @return DTO avec tous les quiz et les informations des membres
     */
    @Transactional(readOnly = true)
    public QuizFamilleDTO getQuizFamille(Long familleId) {
        // 1. Récupérer tous les quiz de la famille
        List<Quiz> quizFamille = quizRepository.findByFamilleId(familleId);
        
        // 2. Récupérer tous les membres de la famille
        List<MembreFamille> membres = membreFamilleRepository.findByFamilleId(familleId);
        
        // 3. Construire les informations des membres avec leurs quiz
        List<QuizMembreDTO> membresQuiz = membres.stream()
                .map(membre -> {
                    List<QuizProgressionDTO> quizProgression = new ArrayList<>();
                    
                    for (Quiz quiz : quizFamille) {
                        QuizProgressionDTO quizProgressionDTO = getQuizProgression(quiz, membre.getUtilisateur().getId());
                        if (quizProgressionDTO != null) {
                            quizProgression.add(quizProgressionDTO);
                        }
                    }
                    
                    // Calculer les statistiques du membre
                    int nombreQuizTotal = quizProgression.size();
                    int nombreQuizTermines = (int) quizProgression.stream()
                            .filter(q -> "TERMINE".equals(q.getStatutQuiz()))
                            .count();
                    int nombreQuizEnCours = (int) quizProgression.stream()
                            .filter(q -> "EN_COURS".equals(q.getStatutQuiz()))
                            .count();
                    
                    int scoreTotalMembre = quizProgression.stream()
                            .mapToInt(QuizProgressionDTO::getScoreActuel)
                            .sum();
                    
                    return QuizMembreDTO.builder()
                            .idMembre(membre.getId())
                            .nomMembre(membre.getUtilisateur().getNom())
                            .prenomMembre(membre.getUtilisateur().getPrenom())
                            .emailMembre(membre.getUtilisateur().getEmail())
                            .roleMembre(membre.getRoleFamille().toString())
                            .lienParenteMembre(membre.getLienParente() != null ? 
                                    membre.getLienParente() : "Non spécifié")
                            .nombreQuizTotal(nombreQuizTotal)
                            .nombreQuizTermines(nombreQuizTermines)
                            .nombreQuizEnCours(nombreQuizEnCours)
                            .scoreTotalMembre(scoreTotalMembre)
                            .quizProgression(quizProgression)
                            .build();
                })
                .collect(Collectors.toList());
        
        // 4. Calculer les statistiques globales de la famille
        int nombreQuizTotal = quizFamille.size();
        int nombreMembresAvecQuiz = (int) membresQuiz.stream()
                .filter(m -> m.getNombreQuizTotal() > 0)
                .count();
        int scoreTotalFamille = membresQuiz.stream()
                .mapToInt(QuizMembreDTO::getScoreTotalMembre)
                .sum();
        
        return QuizFamilleDTO.builder()
                .idFamille(familleId)
                .nomFamille(membres.get(0).getFamille().getNom())
                .descriptionFamille(membres.get(0).getFamille().getDescription())
                .nombreQuizTotal(nombreQuizTotal)
                .nombreMembresAvecQuiz(nombreMembresAvecQuiz)
                .scoreTotalFamille(scoreTotalFamille)
                .membresQuiz(membresQuiz)
                .build();
    }

    /**
     * Récupère les quiz d'un membre spécifique avec ses informations de progression.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return DTO avec les quiz du membre
     */
    @Transactional(readOnly = true)
    public QuizMembreDTO getQuizMembre(Long familleId, Long membreId) {
        // 1. Vérifier que le membre existe et appartient à la famille
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        // 2. Récupérer tous les quiz de la famille
        List<Quiz> quizFamille = quizRepository.findByFamilleId(familleId);
        
        // 3. Construire les informations de progression du membre
        List<QuizProgressionDTO> quizProgression = quizFamille.stream()
                .map(quiz -> getQuizProgression(quiz, membre.getUtilisateur().getId()))
                .filter(quizProgressionDTO -> quizProgressionDTO != null)
                .collect(Collectors.toList());
        
        // 4. Calculer les statistiques du membre
        int nombreQuizTotal = quizProgression.size();
        int nombreQuizTermines = (int) quizProgression.stream()
                .filter(q -> "TERMINE".equals(q.getStatutQuiz()))
                .count();
        int nombreQuizEnCours = (int) quizProgression.stream()
                .filter(q -> "EN_COURS".equals(q.getStatutQuiz()))
                .count();
        
        int scoreTotalMembre = quizProgression.stream()
                .mapToInt(QuizProgressionDTO::getScoreActuel)
                .sum();
        
        return QuizMembreDTO.builder()
                .idMembre(membre.getId())
                .nomMembre(membre.getUtilisateur().getNom())
                .prenomMembre(membre.getUtilisateur().getPrenom())
                .emailMembre(membre.getUtilisateur().getEmail())
                .roleMembre(membre.getRoleFamille().toString())
                .lienParenteMembre(membre.getLienParente() != null ? 
                        membre.getLienParente() : "Non spécifié")
                .nombreQuizTotal(nombreQuizTotal)
                .nombreQuizTermines(nombreQuizTermines)
                .nombreQuizEnCours(nombreQuizEnCours)
                .scoreTotalMembre(scoreTotalMembre)
                .quizProgression(quizProgression)
                .build();
    }

    /**
     * Récupère les informations de progression d'un quiz pour un utilisateur.
     * 
     * @param quiz Quiz à analyser
     * @param utilisateurId ID de l'utilisateur
     * @return DTO avec les informations de progression
     */
    private QuizProgressionDTO getQuizProgression(Quiz quiz, Long utilisateurId) {
        // 1. Récupérer le contenu (conte) associé au quiz
        // Note: Quiz n'a pas de contenu direct, on utilise la description
        String contenu = quiz.getDescription();
        
        // 2. Récupérer le nombre total de questions du quiz
        List<QuestionQuiz> questions = questionQuizRepository.findByQuizId(quiz.getId());
        int nombreQuestionsTotal = questions.size();
        
        // 3. Vérifier s'il y a déjà un résultat pour ce quiz
        List<ResultatQuiz> resultats = resultatQuizRepository.findByQuizIdAndUtilisateurId(quiz.getId(), utilisateurId);
        ResultatQuiz resultat = resultats.isEmpty() ? null : resultats.get(0);
        
        // 4. Déterminer le statut et les informations de progression
        String statutQuiz;
        int nombreQuestionsRepondues;
        int scoreActuel;
        LocalDateTime dateDerniereReponse;
        
        if (resultat != null) {
            // L'utilisateur a déjà répondu au quiz
            statutQuiz = "TERMINE";
            nombreQuestionsRepondues = nombreQuestionsTotal;
            scoreActuel = resultat.getScore();
            dateDerniereReponse = resultat.getDatePassage();
        } else {
            // L'utilisateur n'a pas encore répondu au quiz
            statutQuiz = "NON_COMMENCE";
            nombreQuestionsRepondues = 0;
            scoreActuel = 0;
            dateDerniereReponse = null;
        }
        
        return QuizProgressionDTO.builder()
                .idQuiz(quiz.getId())
                .titreQuiz(quiz.getTitre())
                .descriptionQuiz(quiz.getDescription())
                .nomConte(quiz.getTitre()) // Utiliser le titre du quiz
                .descriptionConte(contenu) // Utiliser la description du quiz
                .nomAuteurConte(quiz.getCreateur().getNom())
                .prenomAuteurConte(quiz.getCreateur().getPrenom())
                .nombreQuestionsTotal(nombreQuestionsTotal)
                .nombreQuestionsRepondues(nombreQuestionsRepondues)
                .scoreActuel(scoreActuel)
                .dateDerniereReponse(dateDerniereReponse)
                .statutQuiz(statutQuiz)
                .typeQuiz(quiz.getTypeQuiz())
                .idContenu(null) // Pas de contenu direct pour Quiz
                .idFamille(quiz.getFamille().getId())
                .nomFamille(quiz.getFamille().getNom())
                .build();
    }
}
