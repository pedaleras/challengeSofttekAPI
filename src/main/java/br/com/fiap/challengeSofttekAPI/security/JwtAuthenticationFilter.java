package br.com.fiap.challengeSofttekAPI.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtService.validateToken(token)) {
                    String anonymousUserId = jwtService.extractAnonymousUserId(token);
                    if (anonymousUserId != null) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        new AnonymousUserDetails(anonymousUserId),
                                        null,
                                        new AnonymousUserDetails(anonymousUserId).getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.info("Usuário autenticado: {} - URI: {}", anonymousUserId, request.getRequestURI());
                    } else {
                        logger.warn("Token válido mas sem anonymousUserId - URI: {}", request.getRequestURI());
                    }
                } else {
                    logger.warn("Token inválido ou expirado - URI: {}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                logger.error("Erro ao processar JWT na URI {}: {}", request.getRequestURI(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("Requisição sem token - URI: {}", request.getRequestURI());
        }

        // Sempre prossegue, mesmo sem token
        filterChain.doFilter(request, response);
    }
}
