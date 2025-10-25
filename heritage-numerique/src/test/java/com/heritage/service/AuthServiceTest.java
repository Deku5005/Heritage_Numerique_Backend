package com.heritage.service;

import com.heritage.dto.AuthResponse;
import com.heritage.dto.LoginRequest;
import com.heritage.dto.RegisterRequest;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le service d'authentification.
 * 
 * @SpringBootTest : Lance le contexte Spring complet
 * @Transactional : Rollback automatique après chaque test
 * @ActiveProfiles("test") : Utilise application-test.properties
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    /**
     * Test d'inscription réussie sans code d'invitation.
     */
    @Test
    public void testRegisterSuccess_WithoutInvitationCode() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean.dupont.test@example.com");
        request.setMotDePasse("MotDePasse123!");
        
        // Act
        AuthResponse response = authService.register(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("jean.dupont.test@example.com", response.getEmail());
        assertEquals("Dupont", response.getNom());
        assertEquals("Jean", response.getPrenom());
        assertEquals("ROLE_MEMBRE", response.getRole());
    }

    /**
     * Test d'inscription avec email déjà existant.
     * Doit lever une BadRequestException.
     */
    @Test
    public void testRegister_DuplicateEmail_ThrowsException() {
        // Arrange - Créer un premier utilisateur
        RegisterRequest firstRequest = new RegisterRequest();
        firstRequest.setNom("Martin");
        firstRequest.setPrenom("Marie");
        firstRequest.setEmail("marie.martin.test@example.com");
        firstRequest.setMotDePasse("Password123!");
        authService.register(firstRequest);
        
        // Act & Assert - Tenter de créer un utilisateur avec le même email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setNom("Durand");
        duplicateRequest.setPrenom("Pierre");
        duplicateRequest.setEmail("marie.martin.test@example.com"); // Même email
        duplicateRequest.setMotDePasse("DifferentPass123!");
        
        assertThrows(BadRequestException.class, () -> {
            authService.register(duplicateRequest);
        });
    }

    /**
     * Test de connexion réussie.
     */
    @Test
    public void testLoginSuccess() {
        // Arrange - Créer un utilisateur d'abord
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNom("Leclerc");
        registerRequest.setPrenom("Sophie");
        registerRequest.setEmail("sophie.leclerc.test@example.com");
        registerRequest.setMotDePasse("SecurePass123!");
        authService.register(registerRequest);
        
        // Act - Se connecter
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("sophie.leclerc.test@example.com");
        loginRequest.setMotDePasse("SecurePass123!");
        
        AuthResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("sophie.leclerc.test@example.com", response.getEmail());
        assertEquals("Leclerc", response.getNom());
    }

    /**
     * Test de connexion avec mot de passe incorrect.
     * Doit lever une UnauthorizedException.
     */
    @Test
    public void testLogin_WrongPassword_ThrowsException() {
        // Arrange - Créer un utilisateur
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNom("Bernard");
        registerRequest.setPrenom("Luc");
        registerRequest.setEmail("luc.bernard.test@example.com");
        registerRequest.setMotDePasse("CorrectPass123!");
        authService.register(registerRequest);
        
        // Act & Assert - Tenter de se connecter avec un mauvais mot de passe
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("luc.bernard.test@example.com");
        loginRequest.setMotDePasse("WrongPassword!");
        
        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginRequest);
        });
    }

    /**
     * Test de connexion avec email inexistant.
     * Doit lever une UnauthorizedException.
     */
    @Test
    public void testLogin_NonExistentEmail_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("inexistant@example.com");
        loginRequest.setMotDePasse("AnyPassword123!");
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginRequest);
        });
    }

    // TODO: Ajouter des tests pour l'inscription avec code d'invitation
    // - Code valide
    // - Code invalide
    // - Code expiré
    // - Email ne correspondant pas au code
}

