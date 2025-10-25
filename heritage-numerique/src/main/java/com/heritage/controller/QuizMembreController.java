package com.heritage.controller;

import com.heritage.dto.QuizFamilleDTO;
import com.heritage.dto.QuizMembreDTO;
import com.heritage.service.QuizMembreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des quiz des membres de famille.
 * Fournit des informations détaillées sur les quiz et la progression des membres.
 */
@RestController
@RequestMapping("/api/quiz-membre")
public class QuizMembreController {

    private final QuizMembreService quizMembreService;

    public QuizMembreController(QuizMembreService quizMembreService) {
        this.quizMembreService = quizMembreService;
    }

    /**
     * Récupère tous les quiz d'une famille avec les informations de progression de chaque membre.
     * 
     * Endpoint : GET /api/quiz-membre/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec tous les quiz et les informations des membres
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<QuizFamilleDTO> getQuizFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        QuizFamilleDTO quizFamille = quizMembreService.getQuizFamille(familleId);
        return ResponseEntity.ok(quizFamille);
    }

    /**
     * Récupère les quiz d'un membre spécifique avec ses informations de progression.
     * 
     * Endpoint : GET /api/quiz-membre/famille/{familleId}/membre/{membreId}
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return DTO avec les quiz du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<QuizMembreDTO> getQuizMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId,
            Authentication authentication) {
        
        QuizMembreDTO quizMembre = quizMembreService.getQuizMembre(familleId, membreId);
        return ResponseEntity.ok(quizMembre);
    }
}
