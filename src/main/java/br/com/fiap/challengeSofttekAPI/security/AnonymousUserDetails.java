package br.com.fiap.challengeSofttekAPI.security; // ou br.com.fiap.challengeSofttekAPI.security.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AnonymousUserDetails implements UserDetails {

    private final String anonymousUserId;
    private final List<SimpleGrantedAuthority> authorities;

    public AnonymousUserDetails(String anonymousUserId) {
        this.anonymousUserId = anonymousUserId;
        // A role 'ROLE_ANONYMOUS' é definida aqui
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        // Log de INFO para indicar a criação de um AnonymousUserDetails.
        // Útil para rastrear quando um usuário anônimo é autenticado no sistema.
        log.info("Novo AnonymousUserDetails criado para o ID: {}.", anonymousUserId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // Usuário anônimo não tem senha
    }

    @Override
    public String getUsername() {
        return anonymousUserId; // Retorna o ID anônimo como nome de usuário
    }

    // Métodos para o estado da conta - para um usuário anônimo, geralmente são sempre true
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
        return true;
    }
}