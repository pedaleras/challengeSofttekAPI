package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.AvaliacaoRiscosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/avaliacoes-riscos")
public class AvaliacaoRiscosController {

    @Autowired
    private AvaliacaoRiscosService service;

    @PostMapping
    public ResponseEntity<AvaliacaoRiscosResponseDTO> criar(@RequestBody AvaliacaoRiscosRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoRiscosResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<AvaliacaoRiscosResponseDTO> dto = service.buscarPorId(id);
        return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> atualizar(@PathVariable Long id, @RequestBody AvaliacaoRiscosRequestDTO dto) {
        Optional<AvaliacaoRiscosResponseDTO> atualizado = service.atualizar(id, dto);
        return atualizado.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
