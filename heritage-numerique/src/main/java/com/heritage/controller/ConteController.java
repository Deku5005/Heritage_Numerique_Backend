package com.heritage.controller;

import com.heritage.dto.ConteDTO;
import com.heritage.dto.ConteRequest;
import com.heritage.entite.Contenu;
import com.heritage.service.ConteService;
import com.heritage.service.ContenuCreationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Garder l'import
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Contrôleur pour la gestion des contes de famille.
 * Fournit des informations détaillées sur les contes créés par les membres.
 */
@RestController
@RequestMapping("/api/contes")
public class ConteController {

    private final ConteService conteService;
    private final ContenuCreationService contenuCreationService;

    public ConteController(
            ConteService conteService,
            ContenuCreationService contenuCreationService) {
        this.conteService = conteService;
        this.contenuCreationService = contenuCreationService;
    }

    /**
     * Crée un nouveau conte et gère l'upload de la photo associée.
     * * Endpoint : POST /api/contes
     * Content-Type requis pour Postman/Front-end: 'multipart/form-data'
     * * @param request Requête de création de conte (y compris fichiers et ID famille)
     * @param authentication Authentification de l'utilisateur
     * @return Le Contenu Entité créé
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Contenu> creerConte(
            @Valid @ModelAttribute ConteRequest request,
            Authentication authentication) {

        try {
            // 🚨 AMÉLIORATION : Récupérer l'ID de l'utilisateur directement du principal
            // On suppose que le principal (getPrincipal()) est un objet qui contient l'ID utilisateur (ex: UserDetails avec un champ custom ou un ID Long)
            // Si le Principal est un Long (L'ID de l'utilisateur) :
            if (authentication.getPrincipal() instanceof Long) {
                Long auteurId = (Long) authentication.getPrincipal();

                // B. Appel au service métier pour créer/sauvegarder le conte
                Contenu createdConte = contenuCreationService.creerConte(request, auteurId);

                // Retourne le DTO créé
                return ResponseEntity.ok(createdConte);
            } else {
                // Gestion si le Principal n'est pas le format attendu (ex: String username)
                // Dans un cas réel, vous feriez une recherche DB ici ou utiliseriez un UserDetails custom.
                // Pour l'instant, on lance une erreur si l'ID n'est pas Long
                throw new IllegalStateException("Le format du principal d'authentification n'est pas un Long ID.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du conte: " + e.getMessage());
            e.printStackTrace();
            // Utiliser un DTO d'erreur si l'application est complexe
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * Récupère tous les contes d'une famille avec leurs informations détaillées.
     * * Endpoint : GET /api/contes/famille/{familleId}
     * * @param familleId ID de la famille
     * @return Liste des contes avec informations des auteurs
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<List<ConteDTO>> getContesByFamille(
            @PathVariable Long familleId) {

        List<ConteDTO> contes = conteService.getContesByFamille(familleId);
        return ResponseEntity.ok(contes);
    }

    /**
     * Récupère un conte spécifique par son ID.
     * * Endpoint : GET /api/contes/{conteId}
     * * @param conteId ID du conte
     * @return DTO du conte
     */
    @GetMapping("/{conteId}")
    public ResponseEntity<ConteDTO> getConteById(
            @PathVariable Long conteId) {

        ConteDTO conte = conteService.getConteById(conteId);
        return ResponseEntity.ok(conte);
    }

    /**
     * Récupère les contes d'un membre spécifique dans une famille.
     * * Endpoint : GET /api/contes/famille/{familleId}/membre/{membreId}
     * * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return Liste des contes du membre
     */
    @GetMapping("/famille/{familleId}/membre/{membreId}")
    public ResponseEntity<List<ConteDTO>> getContesByMembre(
            @PathVariable Long familleId,
            @PathVariable Long membreId) {

        List<ConteDTO> contes = conteService.getContesByMembre(familleId, membreId);
        return ResponseEntity.ok(contes);
    }
}
