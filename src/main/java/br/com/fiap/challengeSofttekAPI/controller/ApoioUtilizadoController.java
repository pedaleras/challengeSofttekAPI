package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.ApoioUtilizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/apoios")
public class ApoioUtilizadoController {

    @Autowired
    private ApoioUtilizadoService service;

    @PostMapping
    public ResponseEntity<ApoioUtilizadoResponseDTO> criar(@RequestBody ApoioUtilizadoRequestDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ApoioUtilizadoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ApoioUtilizadoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ApoioUtilizadoResponseDTO atualizar(@PathVariable Long id, @RequestBody ApoioUtilizadoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

