package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.AvaliacaoRiscosService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/avaliacoes-riscos")
public class AvaliacaoRiscosController {

    private final AvaliacaoRiscosService service;

    public AvaliacaoRiscosController(AvaliacaoRiscosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoRiscosResponseDTO> criar(@RequestBody @Valid AvaliacaoRiscosRequestDTO dto) {
        log.info("POST /avaliacoes-riscos | payload={}", dto);
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoRiscosResponseDTO>> listarMinhasAvaliacoes() {
        log.info("GET /avaliacoes-riscos");
        return ResponseEntity.ok(service.listarPorColaborador());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> buscarPorId(@PathVariable String id) {
        log.info("GET /avaliacoes-riscos/{}", id);
        return ResponseEntity.ok(service.buscarPorIdEColaborador(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid AvaliacaoRiscosRequestDTO dto) {

        log.info("PUT /avaliacoes-riscos/{} | payload={}", id, dto);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        log.info("DELETE /avaliacoes-riscos/{}", id);
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
