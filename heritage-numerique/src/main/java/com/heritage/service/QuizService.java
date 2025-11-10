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
 * Service de gestion des quiz.
 * 
 * Responsabilités :
 * - Création de quiz pour contenus privés (par les membres)
 * - Création de quiz pour contenus publics (par l'admin ou super-admin)
 * - Gestion des questions et propositions
 * - Enregistrement des résultats
 * 
 * Règles métier :
 * - Les membres peuvent créer des quiz pour les contenus privés de leur famille
 * - L'administrateur peut créer des quiz pour tous les contenus publics
 * - Le super-admin peut créer des quiz pour tous les contenus
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final ContenuRepository contenuRepository;
    private final QuestionQuizRepository questionQuizRepository;
    private final PropositionRepository propositionRepository;
    private final ResultatQuizRepository resultatQuizRepository;

    public QuizService(
            QuizRepository quizRepository,
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            MembreFamilleRepository membreFamilleRepository,
            ContenuRepository contenuRepository,
            QuestionQuizRepository questionQuizRepository,
            PropositionRepository propositionRepository,
            ResultatQuizRepository resultatQuizRepository) {
        this.quizRepository = quizRepository;
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.contenuRepository = contenuRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.propositionRepository = propositionRepository;
        this.resultatQuizRepository = resultatQuizRepository;
    }

    /**
     * Crée un quiz pour une famille.
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est membre de la famille
     * 2. Vérifier les permissions selon le type de quiz :
     *    - Quiz privé : membre EDITEUR ou ADMIN
     *    - Quiz public : ADMIN de la famille ou ROLE_ADMIN global
     * 3. Créer le quiz
     * 
     * @param request Requête de création de quiz
     * @param createurId ID du créateur
     * @return DTO du quiz créé
     */
    @Transactional
    public QuizDTO createQuiz(QuizRequest request, Long createurId) {
        // 1. Vérifier que l'utilisateur existe
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 2. Vérifier que la famille existe
        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 3. Vérifier que l'utilisateur est membre de la famille
        MembreFamille membreFamille = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(createurId, request.getIdFamille())
                .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de cette famille"));

        // 4. Vérifier les permissions selon le rôle
        // Les membres EDITEUR ou ADMIN peuvent créer des quiz
        if ("LECTEUR".equals(membreFamille.getRoleFamille())) {
            throw new UnauthorizedException("Vous devez être EDITEUR ou ADMIN pour créer un quiz");
        }

        // 5. Créer le quiz
        Quiz quiz = new Quiz();
        quiz.setFamille(famille);
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
     * Crée un quiz pour un contenu public (par admin ou super-admin).
     * 
     * Workflow :
     * 1. Vérifier que l'utilisateur est ROLE_ADMIN (super-admin)
     *    OU ADMIN de la famille du contenu
     * 2. Vérifier que le contenu est PUBLIE (public)
     * 3. Créer le quiz associé au contenu
     * 
     * @param contenuId ID du contenu public
     * @param request Requête de création de quiz
     * @param createurId ID du créateur (admin)
     * @return DTO du quiz créé
     */
    @Transactional
    public QuizDTO createQuizForPublicContenu(Long contenuId, QuizRequest request, Long createurId) {
        // 1. Vérifier que le contenu existe et est public
        Contenu contenu = contenuRepository.findById(contenuId)
                .orElseThrow(() -> new NotFoundException("Contenu non trouvé"));

        if (!"PUBLIE".equals(contenu.getStatut())) {
            throw new UnauthorizedException("Ce contenu n'est pas public");
        }

        // 2. Vérifier que l'utilisateur existe
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 3. Vérifier les permissions :
        //    - ROLE_ADMIN (super-admin) peut créer pour tous les contenus publics
        //    - ADMIN de la famille peut créer pour les contenus de sa famille
        boolean isSuperAdmin = "ROLE_ADMIN".equals(createur.getRole());
        boolean isFamilleAdmin = false;

        if (!isSuperAdmin) {
            MembreFamille membreFamille = membreFamilleRepository
                    .findByUtilisateurIdAndFamilleId(createurId, contenu.getFamille().getId())
                    .orElse(null);

            isFamilleAdmin = membreFamille != null && "ADMIN".equals(membreFamille.getRoleFamille());
        }

        if (!isSuperAdmin && !isFamilleAdmin) {
            throw new UnauthorizedException("Seul le super-admin ou l'admin de la famille peut créer un quiz pour ce contenu public");
        }

        // 4. Créer le quiz
        Quiz quiz = new Quiz();
        quiz.setFamille(contenu.getFamille());
        quiz.setContenu(contenu); // Lier le quiz au contenu
        quiz.setCreateur(createur);
        quiz.setTitre(request.getTitre());
        quiz.setDescription(request.getDescription());
        quiz.setDifficulte(request.getDifficulte() != null ? request.getDifficulte() : "MOYEN");
        quiz.setTempsLimite(request.getTempsLimite());
        quiz.setActif(request.getActif() != null ? request.getActif() : true);

        quiz = quizRepository.save(quiz);

        // TODO: Envoyer notification de création de quiz

        return convertToDTO(quiz);
    }

    /**
     * Récupère tous les quiz actifs d'une famille.
     * 
     * @param familleId ID de la famille
     * @param utilisateurId ID de l'utilisateur demandeur
     * @return Liste des quiz
     */
    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizFamille(Long familleId, Long utilisateurId) {
        // Vérifier que l'utilisateur est membre de la famille
        boolean estMembre = membreFamilleRepository
                .existsByUtilisateurIdAndFamilleId(utilisateurId, familleId);

        if (!estMembre) {
            throw new UnauthorizedException("Vous n'êtes pas membre de cette famille");
        }

        List<Quiz> quiz = quizRepository.findByFamilleIdAndActif(familleId, true);
        
        return quiz.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un quiz par son ID.
     * 
     * @param id ID du quiz
     * @return DTO du quiz
     */
    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        return convertToDTO(quiz);
    }

    /**
     * Crée une question pour un quiz.
     * 
     * @param request Requête de création de question
     * @param createurId ID du créateur
     * @return Question créée
     */
    @Transactional
    public Object createQuestion(QuestionRequest request, Long createurId) {
        // 1. Vérifier que le quiz existe
        Quiz quiz = quizRepository.findById(request.getIdQuiz())
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // 2. Vérifier les permissions
        boolean peutCreer = peutModifierQuiz(quiz, createurId);
        if (!peutCreer) {
            throw new UnauthorizedException("Vous n'avez pas le droit de modifier ce quiz");
        }

        // 3. Créer la question
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
     * Crée une proposition pour une question de quiz.
     * 
     * @param request Requête de création de proposition
     * @param createurId ID du créateur
     * @return Proposition créée
     */
    @Transactional
    public Object createProposition(PropositionRequest request, Long questionId, Long createurId) {
        // 1. Vérifier que la question existe
        QuestionQuiz question = questionQuizRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question non trouvée"));

        // 2. Vérifier les permissions
        boolean peutCreer = peutModifierQuiz(question.getQuiz(), createurId);
        if (!peutCreer) {
            throw new UnauthorizedException("Vous n'avez pas le droit de modifier ce quiz");
        }

        // 3. Créer la proposition
        Proposition proposition = new Proposition();
        proposition.setQuestion(question);
        proposition.setTexteProposition(request.getTexte());
        proposition.setOrdre(request.getOrdre());
        proposition.setEstCorrecte(request.getEstCorrecte() != null ? request.getEstCorrecte() : false);

        proposition = propositionRepository.save(proposition);
        
        return proposition;
    }

    /**
     * Répond à un quiz et calcule le score.
     * 
     * @param request Requête de réponse au quiz
     * @param utilisateurId ID de l'utilisateur
     * @return Résultat du quiz
     */
    @Transactional
    public Object repondreQuiz(ReponseQuizRequest request, Long utilisateurId) {
        // 1. Vérifier que le quiz existe
        Quiz quiz = quizRepository.findById(request.getIdQuiz())
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // 2. Vérifier que l'utilisateur peut répondre au quiz
        boolean peutRepondre = peutRepondreQuiz(quiz, utilisateurId);
        if (!peutRepondre) {
            throw new UnauthorizedException("Vous n'avez pas le droit de répondre à ce quiz");
        }

        // 3. Calculer le score
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

        // 4. Enregistrer le résultat
        ResultatQuiz resultat = new ResultatQuiz();
        resultat.setQuiz(quiz);
        resultat.setUtilisateur(utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé")));
        resultat.setScore(score);
        resultat.setScoreMax(scoreMax);
        resultat.setTempsEcoule(request.getTempsEcoule());

        resultat = resultatQuizRepository.save(resultat);
        
        return resultat;
    }

    /**
     * Récupère les questions d'un quiz.
     * 
     * @param quizId ID du quiz
     * @param utilisateurId ID de l'utilisateur
     * @return Questions du quiz
     */
    @Transactional(readOnly = true)
    public Object getQuestionsQuiz(Long quizId, Long utilisateurId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // Vérifier que l'utilisateur peut voir ce quiz
        boolean peutVoir = peutVoirQuiz(quiz, utilisateurId);
        if (!peutVoir) {
            throw new UnauthorizedException("Vous n'avez pas le droit de voir ce quiz");
        }

        return quiz.getQuestions().stream()
                .map(this::convertQuestionToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les résultats d'un quiz.
     * 
     * @param quizId ID du quiz
     * @param utilisateurId ID de l'utilisateur
     * @return Résultats du quiz
     */
    @Transactional(readOnly = true)
    public Object getResultatsQuiz(Long quizId, Long utilisateurId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // Vérifier que l'utilisateur peut voir les résultats
        boolean peutVoir = peutVoirResultatsQuiz(quiz, utilisateurId);
        if (!peutVoir) {
            throw new UnauthorizedException("Vous n'avez pas le droit de voir les résultats de ce quiz");
        }

        return resultatQuizRepository.findByQuizId(quizId).stream()
                .map(this::convertResultatToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un utilisateur peut modifier un quiz.
     */
    private boolean peutModifierQuiz(Quiz quiz, Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // Super-admin peut tout modifier
        if ("ROLE_ADMIN".equals(utilisateur.getRole())) {
            return true;
        }

        // Créateur du quiz peut le modifier
        if (quiz.getCreateur().getId().equals(utilisateurId)) {
            return true;
        }

        // Admin de la famille peut modifier
        MembreFamille membre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(utilisateurId, quiz.getFamille().getId())
                .orElse(null);

        return membre != null && "ADMIN".equals(membre.getRoleFamille().toString());
    }

    /**
     * Vérifie si un utilisateur peut répondre à un quiz.
     */
    private boolean peutRepondreQuiz(Quiz quiz, Long utilisateurId) {
        // Si c'est un quiz public, tout le monde peut répondre
        if (quiz.getFamille() == null) {
            return true;
        }

        // Pour les quiz familiaux, seuls les membres peuvent répondre
        return membreFamilleRepository
                .existsByUtilisateurIdAndFamilleId(utilisateurId, quiz.getFamille().getId());
    }

    /**
     * Vérifie si un utilisateur peut voir un quiz.
     */
    private boolean peutVoirQuiz(Quiz quiz, Long utilisateurId) {
        return peutRepondreQuiz(quiz, utilisateurId);
    }

    /**
     * Vérifie si un utilisateur peut voir les résultats d'un quiz.
     */
    private boolean peutVoirResultatsQuiz(Quiz quiz, Long utilisateurId) {
        // Admin de la famille ou créateur du quiz peut voir tous les résultats
        if (peutModifierQuiz(quiz, utilisateurId)) {
            return true;
        }

        // Sinon, on peut voir ses propres résultats
        return true;
    }

    /**
     * Convertit une question en DTO.
     */
    private Object convertQuestionToDTO(QuestionQuiz question) {
        return new Object() {
            public final Long id = question.getId();
            public final String texteQuestion = question.getTexteQuestion();
            public final String typeQuestion = question.getTypeQuestion();
            public final Integer ordre = question.getOrdre();
            public final Integer points = question.getPoints();
            public final List<Object> propositions = question.getPropositions().stream()
                    .map(p -> new Object() {
                        public final Long id = p.getId();
                        public final String texteProposition = p.getTexteProposition();
                        public final Integer ordre = p.getOrdre();
                        // Ne pas exposer si c'est correct pour éviter la triche
                    })
                    .collect(Collectors.toList());
        };
    }

    /**
     * Convertit un résultat de quiz en DTO.
     */
    private Object convertResultatToDTO(ResultatQuiz resultat) {
        return new Object() {
            public final Long id = resultat.getId();
            public final Object utilisateur = new Object() {
                public final Long id = resultat.getUtilisateur().getId();
                public final String nom = resultat.getUtilisateur().getNom();
                public final String prenom = resultat.getUtilisateur().getPrenom();
            };
            public final Integer score = resultat.getScore();
            public final Integer scoreMax = resultat.getScoreMax();
            public final Integer tempsEcoule = resultat.getTempsEcoule();
            public final String datePassage = resultat.getDatePassage().toString();
        };
    }

    /**
     * Convertit une entité Quiz en DTO.
     * 
     * @param quiz Entité à convertir
     * @return DTO
     */
    private QuizDTO convertToDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .idFamille(quiz.getFamille().getId())
                .nomFamille(quiz.getFamille().getNom())
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

