package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.AvaliacaoRiscosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/avaliacoes-riscos")
public class AvaliacaoRiscosController {

    @Autowired
    private AvaliacaoRiscosService service;

    @PostMapping
    public ResponseEntity<AvaliacaoRiscosResponseDTO> criar(
            @RequestBody @Valid AvaliacaoRiscosRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId
        return ResponseEntity.ok(service.salvar(colaboradorId, dto)); // Passa o colaboradorId para o serviço
    }

    // Método alterado de listarTodos para listarMinhasAvaliacoes
    @GetMapping
    public ResponseEntity<List<AvaliacaoRiscosResponseDTO>> listarMinhasAvaliacoes(
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId
        // Este método agora deve listar apenas as avaliações do colaborador autenticado
        return ResponseEntity.ok(service.listarPorColaborador(colaboradorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> buscarPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // para verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        AvaliacaoRiscosResponseDTO dto = service.buscarPorIdEColaborador(id, colaboradorId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid AvaliacaoRiscosRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // para verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        AvaliacaoRiscosResponseDTO atualizado = service.atualizar(id, colaboradorId, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // verificar posse
        String colaboradorId = currentUser.getUsername();
        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        service.deletar(id, colaboradorId);
        return ResponseEntity.noContent().build();
    }
}