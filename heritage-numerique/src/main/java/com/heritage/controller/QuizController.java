package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.service.QuizService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des quiz.
 * 
 * Endpoints :
 * - POST /api/quiz : créer un quiz pour une famille
 * - POST /api/quiz/public/{contenuId} : créer un quiz pour un contenu public
 * - GET /api/quiz/{id} : récupérer un quiz
 * - GET /api/quiz/famille/{familleId} : récupérer les quiz d'une famille
 */
@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Crée un quiz pour une famille.
     * Les membres EDITEUR et ADMIN peuvent créer des quiz.
     * 
     * @param request Requête de création
     * @param authentication Authentification
     * @return Quiz créé
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<QuizDTO> createQuiz(
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        
        Long createurId = getUserIdFromAuth(authentication);
        QuizDTO quiz = quizService.createQuiz(request, createurId);
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    /**
     * Crée un quiz pour un contenu public.
     * Seul le super-admin ou l'admin de la famille peut créer.
     * 
     * @param contenuId ID du contenu public
     * @param request Requête de création
     * @param authentication Authentification
     * @return Quiz créé
     */
    @PostMapping("/public/{contenuId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<QuizDTO> createQuizForPublicContenu(
            @PathVariable Long contenuId,
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        
        Long createurId = getUserIdFromAuth(authentication);
        QuizDTO quiz = quizService.createQuizForPublicContenu(contenuId, request, createurId);
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    /**
     * Récupère un quiz par son ID.
     * 
     * @param id ID du quiz
     * @return Quiz
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable Long id) {
        QuizDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Récupère tous les quiz actifs d'une famille.
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification
     * @return Liste des quiz
     */
    @GetMapping("/famille/{familleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<List<QuizDTO>> getQuizFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        List<QuizDTO> quiz = quizService.getQuizFamille(familleId, utilisateurId);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Crée une question pour un quiz.
     * Seuls les créateurs du quiz ou les ADMIN de la famille peuvent ajouter des questions.
     * 
     * @param request Requête de création de question
     * @param authentication Authentification
     * @return Question créée
     */
    @PostMapping("/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        
        Long createurId = getUserIdFromAuth(authentication);
        Object question = quizService.createQuestion(request, createurId);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    /**
     * Crée une proposition pour une question de quiz.
     * Seuls les créateurs du quiz ou les ADMIN de la famille peuvent ajouter des propositions.
     * 
     * @param request Requête de création de proposition
     * @param authentication Authentification
     * @return Proposition créée
     */
    @PostMapping("/questions/{questionId}/propositions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> createProposition(
            @PathVariable Long questionId,
            @Valid @RequestBody PropositionRequest request,
            Authentication authentication) {
        
        Long createurId = getUserIdFromAuth(authentication);
        Object proposition = quizService.createProposition(request, questionId, createurId);
        return new ResponseEntity<>(proposition, HttpStatus.CREATED);
    }

    /**
     * Répond à un quiz.
     * 
     * @param request Requête de réponse au quiz
     * @param authentication Authentification
     * @return Résultat du quiz
     */
    @PostMapping("/repondre")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> repondreQuiz(
            @Valid @RequestBody ReponseQuizRequest request,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        Object resultat = quizService.repondreQuiz(request, utilisateurId);
        return new ResponseEntity<>(resultat, HttpStatus.CREATED);
    }

    /**
     * Récupère les questions d'un quiz.
     * 
     * @param quizId ID du quiz
     * @param authentication Authentification
     * @return Liste des questions
     */
    @GetMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> getQuestionsQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        Object questions = quizService.getQuestionsQuiz(quizId, utilisateurId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Récupère les résultats d'un quiz.
     * 
     * @param quizId ID du quiz
     * @param authentication Authentification
     * @return Liste des résultats
     */
    @GetMapping("/{quizId}/resultats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRE')")
    public ResponseEntity<Object> getResultatsQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {
        
        Long utilisateurId = getUserIdFromAuth(authentication);
        Object resultats = quizService.getResultatsQuiz(quizId, utilisateurId);
        return ResponseEntity.ok(resultats);
    }

    /**
     * Récupère l'ID de l'utilisateur depuis l'authentification.
     * 
     * @param authentication Authentification Spring Security
     * @return ID de l'utilisateur
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return AuthenticationHelper.getCurrentUserId();
    }
}

