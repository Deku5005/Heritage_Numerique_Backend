package com.heritage.service;

import com.heritage.dto.*;
import com.heritage.entite.*;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les quiz publics créés par le super-admin.
 * Ces quiz sont accessibles à tous les utilisateurs et ne sont pas liés à une famille.
 */
@Service
public class SuperAdminQuizService {

    private final QuizRepository quizRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final QuestionQuizRepository questionQuizRepository;
    private final PropositionRepository propositionRepository;
    private final ResultatQuizRepository resultatQuizRepository;

    public SuperAdminQuizService(
            QuizRepository quizRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            QuestionQuizRepository questionQuizRepository,
            PropositionRepository propositionRepository,
            ResultatQuizRepository resultatQuizRepository) {
        this.quizRepository = quizRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.propositionRepository = propositionRepository;
        this.resultatQuizRepository = resultatQuizRepository;
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
     * Récupère la famille virtuelle PUBLIC.
     */
    private Famille getFamillePublic() {
        return familleRepository.findByNom("PUBLIC_HERITAGE")
                .orElseThrow(() -> new NotFoundException("Famille PUBLIC non trouvée"));
    }

    /**
     * Crée un quiz public (accessible à tous).
     */
    @Transactional
    public QuizDTO creerQuizPublic(QuizRequest request, Long adminId) {
        verifierSuperAdmin(adminId);
        
        Utilisateur createur = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famillePublic = getFamillePublic();

        Quiz quiz = new Quiz();
        quiz.setFamille(famillePublic);
        quiz.setCreateur(createur);
        quiz.setTitre(request.getTitre());
        quiz.setDescription(request.getDescription());
        quiz.setDifficulte(request.getDifficulte() != null ? request.getDifficulte() : "MOYEN");
        quiz.setTempsLimite(request.getTempsLimite());
        quiz.setActif(request.getActif() != null ? request.getActif() : true);

        quiz = quizRepository.save(quiz);

        return convertToDTO(quiz);
    }

    /**
     * Récupère tous les quiz publics.
     */
    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizPublics() {
        Famille famillePublic = getFamillePublic();
        
        List<Quiz> quiz = quizRepository.findByFamilleIdAndActif(famillePublic.getId(), true);
        
        return quiz.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un quiz public par son ID.
     */
    @Transactional(readOnly = true)
    public QuizDTO getQuizPublicById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // Vérifier que c'est un quiz public
        if (!"PUBLIC_HERITAGE".equals(quiz.getFamille().getNom())) {
            throw new UnauthorizedException("Ce n'est pas un quiz public");
        }

        return convertToDTO(quiz);
    }

    /**
     * Ajoute une question à un quiz public.
     */
    @Transactional
    public Object ajouterQuestionPublic(Long quizId, QuestionRequest request, Long adminId) {
        verifierSuperAdmin(adminId);
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        if (!"PUBLIC_HERITAGE".equals(quiz.getFamille().getNom())) {
            throw new UnauthorizedException("Ce n'est pas un quiz public");
        }

        QuestionQuiz question = new QuestionQuiz();
        question.setQuiz(quiz);
        question.setTexteQuestion(request.getTexteQuestion());
        question.setTypeQuestion(request.getTypeQuestion() != null ? request.getTypeQuestion() : "QCM");
        question.setOrdre(request.getOrdre());
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);

        question = questionQuizRepository.save(question);
        
        return question;
    }

    /**
     * Ajoute une proposition à une question d'un quiz public.
     */
    @Transactional
    public Object ajouterPropositionPublic(Long questionId, PropositionRequest request, Long adminId) {
        verifierSuperAdmin(adminId);
        
        QuestionQuiz question = questionQuizRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question non trouvée"));

        if (!"PUBLIC_HERITAGE".equals(question.getQuiz().getFamille().getNom())) {
            throw new UnauthorizedException("Ce n'est pas une question d'un quiz public");
        }

        Proposition proposition = new Proposition();
        proposition.setQuestion(question);
        proposition.setTexteProposition(request.getTexte());
        proposition.setOrdre(request.getOrdre());
        proposition.setEstCorrecte(request.getEstCorrecte() != null ? request.getEstCorrecte() : false);

        proposition = propositionRepository.save(proposition);
        
        return proposition;
    }

    /**
     * Répond à un quiz public et calcule le score.
     * Accessible à tous les utilisateurs.
     */
    @Transactional
    public Object repondreQuizPublic(ReponseQuizRequest request, Long utilisateurId) {
        Quiz quiz = quizRepository.findById(request.getIdQuiz())
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        if (!"PUBLIC_HERITAGE".equals(quiz.getFamille().getNom())) {
            throw new UnauthorizedException("Ce n'est pas un quiz public");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // Calculer le score
        int score = 0;
        int scoreMax = quiz.getQuestions().size();
        
        for (ReponseQuestionRequest reponse : request.getReponses()) {
            Proposition proposition = propositionRepository.findById(reponse.getIdProposition())
                    .orElse(null);
            
            if (proposition != null && proposition.getEstCorrecte()) {
                QuestionQuiz question = proposition.getQuestion();
                score += question.getPoints();
            }
        }

        // Enregistrer le résultat
        ResultatQuiz resultat = new ResultatQuiz();
        resultat.setQuiz(quiz);
        resultat.setUtilisateur(utilisateur);
        resultat.setScore(score);
        resultat.setScoreMax(scoreMax);
        resultat.setTempsEcoule(request.getTempsEcoule());

        resultat = resultatQuizRepository.save(resultat);
        
        return resultat;
    }

    /**
     * Convertit une entité Quiz en DTO.
     */
    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .idFamille(quiz.getFamille() != null ? quiz.getFamille().getId() : null)
                .nomFamille(quiz.getFamille() != null ? quiz.getFamille().getNom() : "PUBLIC")
                .idCreateur(quiz.getCreateur().getId())
                .nomCreateur(quiz.getCreateur().getNom() + " " + quiz.getCreateur().getPrenom())
                .titre(quiz.getTitre())
                .description(quiz.getDescription())
                .difficulte(quiz.getDifficulte())
                .tempsLimite(quiz.getTempsLimite())
                .actif(quiz.getActif())
                .nombreQuestions(quiz.getQuestions().size())
                .dateCreation(quiz.getDateCreation())
                .build();
    }
}

