package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.ApoioUtilizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/apoios")
public class ApoioUtilizadoController {

    @Autowired
    private ApoioUtilizadoService service;

    @PostMapping
    public ResponseEntity<ApoioUtilizadoResponseDTO> criar(
            @RequestBody ApoioUtilizadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId
        return ResponseEntity.ok(service.salvar(colaboradorId, dto)); // Passa o colaboradorId para o serviço
    }

    @GetMapping
    public ResponseEntity<List<ApoioUtilizadoResponseDTO>> listarMeusApoios(
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId
        // Este método agora deve listar apenas os apoios do colaborador autenticado
        return ResponseEntity.ok(service.listarPorColaborador(colaboradorId));
    }

    @GetMapping("/{id}")
    public ApoioUtilizadoResponseDTO buscarPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { //  verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço deve verificar se o ID pertence ao colaborador auenticado
        return service.buscarPorIdEColaborador(id, colaboradorId);
    }

    @PutMapping("/{id}")
    public ApoioUtilizadoResponseDTO atualizar(
            @PathVariable String id,
            @RequestBody ApoioUtilizadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço deve verificar se o ID pertence ao colaborador autenticado antes de atualizar
        return service.atualizar(id, colaboradorId, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Adicionado: para verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço deve verificar se o ID pertence ao colaborador autenticado antes de deletar
        service.deletar(id, colaboradorId);
        return ResponseEntity.noContent().build();
    }
}