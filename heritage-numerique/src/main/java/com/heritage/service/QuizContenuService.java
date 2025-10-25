package com.heritage.service;

import com.heritage.dto.*;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des quiz sur les contenus (contes).
 * Les quiz peuvent être créés par les admins/éditeurs de famille ou par le super-admin.
 */
@Service
public class QuizContenuService {

    private final QuizRepository quizRepository;
    private final QuestionQuizRepository questionQuizRepository;
    private final PropositionRepository propositionRepository;
    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final ResultatQuizRepository resultatQuizRepository;
    private final UtilisateurRepository utilisateurRepository;

    public QuizContenuService(QuizRepository quizRepository,
                             QuestionQuizRepository questionQuizRepository,
                             PropositionRepository propositionRepository,
                             ContenuRepository contenuRepository,
                             MembreFamilleRepository membreFamilleRepository,
                             ResultatQuizRepository resultatQuizRepository,
                             UtilisateurRepository utilisateurRepository) {
        this.quizRepository = quizRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.propositionRepository = propositionRepository;
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.resultatQuizRepository = resultatQuizRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Crée un quiz pour un contenu (conte) par un membre de famille.
     * 
     * @param request Requête de création du quiz
     * @param auteurId ID de l'utilisateur qui crée le quiz
     * @return DTO du quiz créé
     */
    @Transactional
    public QuizDTO creerQuizContenu(QuizContenuRequest request, Long auteurId) {
        // 1. Vérifier que le contenu existe et est un conte
        Contenu contenu = contenuRepository.findById(request.getIdContenu())
                .orElseThrow(() -> new NotFoundException("Contenu non trouvé"));

        if (!"CONTE".equals(contenu.getTypeContenu())) {
            throw new BadRequestException("Seuls les contes peuvent avoir des quiz");
        }

        // 2. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // 3. Vérifier que l'utilisateur est membre de la famille avec le rôle ADMIN ou EDITEUR
        MembreFamille membre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, contenu.getFamille().getId())
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de cette famille"));

        if (!membre.getRoleFamille().equals(RoleFamille.ADMIN) && 
            !membre.getRoleFamille().equals(RoleFamille.EDITEUR)) {
            throw new BadRequestException("Seuls les administrateurs et éditeurs peuvent créer des quiz");
        }

        // 4. Vérifier qu'il n'y a pas déjà un quiz pour ce contenu
        // Note: Les quiz ne sont plus liés directement aux contenus par ID
        // Cette vérification est désactivée pour l'instant

        // 5. Créer le quiz
        Quiz quiz = new Quiz();
        quiz.setTitre(request.getTitre());
        quiz.setDescription(request.getDescription());
        quiz.setFamille(contenu.getFamille());
        quiz.setCreateur(utilisateur);
        quiz.setActif(true);
        quiz = quizRepository.save(quiz);

        // 5. Créer les questions et propositions
        int ordreQuestion = 1;
        for (QuestionQuizRequest questionRequest : request.getQuestions()) {
            QuestionQuiz question = new QuestionQuiz();
            question.setQuiz(quiz);
            question.setTexteQuestion(questionRequest.getQuestion());
            question.setTypeQuestion(questionRequest.getTypeReponse().toString());
            question.setOrdre(ordreQuestion++);
            question = questionQuizRepository.save(question);

            if (questionRequest.getTypeReponse() == TypeReponse.QCM) {
                // Créer les propositions pour QCM
                int ordreProposition = 1;
                for (PropositionRequest propositionRequest : questionRequest.getPropositions()) {
                    Proposition proposition = new Proposition();
                    proposition.setQuestion(question);
                    proposition.setTexteProposition(propositionRequest.getTexte());
                    proposition.setEstCorrecte(propositionRequest.getEstCorrecte());
                    proposition.setOrdre(ordreProposition++);
                    propositionRepository.save(proposition);
                }
            } else if (questionRequest.getTypeReponse() == TypeReponse.VRAI_FAUX) {
                // Créer les propositions pour VRAI_FAUX
                Proposition propositionVrai = new Proposition();
                propositionVrai.setQuestion(question);
                propositionVrai.setTexteProposition("Vrai");
                propositionVrai.setEstCorrecte(questionRequest.getReponseVraiFaux());
                propositionVrai.setOrdre(1);
                propositionRepository.save(propositionVrai);

                Proposition propositionFaux = new Proposition();
                propositionFaux.setQuestion(question);
                propositionFaux.setTexteProposition("Faux");
                propositionFaux.setEstCorrecte(!questionRequest.getReponseVraiFaux());
                propositionFaux.setOrdre(2);
                propositionRepository.save(propositionFaux);
            }
        }

        return convertToQuizDTO(quiz);
    }

    /**
     * Crée un quiz pour un contenu public par le super-admin.
     * 
     * @param request Requête de création du quiz
     * @param auteurId ID du super-admin
     * @return DTO du quiz créé
     */
    @Transactional
    public QuizDTO creerQuizPublic(QuizContenuRequest request, Long auteurId) {
        // 1. Vérifier que l'utilisateur est super-admin
        Utilisateur utilisateur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!"ROLE_ADMIN".equals(utilisateur.getRole())) {
            throw new BadRequestException("Seul le super-admin peut créer des quiz publics");
        }

        // 2. Vérifier que le contenu existe et est un conte public
        Contenu contenu = contenuRepository.findById(request.getIdContenu())
                .orElseThrow(() -> new NotFoundException("Contenu non trouvé"));

        if (!"CONTE".equals(contenu.getTypeContenu())) {
            throw new BadRequestException("Seuls les contes peuvent avoir des quiz");
        }

        if (!"PUBLIE".equals(contenu.getStatut())) {
            throw new BadRequestException("Seuls les contenus publics peuvent avoir des quiz publics");
        }

        // 3. Vérifier qu'il n'y a pas déjà un quiz pour ce contenu
        // Note: Les quiz ne sont plus liés directement aux contenus par ID
        // Cette vérification est désactivée pour l'instant

        // 4. Créer le quiz public
        Quiz quiz = new Quiz();
        quiz.setTitre(request.getTitre());
        quiz.setDescription(request.getDescription());
        quiz.setFamille(contenu.getFamille());
        quiz.setCreateur(utilisateur);
        quiz.setActif(true);
        quiz = quizRepository.save(quiz);

        // 5. Créer les questions et propositions (même logique que pour les quiz familiaux)
        int ordreQuestion = 1;
        for (QuestionQuizRequest questionRequest : request.getQuestions()) {
            QuestionQuiz question = new QuestionQuiz();
            question.setQuiz(quiz);
            question.setTexteQuestion(questionRequest.getQuestion());
            question.setTypeQuestion(questionRequest.getTypeReponse().toString());
            question.setOrdre(ordreQuestion++);
            question = questionQuizRepository.save(question);

            if (questionRequest.getTypeReponse() == TypeReponse.QCM) {
                int ordreProposition = 1;
                for (PropositionRequest propositionRequest : questionRequest.getPropositions()) {
                    Proposition proposition = new Proposition();
                    proposition.setQuestion(question);
                    proposition.setTexteProposition(propositionRequest.getTexte());
                    proposition.setEstCorrecte(propositionRequest.getEstCorrecte());
                    proposition.setOrdre(ordreProposition++);
                    propositionRepository.save(proposition);
                }
            } else if (questionRequest.getTypeReponse() == TypeReponse.VRAI_FAUX) {
                Proposition propositionVrai = new Proposition();
                propositionVrai.setQuestion(question);
                propositionVrai.setTexteProposition("Vrai");
                propositionVrai.setEstCorrecte(questionRequest.getReponseVraiFaux());
                propositionVrai.setOrdre(1);
                propositionRepository.save(propositionVrai);

                Proposition propositionFaux = new Proposition();
                propositionFaux.setQuestion(question);
                propositionFaux.setTexteProposition("Faux");
                propositionFaux.setEstCorrecte(!questionRequest.getReponseVraiFaux());
                propositionFaux.setOrdre(2);
                propositionRepository.save(propositionFaux);
            }
        }

        return convertToQuizDTO(quiz);
    }

    /**
     * Récupère les contes d'une famille avec leurs quiz éventuels.
     * 
     * @param familleId ID de la famille
     * @return Liste des contes avec leurs quiz
     */
    @Transactional(readOnly = true)
    public List<ConteDTO> getContesAvecQuiz(Long familleId) {
        List<Contenu> contes = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "CONTE");
        
        return contes.stream()
                .map(conte -> {
                    ConteDTO conteDTO = convertToConteDTO(conte);
                    
                    // Vérifier s'il y a un quiz pour ce conte
                    // Note: Les quiz ne sont plus liés directement aux contenus par ID
                    Quiz quiz = null;
                    if (quiz != null) {
                        conteDTO.setQuiz(convertToQuizDTO(quiz));
                    }
                    
                    return conteDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les contes publics avec leurs quiz éventuels.
     * 
     * @return Liste des contes publics avec leurs quiz
     */
    @Transactional(readOnly = true)
    public List<ConteDTO> getContesPublicsAvecQuiz() {
        List<Contenu> contes = contenuRepository.findByStatutAndTypeContenu("PUBLIE", "CONTE");
        
        return contes.stream()
                .map(conte -> {
                    ConteDTO conteDTO = convertToConteDTO(conte);
                    
                    // Vérifier s'il y a un quiz pour ce conte
                    // Note: Les quiz ne sont plus liés directement aux contenus par ID
                    Quiz quiz = null;
                    if (quiz != null) {
                        conteDTO.setQuiz(convertToQuizDTO(quiz));
                    }
                    
                    return conteDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Répond à un quiz et calcule le score.
     * 
     * @param request Requête de réponse au quiz
     * @param utilisateurId ID de l'utilisateur qui répond
     * @return Résultat du quiz
     */
    @Transactional
    public ResultatQuizDTO repondreQuiz(ReponseQuizRequest request, Long utilisateurId) {
        // 1. Vérifier que le quiz existe
        Quiz quiz = quizRepository.findById(request.getIdQuiz())
                .orElseThrow(() -> new NotFoundException("Quiz non trouvé"));

        // 2. Vérifier que l'utilisateur peut répondre au quiz
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est membre de la famille du quiz
        MembreFamille membre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(utilisateurId, quiz.getFamille().getId())
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de cette famille"));

        // 3. Vérifier qu'il n'y a pas déjà une réponse de cet utilisateur
        if (resultatQuizRepository.existsByQuizIdAndUtilisateurId(request.getIdQuiz(), utilisateurId)) {
            throw new BadRequestException("Vous avez déjà répondu à ce quiz");
        }

        // 4. Calculer le score
        int score = 0;
        int totalQuestions = request.getReponses().size();

        for (ReponseQuestionRequest reponse : request.getReponses()) {
            QuestionQuiz question = questionQuizRepository.findById(reponse.getIdQuestion())
                    .orElseThrow(() -> new NotFoundException("Question non trouvée"));

            if ("QCM".equals(question.getTypeQuestion())) {
                // Vérifier la proposition sélectionnée
                Proposition proposition = propositionRepository.findById(reponse.getIdProposition())
                        .orElseThrow(() -> new NotFoundException("Proposition non trouvée"));
                
                if (proposition.getEstCorrecte()) {
                    score++;
                }
            } else if ("VRAI_FAUX".equals(question.getTypeQuestion())) {
                // Vérifier la réponse vrai/faux
                List<Proposition> propositions = propositionRepository.findByQuestionId(question.getId());
                Proposition bonneReponse = propositions.stream()
                        .filter(Proposition::getEstCorrecte)
                        .findFirst()
                        .orElse(null);
                
                if (bonneReponse != null && bonneReponse.getTexteProposition().equals(reponse.getReponseVraiFaux() ? "Vrai" : "Faux")) {
                    score++;
                }
            }
        }

        // 5. Sauvegarder le résultat
        ResultatQuiz resultat = new ResultatQuiz();
        resultat.setQuiz(quiz);
        resultat.setUtilisateur(utilisateur);
        resultat.setScore(score);
        resultat.setScoreMax(totalQuestions);
        resultat.setDatePassage(LocalDateTime.now());
        resultat = resultatQuizRepository.save(resultat);

        // 6. Le score est déjà sauvegardé dans le résultat de quiz
        // Pas besoin de mettre à jour un score global de l'utilisateur

        return convertToResultatQuizDTO(resultat);
    }

    /**
     * Récupère le score d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return DTO du score
     */
    @Transactional(readOnly = true)
    public ScoreUtilisateurDTO getScoreUtilisateur(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        List<ResultatQuiz> resultats = resultatQuizRepository.findByUtilisateurId(utilisateurId);
        int nombreQuizReussis = resultats.size();
        // Les quiz sont tous traités de la même manière
        // Pas de distinction entre quiz familiaux et publics dans les statistiques
        int nombreQuizFamiliaux = 0;
        int nombreQuizPublics = 0;

        return ScoreUtilisateurDTO.builder()
                .idUtilisateur(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .scoreTotal(0) // Score total non disponible dans l'entité Utilisateur
                .nombreQuizReussis(nombreQuizReussis)
                .nombreQuizFamiliaux(nombreQuizFamiliaux)
                .nombreQuizPublics(nombreQuizPublics)
                .build();
    }

    // Méthodes de conversion
    private QuizDTO convertToQuizDTO(Quiz quiz) {
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

    private ConteDTO convertToConteDTO(Contenu conte) {
        return ConteDTO.builder()
                .id(conte.getId())
                .titre(conte.getTitre())
                .description(conte.getDescription())
                .nomAuteur(conte.getAuteur().getNom())
                .prenomAuteur(conte.getAuteur().getPrenom())
                .emailAuteur(conte.getAuteur().getEmail())
                .dateCreation(conte.getDateCreation())
                .statut(conte.getStatut())
                .urlFichier(conte.getUrlFichier())
                .urlPhoto(conte.getUrlFichier())
                .lieu(conte.getLieu())
                .region(conte.getRegion())
                .idFamille(conte.getFamille().getId())
                .nomFamille(conte.getFamille().getNom())
                .build();
    }

    private ResultatQuizDTO convertToResultatQuizDTO(ResultatQuiz resultat) {
        return ResultatQuizDTO.builder()
                .id(resultat.getId())
                .score(resultat.getScore())
                .totalQuestions(resultat.getScoreMax())
                .datePassage(resultat.getDatePassage())
                .idQuiz(resultat.getQuiz().getId())
                .titreQuiz(resultat.getQuiz().getTitre())
                .idUtilisateur(resultat.getUtilisateur().getId())
                .nomUtilisateur(resultat.getUtilisateur().getNom())
                .prenomUtilisateur(resultat.getUtilisateur().getPrenom())
                .build();
    }
}
