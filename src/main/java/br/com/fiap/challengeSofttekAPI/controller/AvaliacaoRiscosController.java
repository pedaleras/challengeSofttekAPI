package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.AvaliacaoRiscosService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/avaliacoes-riscos")
@Slf4j
public class AvaliacaoRiscosController {

    @Autowired
    private AvaliacaoRiscosService service;

    // Construtor, se houver lógica de inicialização. No caso, @Autowired já inicializa.
    public AvaliacaoRiscosController(AvaliacaoRiscosService service) {
        this.service = service;
        // Log de INFO para indicar que o controller foi inicializado.
        log.info("AvaliacaoRiscosController inicializado.");
    }

    @PostMapping
    public ResponseEntity<AvaliacaoRiscosResponseDTO> criar(
            @RequestBody @Valid AvaliacaoRiscosRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId

        // Log de INFO para indicar o recebimento de uma requisição de criação.
        // Inclui o ID do colaborador para rastreamento.
        log.info("Recebida requisição POST para criar Avaliação de Riscos para colaborador: {}.", colaboradorId);
        // Log de DEBUG para mostrar o DTO recebido. Útil para depuração em ambiente de desenvolvimento.
        log.debug("Dados do AvaliacaoRiscosRequestDTO recebidos: {}.", dto);

        AvaliacaoRiscosResponseDTO response = service.salvar(colaboradorId, dto); // Passa o colaboradorId para o serviço

        // Log de INFO para indicar o sucesso da operação, incluindo o ID da Avaliação criada.
        log.info("Avaliação de Riscos com ID {} criada com sucesso para colaborador {}.", response.id(), colaboradorId);
        return ResponseEntity.ok(response);
    }

    // Método alterado de listarTodos para listarMinhasAvaliacoes
    @GetMapping
    public ResponseEntity<List<AvaliacaoRiscosResponseDTO>> listarMinhasAvaliacoes(
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId

        // Log de INFO para indicar o recebimento de uma requisição de listagem.
        log.info("Recebida requisição GET para listar Avaliações de Riscos do colaborador: {}.", colaboradorId);

        // Este método agora deve listar apenas as avaliações do colaborador autenticado
        List<AvaliacaoRiscosResponseDTO> avaliacoes = service.listarPorColaborador(colaboradorId);

        // Log de INFO para indicar o número de itens encontrados.
        log.info("Retornando {} Avaliações de Riscos para o colaborador {}.", avaliacoes.size(), colaboradorId);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> buscarPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // para verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de INFO para indicar a busca por um ID específico.
        log.info("Recebida requisição GET para buscar Avaliação de Riscos ID: {} para colaborador: {}.", id, colaboradorId);

        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        AvaliacaoRiscosResponseDTO dto = service.buscarPorIdEColaborador(id, colaboradorId);
        // Log de INFO para indicar que a avaliação foi encontrada.
        log.info("Avaliação de Riscos ID {} encontrada para colaborador {}.", id, colaboradorId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoRiscosResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid AvaliacaoRiscosRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // para verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de INFO para indicar uma requisição de atualização.
        log.info("Recebida requisição PUT para atualizar Avaliação de Riscos ID: {} para colaborador: {}.", id, colaboradorId);
        // Log de DEBUG para mostrar os dados de atualização.
        log.debug("Dados de atualização para AvaliacaoRiscosRequestDTO: {}.", dto);

        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        AvaliacaoRiscosResponseDTO atualizado = service.atualizar(id, colaboradorId, dto);

        // Log de INFO para indicar o sucesso da atualização.
        log.info("Avaliação de Riscos ID {} atualizada com sucesso para colaborador {}.", id, colaboradorId);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de WARN (aviso) para operações de DELETE, pois são destrutivas.
        log.warn("Recebida requisição DELETE para Avaliação de Riscos ID: {} do colaborador: {}.", id, colaboradorId);

        // O serviço agora lança uma exceção se não encontrar ou não pertencer ao colaborador
        service.deletar(id, colaboradorId);

        // Log de INFO para indicar o sucesso da deleção.
        log.info("Avaliação de Riscos ID {} deletada com sucesso para colaborador {}.", id, colaboradorId);
        return ResponseEntity.noContent().build();
    }
}