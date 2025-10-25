package com.heritage.securite;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriétés de configuration pour JWT
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secret = "heritage-numerique-secret-key-2024";
    private int expiration = 3600000; // 1 heure en millisecondes
    private String issuer = "heritage-numerique";
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public int getExpiration() {
        return expiration;
    }
    
    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}