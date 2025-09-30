package br.com.fiap.challengeSofttekAPI.config;

import br.com.fiap.challengeSofttekAPI.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        // Log de INFO para indicar que a configuração de segurança está sendo inicializada.
        log.info("SecurityConfig inicializado.");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando SecurityFilterChain...");

        http
                .csrf(csrf -> {
                    csrf.disable();
                    // Log de INFO para indicar que a proteção CSRF foi desabilitada.
                    log.info("Proteção CSRF desabilitada.");
                })
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/api/auth/anonymous").permitAll()
                            // Log de INFO para a rota pública.
                            .anyRequest().hasRole("ANONYMOUS");
                    // Log de INFO para as rotas protegidas.
                    log.info("Rota '/api/auth/anonymous' configurada para 'permitAll'.");
                    log.info("Todas as outras requisições exigem a role 'ANONYMOUS'.");
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Define que a sessão será sem estado (essencial para JWT)
                    // Log de INFO para o tipo de gerenciamento de sessão.
                    log.info("Política de criação de sessão definida como STATELESS (para JWT).");
                })
                // Adiciona o filtro JWT antes do filtro padrão de autenticação de usuário/senha
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Log de INFO para a adição do filtro JWT.
        log.info("JwtAuthenticationFilter adicionado antes de UsernamePasswordAuthenticationFilter.");

        log.info("SecurityFilterChain configurada com sucesso.");
        return http.build();
    }
}