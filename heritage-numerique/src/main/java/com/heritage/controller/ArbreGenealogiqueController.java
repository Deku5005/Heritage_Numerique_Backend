package com.heritage.controller;

import com.heritage.dto.AjoutMembreArbreRequest;
import com.heritage.dto.ArbreGenealogiqueDTO;
import com.heritage.dto.MembreArbreDTO;
import com.heritage.service.ArbreGenealogiqueService;
import com.heritage.util.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion de l'arbre généalogique de famille.
 * Chaque famille a un seul arbre généalogique.
 */
@RestController
@RequestMapping("/api/arbre-genealogique")
public class ArbreGenealogiqueController {

    private final ArbreGenealogiqueService arbreGenealogiqueService;

    public ArbreGenealogiqueController(ArbreGenealogiqueService arbreGenealogiqueService) {
        this.arbreGenealogiqueService = arbreGenealogiqueService;
    }

    /**
     * Récupère l'arbre généalogique d'une famille avec tous ses membres.
     * 
     * Endpoint : GET /api/arbre-genealogique/famille/{familleId}
     * 
     * @param familleId ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return Arbre généalogique complet
     */
    @GetMapping("/famille/{familleId}")
    public ResponseEntity<ArbreGenealogiqueDTO> getArbreByFamille(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        ArbreGenealogiqueDTO arbre = arbreGenealogiqueService.getArbreByFamille(familleId);
        return ResponseEntity.ok(arbre);
    }

    /**
     * Ajoute un membre à l'arbre généalogique d'une famille.
     * 
     * Endpoint : POST /api/arbre-genealogique/ajouter-membre
     * 
     * @param nomComplet Nom complet du membre
     * @param dateNaissance Date de naissance
     * @param lieuNaissance Lieu de naissance
     * @param relationFamiliale Relation familiale
     * @param photo Fichier photo (optionnel)
     * @param telephone Numéro de téléphone
     * @param email Adresse email
     * @param biographie Biographie du membre
     * @param parent1Id ID du premier parent
     * @param parent2Id ID du deuxième parent
     * @param idFamille ID de la famille
     * @param authentication Authentification de l'utilisateur
     * @return DTO du membre ajouté
     */
    @PostMapping(value = "/ajouter-membre", consumes = "multipart/form-data")
    public ResponseEntity<MembreArbreDTO> ajouterMembreArbre(
            @RequestParam String nomComplet,
            @RequestParam String dateNaissance,
            @RequestParam String lieuNaissance,
            @RequestParam String relationFamiliale,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile photo,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String biographie,
            @RequestParam(required = false) Long parent1Id,
            @RequestParam(required = false) Long parent2Id,
            @RequestParam Long idFamille,
            Authentication authentication) {
        
        try {
            // Créer la requête à partir des paramètres
            AjoutMembreArbreRequest request = AjoutMembreArbreRequest.builder()
                    .nomComplet(nomComplet)
                    .dateNaissance(dateNaissance) // Passer la chaîne directement
                    .lieuNaissance(lieuNaissance)
                    .relationFamiliale(relationFamiliale)
                    .photo(photo)
                    .telephone(telephone)
                    .email(email)
                    .biographie(biographie)
                    .parent1Id(parent1Id)
                    .parent2Id(parent2Id)
                    .idFamille(idFamille)
                    .build();
            
            Long auteurId = AuthenticationHelper.getCurrentUserId();
            MembreArbreDTO membre = arbreGenealogiqueService.ajouterMembreArbre(request, auteurId);
            return ResponseEntity.ok(membre);
        } catch (Exception e) {
            // Retourner une réponse d'erreur au lieu de laisser l'exception remonter
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Récupère un membre spécifique de l'arbre généalogique.
     * 
     * Endpoint : GET /api/arbre-genealogique/membre/{membreId}
     * 
     * @param membreId ID du membre
     * @param authentication Authentification de l'utilisateur
     * @return DTO du membre
     */
    @GetMapping("/membre/{membreId}")
    public ResponseEntity<MembreArbreDTO> getMembreArbreById(
            @PathVariable Long membreId,
            Authentication authentication) {
        
        MembreArbreDTO membre = arbreGenealogiqueService.getMembreArbreById(membreId);
        return ResponseEntity.ok(membre);
    }

    /**
     * Récupère tous les membres de l'arbre généalogique liés à un membre spécifique.
     * Cette méthode retourne récursivement tous les membres liés :
     * - Descendants (enfants, petits-enfants, etc.)
     * - Ascendants (parents, grands-parents, etc.)
     * - Frères et sœurs
     * 
     * Endpoint : GET /api/arbre-genealogique/membre/{membreId}/membres-lies
     * 
     * @param membreId ID du membre de référence
     * @param authentication Authentification de l'utilisateur
     * @return Liste de tous les membres liés
     */
    @GetMapping("/membre/{membreId}/membres-lies")
    public ResponseEntity<List<MembreArbreDTO>> getTousMembresLies(
            @PathVariable Long membreId,
            Authentication authentication) {
        
        List<MembreArbreDTO> membresLies = arbreGenealogiqueService.getTousMembresLies(membreId);
        return ResponseEntity.ok(membresLies);
    }

    /**
     * Endpoint de test pour vérifier les permissions.
     * 
     * Endpoint : GET /api/arbre-genealogique/test-permissions/{familleId}
     */
    @GetMapping("/test-permissions/{familleId}")
    public ResponseEntity<String> testPermissions(
            @PathVariable Long familleId,
            Authentication authentication) {
        
        try {
            Long auteurId = AuthenticationHelper.getCurrentUserId();
            
            // Vérifier si l'utilisateur est membre de la famille
            var membreFamille = arbreGenealogiqueService.getMembreFamille(auteurId, familleId);
            if (membreFamille == null) {
                return ResponseEntity.ok("User ID: " + auteurId + ", Famille ID: " + familleId + " - NOT MEMBER");
            }
            
            return ResponseEntity.ok("User ID: " + auteurId + ", Famille ID: " + familleId + 
                    ", Role: " + membreFamille.getRoleFamille() + 
                    ", Can Write: " + membreFamille.getRoleFamille().canWrite());
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint de test pour créer un membre simple.
     * 
     * Endpoint : POST /api/arbre-genealogique/test-ajouter
     */
    @PostMapping("/test-ajouter")
    public ResponseEntity<String> testAjouterMembre(
            @RequestParam String nomComplet,
            @RequestParam String dateNaissance,
            @RequestParam String lieuNaissance,
            @RequestParam String relationFamiliale,
            @RequestParam Long idFamille,
            Authentication authentication) {
        
        try {
            Long auteurId = AuthenticationHelper.getCurrentUserId();
            
            // Créer la requête à partir des paramètres
            AjoutMembreArbreRequest request = AjoutMembreArbreRequest.builder()
                    .nomComplet(nomComplet)
                    .dateNaissance(dateNaissance) // Passer la chaîne directement
                    .lieuNaissance(lieuNaissance)
                    .relationFamiliale(relationFamiliale)
                    .photo(null)
                    .telephone(null)
                    .email(null)
                    .biographie(null)
                    .parent1Id(null)
                    .parent2Id(null)
                    .idFamille(idFamille)
                    .build();
            
            MembreArbreDTO membre = arbreGenealogiqueService.ajouterMembreArbre(request, auteurId);
            return ResponseEntity.ok("Success: Member created with ID " + membre.getId());
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }
}