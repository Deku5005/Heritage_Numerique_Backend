package com.heritage.controller;

import com.heritage.dto.*;
import com.heritage.service.QuizContenuService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des quiz sur les contenus (contes).
 */
@RestController
@RequestMapping("/api/quiz-contenu")
public class QuizContenuController {

    private final QuizContenuService quizContenuService;

    public QuizContenuController(QuizContenuService quizContenuService) {
        this.quizContenuService = quizContenuService;
    }

    /**
     * Crée un quiz pour un contenu (conte) par un membre de famille.
     * 
     * Endpoint : POST /api/quiz-contenu/creer
     * 
     * @param request Requête de création du quiz
     * @param authentication Authentification de l'utilisateur
     * @return DTO du quiz créé
     */
    @PostMapping("/creer")
    public ResponseEntity<QuizDTO> creerQuizContenu(
            @Valid @RequestBody QuizContenuRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        QuizDTO quiz = quizContenuService.creerQuizContenu(request, auteurId);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Crée un quiz pour un contenu public par le super-admin.
     * 
     * Endpoint : POST /api/quiz-contenu/creer-public
     * 
     * @param request Requête de création du quiz
     * @param authentication Authentification de l'utilisateur
     * @return DTO du quiz créé
     */
    @PostMapping("/creer-public")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> creerQuizPublic(
            @Valid @RequestBody QuizContenuRequest request,
            Authentication authentication) {
        
        Long auteurId = AuthenticationHelper.getCurrentUserId();
        QuizDTO quiz = quizContenuService.creerQuizPublic(request, auteurId);
        return ResponseEntity.ok(quiz);
    }

    /**
     * Récupère les contes d'une famille avec leurs quiz éventuels.
     * 
     * Endpoint : GET /api/quiz-contenu/contes/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Liste des contes avec leurs quiz
     */
    @GetMapping("/contes/famille/{familleId}")
    public ResponseEntity<List<ConteDTO>> getContesAvecQuiz(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        List<ConteDTO> contes = quizContenuService.getContesAvecQuiz(familleId);
        return ResponseEntity.ok(contes);
    }

    /**
     * Récupère les contes publics avec leurs quiz éventuels.
     * 
     * Endpoint : GET /api/quiz-contenu/contes/publics
     * 
     * @param authentication Authentification de l'utilisateur
     * @return Liste des contes publics avec leurs quiz
     */
    @GetMapping("/contes/publics")
    public ResponseEntity<List<ConteDTO>> getContesPublicsAvecQuiz(
            Authentication authentication) {
        
        List<ConteDTO> contes = quizContenuService.getContesPublicsAvecQuiz();
        return ResponseEntity.ok(contes);
    }

    /**
     * Répond à un quiz et calcule le score.
     * 
     * Endpoint : POST /api/quiz-contenu/repondre
     * 
     * @param request Requête de réponse au quiz
     * @param authentication Authentification de l'utilisateur
     * @return Résultat du quiz
     */
    @PostMapping("/repondre")
    public ResponseEntity<ResultatQuizDTO> repondreQuiz(
            @Valid @RequestBody ReponseQuizRequest request,
            Authentication authentication) {
        
        Long utilisateurId = AuthenticationHelper.getCurrentUserId();
        ResultatQuizDTO resultat = quizContenuService.repondreQuiz(request, utilisateurId);
        return ResponseEntity.ok(resultat);
    }

    /**
     * Récupère le score d'un utilisateur.
     * 
     * Endpoint : GET /api/quiz-contenu/score
     * 
     * @param authentication Authentification de l'utilisateur
     * @return DTO du score
     */
    @GetMapping("/score")
    public ResponseEntity<ScoreUtilisateurDTO> getScoreUtilisateur(
            Authentication authentication) {
        
        Long utilisateurId = AuthenticationHelper.getCurrentUserId();
        ScoreUtilisateurDTO score = quizContenuService.getScoreUtilisateur(utilisateurId);
        return ResponseEntity.ok(score);
    }
}
