package com.heritage.securite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Classe représentant le principal (utilisateur authentifié) dans Spring Security.
 * Implémente UserDetails pour être compatible avec Spring Security.
 * 
 * Cette classe encapsule les informations de l'utilisateur connecté,
 * notamment son ID, qui peut être facilement récupéré dans les controllers.
 * 
 * Avantage par rapport à UserDetails standard :
 * - Accès direct à l'ID utilisateur (pas besoin de requête en BDD)
 * - Information disponible dans tout le contexte de sécurité
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String role;
    private final boolean actif;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return actif;
    }

    /**
     * Récupère l'ID de l'utilisateur.
     * Cette méthode permet d'accéder facilement à l'ID dans les controllers.
     * 
     * @return ID de l'utilisateur
     */
    public Long getId() {
        return id;
    }
}

