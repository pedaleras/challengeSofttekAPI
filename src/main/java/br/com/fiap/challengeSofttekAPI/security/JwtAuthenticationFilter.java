package br.com.fiap.challengeSofttekAPI.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String anonymousUserId = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Requisição para URI: {} - Authorization header ausente ou malformado. Header recebido: {}");
        } else {
            jwt = authHeader.substring(7);
            try {
                anonymousUserId = jwtService.extractAnonymousUserId(jwt);

                if (anonymousUserId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.validateToken(jwt)) {
                        // *** ALTERAÇÃO AQUI: Use AnonymousUserDetails como principal ***
                        AnonymousUserDetails userDetails = new AnonymousUserDetails(anonymousUserId);

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, // Agora passa um objeto UserDetails
                                null,
                                userDetails.getAuthorities() // As authorities vêm do UserDetails
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.info("Requisição para URI: {} - Usuário autenticado no SecurityContextHolder: {}");
                    } else {
                        logger.warn("Requisição para URI: {} - Token JWT inválido. ID extraído: {}");
                    }
                } else if (anonymousUserId == null) {
                    logger.warn("Requisição para URI: {} - Não foi possível extrair anonymousUserId do token.");
                } else {
                    logger.info("Requisição para URI: {} - SecurityContextHolder já contém autenticação (ignorado).");
                }
            } catch (Exception e) {
                logger.error("Requisição para URI: {} - Erro ao processar JWT. Detalhes: {}");
                SecurityContextHolder.clearContext();
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Requisição para URI: {} prosseguindo SEM autenticação NO SecurityContextHolder.");
        } else {
            // Ajuste aqui para pegar o username corretamente do principal, que agora é UserDetails
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                logger.info("Requisição para URI: {} prosseguindo COM autenticação para usuário: {}"
                );
            } else {
                logger.info("Requisição para URI: {} prosseguindo COM autenticação, mas principal não é UserDetails.");
            }
        }

        filterChain.doFilter(request, response);
    }
}