package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.HumorRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.HumorResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.HumorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/humores")
public class HumorController {

    private final HumorService humorService;

    public HumorController(HumorService humorService) {
        this.humorService = humorService;
    }

    @PostMapping
    public ResponseEntity<HumorResponseDTO> criar(@RequestBody @Valid HumorRequestDTO humorDTO) {
        HumorResponseDTO salvo = humorService.salvar(humorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<HumorResponseDTO>> listar() {
        return ResponseEntity.ok(humorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<HumorResponseDTO>> buscar(@PathVariable String id) {
        return ResponseEntity.ok(humorService.buscarPorIdColaborador(id));
    }

}
