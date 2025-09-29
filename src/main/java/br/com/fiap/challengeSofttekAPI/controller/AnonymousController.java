package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AnonymousController {

    private final JwtService jwtService;

    public AnonymousController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/anonymous")
    public ResponseEntity<Map<String, String>> getAnonymousToken() {
        String anonymousUserId = UUID.randomUUID().toString(); // Gera um ID único para o usuário anônimo
        String token = jwtService.generateAnonymousToken(anonymousUserId);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("anonymousUserId", anonymousUserId); // Opcional: retorna o ID também
        return ResponseEntity.ok(response);
    }
}