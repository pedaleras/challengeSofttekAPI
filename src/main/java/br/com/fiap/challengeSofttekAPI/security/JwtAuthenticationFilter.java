package br.com.fiap.challengeSofttekAPI.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    // Adicionado construtor para logar a inicialização do filtro
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        log.info("JwtAuthenticationFilter inicializado.");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI(); // Captura a URI da requisição para logs
        log.debug("Processando requisição para URI: {}", requestURI);

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String anonymousUserId = null;

        // 1. Verifica a presença e o formato do cabeçalho Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Requisição para URI: {} - Authorization header ausente ou malformado. Header recebido: {}.", requestURI, authHeader);
            // Continua a cadeia de filtros, permitindo que permitAll ou outros filtros atuem.
            filterChain.doFilter(request, response);
            return; // Encerra o processamento do filtro JWT para esta requisição
        }

        jwt = authHeader.substring(7); // Extrai o token JWT (após "Bearer ")
        log.debug("Requisição para URI: {} - JWT extraído: {}.", requestURI, jwt);

        try {
            // 2. Extrai o ID do usuário anônimo do token
            anonymousUserId = jwtService.extractAnonymousUserId(jwt);

            // 3. Verifica se o ID foi extraído e se já não há autenticação no contexto
            if (anonymousUserId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 4. Valida o token
                if (jwtService.validateToken(jwt)) {
                    // *** ALTERAÇÃO AQUI: Use AnonymousUserDetails como principal ***
                    AnonymousUserDetails userDetails = new AnonymousUserDetails(anonymousUserId);
                    log.debug("Requisição para URI: {} - JWT válido. Criado UserDetails para ID: {}.", requestURI, anonymousUserId);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // Agora passa um objeto UserDetails
                            null,
                            userDetails.getAuthorities() // As authorities vêm do UserDetails
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Requisição para URI: {} - Usuário autenticado no SecurityContextHolder com ID: {}.", requestURI, anonymousUserId);
                } else {
                    log.warn("Requisição para URI: {} - Token JWT inválido. ID extraído: {}.", requestURI, anonymousUserId);
                }
            } else if (anonymousUserId == null) {
                log.warn("Requisição para URI: {} - Não foi possível extrair anonymousUserId do token JWT: {}.", requestURI, jwt);
            } else {
                log.info("Requisição para URI: {} - SecurityContextHolder já contém autenticação para usuário {}. Ignorando processamento JWT.", requestURI, SecurityContextHolder.getContext().getAuthentication().getName());
            }
        } catch (Exception e) {
            // 5. Captura e loga quaisquer exceções durante o processamento do JWT
            log.error("Requisição para URI: {} - Erro ao processar JWT. Limpando contexto de segurança. Detalhes: {}", requestURI, e.getMessage(), e);
            SecurityContextHolder.clearContext(); // Garante que nenhum estado de autenticação inválido permaneça
        }

        // 6. Loga o estado final da autenticação antes de prosseguir com a cadeia de filtros
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Requisição para URI: {} prosseguindo SEM autenticação no SecurityContextHolder.", requestURI);
        } else {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                log.info("Requisição para URI: {} prosseguindo COM autenticação para usuário: {}.", requestURI, ((UserDetails) principal).getUsername());
            } else {
                log.warn("Requisição para URI: {} prosseguindo COM autenticação, mas principal não é UserDetails. Tipo: {}.", requestURI, principal.getClass().getName());
            }
        }

        filterChain.doFilter(request, response);
    }
}