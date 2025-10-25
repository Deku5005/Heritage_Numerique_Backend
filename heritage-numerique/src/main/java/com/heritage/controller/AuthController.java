package com.heritage.controller;

import com.heritage.dto.AuthResponse;
import com.heritage.dto.LoginRequest;
import com.heritage.dto.LoginWithCodeRequest;
import com.heritage.dto.RegisterRequest;
import com.heritage.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour l'authentification.
 * 
 * Endpoints :
 * - POST /api/auth/register : Inscription d'un nouvel utilisateur
 * - POST /api/auth/login : Connexion d'un utilisateur existant
 * 
 * Sécurité :
 * - Ces endpoints sont publics (pas d'authentification requise)
 * - Validation des données avec @Valid
 * - Les mots de passe ne sont JAMAIS renvoyés dans les réponses
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // À configurer selon vos besoins en production
@Tag(name = "🔐 Authentification", description = "Endpoints pour l'inscription, la connexion et la gestion des utilisateurs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint d'inscription.
     * 
     * URL : POST /api/auth/register
     * 
     * Body JSON :
     * {
     *   "nom": "Dupont",
     *   "prenom": "Jean",
     *   "email": "jean.dupont@example.com",
     *   "motDePasse": "MonMotDePasse123!",
     *   "codeInvitation": "ABC12345" (optionnel)
     * }
     * 
     * Processus :
     * 1. Valider les données d'entrée (@Valid)
     * 2. Si codeInvitation fourni :
     *    a. Vérifier que le code existe et n'est pas expiré
     *    b. Vérifier que l'email correspond à l'email invité
     *    c. Créer l'utilisateur
     *    d. Lier l'utilisateur à la famille
     *    e. Mettre à jour le statut de l'invitation à ACCEPTEE
     * 3. Sinon : créer simplement l'utilisateur
     * 4. Hasher le mot de passe avec BCrypt
     * 5. Générer un token JWT
     * 6. Retourner le token et les infos utilisateur
     * 
     * Réponse :
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "email": "jean.dupont@example.com",
     *   "nom": "Dupont",
     *   "prenom": "Jean",
     *   "role": "ROLE_MEMBRE"
     * }
     * 
     * Erreurs possibles :
     * - 400 : Email déjà existant, code d'invitation invalide/expiré
     * - 400 : Erreurs de validation (email invalide, mot de passe trop court, etc.)
     * 
     * @param request Requête d'inscription
     * @return Réponse avec token JWT
     */
    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = """
                    Inscription d'un nouvel utilisateur dans l'application.
                    
                    **Avec code d'invitation :**
                    - L'utilisateur devient automatiquement membre de la famille
                    - L'invitation passe au statut ACCEPTEE
                    
                    **Sans code d'invitation :**
                    - L'utilisateur est créé mais n'appartient à aucune famille
                    - Il peut être invité par une famille plus tard
                    """,
            tags = {"🔐 Authentification"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Inscription réussie",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                              "tokenType": "Bearer",
                                              "userId": 1,
                                              "email": "jean.dupont@example.com",
                                              "nom": "Dupont",
                                              "prenom": "Jean",
                                              "role": "ROLE_MEMBRE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur de validation ou email déjà existant",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Email déjà existant",
                                    value = """
                                            {
                                              "message": "Un utilisateur avec cet email existe déjà",
                                              "timestamp": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "Données d'inscription de l'utilisateur", required = true)
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint de connexion.
     * 
     * URL : POST /api/auth/login
     * 
     * Body JSON :
     * {
     *   "email": "jean.dupont@example.com",
     *   "motDePasse": "MonMotDePasse123!"
     * }
     * 
     * Processus :
     * 1. Valider les données d'entrée (@Valid)
     * 2. Authentifier avec email/mot de passe
     * 3. Générer un nouveau token JWT
     * 4. Retourner le token et les infos utilisateur
     * 
     * Réponse :
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "email": "jean.dupont@example.com",
     *   "nom": "Dupont",
     *   "prenom": "Jean",
     *   "role": "ROLE_MEMBRE"
     * }
     * 
     * Erreurs possibles :
     * - 401 : Email ou mot de passe incorrect
     * - 400 : Erreurs de validation
     * 
     * Sécurité :
     * - Le message d'erreur ne révèle pas si l'email existe ou non (protection contre l'énumération)
     * - Le mot de passe est vérifié avec BCrypt
     * - Le mot de passe n'est JAMAIS renvoyé dans la réponse
     * 
     * @param request Requête de connexion
     * @return Réponse avec token JWT
     */
    @Operation(
            summary = "Connexion d'un utilisateur existant",
            description = """
                    Connexion d'un utilisateur existant avec son email et mot de passe.
                    
                    **Processus :**
                    1. Validation des données d'entrée
                    2. Authentification avec email/mot de passe
                    3. Génération d'un nouveau token JWT
                    4. Retour du token et des informations utilisateur
                    
                    **Sécurité :**
                    - Le message d'erreur ne révèle pas si l'email existe ou non
                    - Le mot de passe est vérifié avec BCrypt
                    - Le mot de passe n'est jamais renvoyé dans la réponse
                    """,
            tags = {"🔐 Authentification"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Connexion réussie",
                                    value = """
                                            {
                                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                              "tokenType": "Bearer",
                                              "userId": 1,
                                              "email": "jean.dupont@example.com",
                                              "nom": "Dupont",
                                              "prenom": "Jean",
                                              "role": "ROLE_MEMBRE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Email ou mot de passe incorrect",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Identifiants incorrects",
                                    value = """
                                            {
                                              "message": "Email ou mot de passe incorrect",
                                              "timestamp": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Données de connexion de l'utilisateur", required = true)
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Connexion avec code d'invitation pour utilisateur existant.
     * 
     * Endpoint : POST /api/auth/login-with-code
     * 
     * Utilisé quand un utilisateur existant reçoit une invitation par email
     * et veut rejoindre une famille en utilisant son compte existant.
     * 
     * Processus :
     * 1. Authentifier l'utilisateur avec email/mot de passe
     * 2. Valider le code d'invitation
     * 3. Créer le lien membre_famille
     * 4. Mettre à jour le statut de l'invitation à ACCEPTEE
     * 5. Générer un token JWT
     * 
     * @param request Requête avec email, mot de passe et code d'invitation
     * @return Réponse avec token JWT
     */
    @PostMapping("/login-with-code")
    public ResponseEntity<AuthResponse> loginWithCode(@Valid @RequestBody LoginWithCodeRequest request) {
        AuthResponse response = authService.loginWithCode(request);
        return ResponseEntity.ok(response);
    }
}

