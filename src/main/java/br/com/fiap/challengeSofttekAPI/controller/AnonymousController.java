package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.security.JwtService;
import lombok.extern.slf4j.Slf4j; // Importação adicionada para usar a anotação @Slf4j do Lombok
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AnonymousController {

    private final JwtService jwtService;

    public AnonymousController(JwtService jwtService) {
        this.jwtService = jwtService;
        // O construtor é executado na inicialização do Spring.
        // É bom logar que o controller está sendo inicializado.
        log.info("AnonymousController inicializado.");
    }

    @PostMapping("/anonymous")
    public ResponseEntity<Map<String, String>> getAnonymousToken() {
        // Nível INFO para registrar que uma requisição para token anônimo foi recebida.
        log.info("Recebida requisição para gerar token de usuário anônimo.");

        String anonymousUserId = UUID.randomUUID().toString(); // Gera um ID único para o usuário anônimo
        // Nível DEBUG para logar o ID gerado (útil em desenvolvimento, mas não expõe dados sensíveis).
        log.debug("Gerado novo anonymousUserId: {}", anonymousUserId);

        String token = jwtService.generateAnonymousToken(anonymousUserId);
        // Nível INFO para registrar que o token foi gerado com sucesso.
        log.info("Token JWT anônimo gerado com sucesso para o usuário {}.", anonymousUserId);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("anonymousUserId", anonymousUserId); // Opcional: retorna o ID também

        // Nível INFO para indicar o sucesso da operação e o envio da resposta.
        log.info("Resposta com token anônimo enviada para o usuário {}.", anonymousUserId);
        return ResponseEntity.ok(response);
    }
}