package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.service.ApoioUtilizadoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/apoios")
@Slf4j // Anotação @Slf4j adicionada para gerar automaticamente uma instância de logger (chamada 'log')
public class ApoioUtilizadoController {

    @Autowired
    private ApoioUtilizadoService service;

    // Construtor, se houver lógica de inicialização. No caso, @Autowired já inicializa.
    public ApoioUtilizadoController(ApoioUtilizadoService service) {
        this.service = service;
        // Log de INFO para indicar que o controller foi inicializado.
        log.info("ApoioUtilizadoController inicializado.");
    }

    @PostMapping
    public ResponseEntity<ApoioUtilizadoResponseDTO> criar(
            @RequestBody ApoioUtilizadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId

        // Log de INFO para indicar o recebimento de uma requisição de criação.
        // Inclui o ID do colaborador para rastreamento.
        log.info("Recebida requisição POST para criar Apoio Utilizado para colaborador: {}", colaboradorId);
        // Log de DEBUG para mostrar o DTO recebido. Útil para depuração em ambiente de desenvolvimento.
        log.debug("Dados do ApoioUtilizadoRequestDTO recebidos: {}", dto);

        ApoioUtilizadoResponseDTO response = service.salvar(colaboradorId, dto); // Passa o colaboradorId para o serviço

        // Log de INFO para indicar o sucesso da operação, incluindo o ID do Apoio criado.
        log.info("Apoio Utilizado com ID {} criado com sucesso para colaborador {}.", response.id(), colaboradorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApoioUtilizadoResponseDTO>> listarMeusApoios(
            @AuthenticationPrincipal UserDetails currentUser) { // pega o usuário atual
        String colaboradorId = currentUser.getUsername(); // O username é o anonymousUserId

        // Log de INFO para indicar o recebimento de uma requisição de listagem.
        log.info("Recebida requisição GET para listar Apoios Utilizados do colaborador: {}", colaboradorId);

        // Este método agora deve listar apenas os apoios do colaborador autenticado
        List<ApoioUtilizadoResponseDTO> apoios = service.listarPorColaborador(colaboradorId);

        // Log de INFO para indicar o número de itens encontrados.
        log.info("Retornando {} Apoios Utilizados para o colaborador {}.", apoios.size(), colaboradorId);
        return ResponseEntity.ok(apoios);
    }

    @GetMapping("/{id}")
    public ApoioUtilizadoResponseDTO buscarPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { //  verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de INFO para indicar a busca por um ID específico.
        log.info("Recebida requisição GET para buscar Apoio Utilizado ID: {} para colaborador: {}", id, colaboradorId);

        // O serviço deve verificar se o ID pertence ao colaborador autenticado
        ApoioUtilizadoResponseDTO apoio = service.buscarPorIdEColaborador(id, colaboradorId);

        // Log de INFO para indicar que o apoio foi encontrado.
        log.info("Apoio Utilizado ID {} encontrado para colaborador {}.", id, colaboradorId);
        return apoio;
    }

    @PutMapping("/{id}")
    public ApoioUtilizadoResponseDTO atualizar(
            @PathVariable String id,
            @RequestBody ApoioUtilizadoRequestDTO dto,
            @AuthenticationPrincipal UserDetails currentUser) { // verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de INFO para indicar uma requisição de atualização.
        log.info("Recebida requisição PUT para atualizar Apoio Utilizado ID: {} para colaborador: {}", id, colaboradorId);
        // Log de DEBUG para mostrar os dados de atualização.
        log.debug("Dados de atualização para ApoioUtilizadoRequestDTO: {}", dto);

        // O serviço deve verificar se o ID pertence ao colaborador autenticado antes de atualizar
        ApoioUtilizadoResponseDTO updatedApoio = service.atualizar(id, colaboradorId, dto);

        // Log de INFO para indicar o sucesso da atualização.
        log.info("Apoio Utilizado ID {} atualizado com sucesso para colaborador {}.", id, colaboradorId);
        return updatedApoio;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Adicionado: para verificar posse
        String colaboradorId = currentUser.getUsername();

        // Log de WARN (aviso) para operações de DELETE, pois são destrutivas.
        log.warn("Recebida requisição DELETE para Apoio Utilizado ID: {} do colaborador: {}", id, colaboradorId);

        // O serviço deve verificar se o ID pertence ao colaborador autenticado antes de deletar
        service.deletar(id, colaboradorId);

        // Log de INFO para indicar o sucesso da deleção.
        log.info("Apoio Utilizado ID {} deletado com sucesso para colaborador {}.", id, colaboradorId);
        return ResponseEntity.noContent().build();
    }
}