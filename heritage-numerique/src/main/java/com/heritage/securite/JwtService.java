package com.heritage.securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service de gestion des tokens JWT.
 * 
 * Responsabilités :
 * - Génération de tokens JWT avec claims personnalisés
 * - Validation et parsing des tokens
 * - Extraction des informations (email, expiration, etc.)
 * 
 * Sécurité :
 * - Utilise l'algorithme HS256 (HMAC with SHA-256) pour signer les tokens
 * - La clé secrète est stockée dans JwtProperties (à charger depuis variable d'environnement en production)
 * - Les tokens ont une durée de vie limitée (configurable)
 * - Validation stricte de l'expiration et de la signature
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Génère un token JWT pour un utilisateur.
     * 
     * @param userDetails Détails de l'utilisateur
     * @param userId ID de l'utilisateur (ajouté dans les claims)
     * @param role Rôle de l'utilisateur
     * @return Token JWT signé
     */
    public String generateToken(UserDetails userDetails, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("email", userDetails.getUsername());
        
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crée un token JWT avec les claims fournis.
     * 
     * @param claims Claims à inclure dans le token
     * @param subject Sujet du token (généralement l'email)
     * @return Token JWT signé
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur (email) du token.
     * 
     * @param token Token JWT
     * @return Email de l'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait l'ID utilisateur du token.
     * 
     * @param token Token JWT
     * @return ID de l'utilisateur
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extrait le rôle du token.
     * 
     * @param token Token JWT
     * @return Rôle de l'utilisateur
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extrait la date d'expiration du token.
     * 
     * @param token Token JWT
     * @return Date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token.
     * 
     * @param token Token JWT
     * @param claimsResolver Fonction pour extraire le claim
     * @param <T> Type du claim
     * @return Valeur du claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token.
     * Parse et valide le token (signature et expiration).
     * 
     * @param token Token JWT
     * @return Claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token est expiré.
     * 
     * @param token Token JWT
     * @return true si expiré
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide un token JWT.
     * Vérifie que :
     * - Le token n'est pas expiré
     * - Le nom d'utilisateur correspond
     * 
     * @param token Token JWT
     * @param userDetails Détails de l'utilisateur
     * @return true si le token est valide
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Obtient la clé de signature pour JWT.
     * Convertit la clé secrète en format approprié pour l'algorithme HMAC.
     * 
     * Sécurité : La clé secrète doit être suffisamment longue (au moins 256 bits).
     * 
     * @return Clé de signature
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

