package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/humores")
@Slf4j // Anotação @Slf4j adicionada para gerar automaticamente uma instância de logger (chamada 'log')
public class HumorController {

    // Removida: private static final Logger logger = LoggerFactory.getLogger(HumorController.class);

    @Autowired
    private HumorRepository repository;

    // Construtor, se houver lógica de inicialização. No caso, @Autowired já inicializa.
    public HumorController(HumorRepository repository) {
        this.repository = repository;
        // Log de INFO para indicar que o controller foi inicializado.
        log.info("HumorController inicializado.");
    }

    @PostMapping
    public ResponseEntity<Humor> criar(
            @RequestBody @Valid Humor humor,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        log.info("Recebida requisição POST para criação de Humor."); // Alterado de "Tentativa de criação..."

        // O Spring Security injeta 'currentUser' com o AnonymousUserDetails se autenticado.
        // Se 'currentUser' for null, significa que não há autenticação (o filtro não encontrou token válido ou a rota não é permitAll).
        if (currentUser == null) {
            log.warn("Requisição não autenticada para /humores. Retornando 401 UNAUTHORIZED."); // Melhorado o log
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails
        log.debug("anonymousUserId extraído do principal: {}", anonymousUserId); // Alterado para DEBUG, pois é um detalhe interno

        humor.setColaboradorId(anonymousUserId);
        humor.setDataRegistro(LocalDateTime.now());

        log.info("Salvando novo Humor para o colaborador {}.", anonymousUserId); // Adicionado log antes de salvar
        repository.save(humor);
        log.info("Humor com Nível {} registrado com sucesso para o colaborador {}", humor.getNivelHumor(), anonymousUserId); // Mais detalhes no log
        return ResponseEntity.status(HttpStatus.CREATED).body(humor);
    }

    @GetMapping
    public ResponseEntity<List<Humor>> listarTodosHumores(
            @AuthenticationPrincipal UserDetails currentUser) { // Adicionado para consistência na verificação de autenticação
        log.info("Recebida requisição GET para listar todos os Humores."); // Alterado

        if (currentUser == null) { // Verifica se há um usuário autenticado
            log.warn("Requisição não autenticada para listar Humores. Retornando 401 UNAUTHORIZED.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // Decisão de negócio: Listar todos os humores registrados, independente do colaborador
        // Se a regra fosse para listar apenas os humores do usuário logado, a query seria diferente (ex: repository.findByColaboradorId(currentUser.getUsername());)
        List<Humor> humores = repository.findAll();
        log.info("Listagem de {} humores realizada com sucesso para o colaborador {}.", humores.size(), currentUser.getUsername()); // Adicionado colaborador para contexto
        return ResponseEntity.ok(humores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Humor> buscarHumorPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        log.info("Recebida requisição GET para buscar Humor pelo ID: {} do colaborador {}.", id, currentUser != null ? currentUser.getUsername() : "não autenticado"); // Melhorado o log

        if (currentUser == null) { // Verifica se há um usuário autenticado
            log.warn("Requisição não autenticada para buscar Humor por ID. Retornando 401 UNAUTHORIZED.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Humor> humor = repository.findById(id);

        if (humor.isPresent()) {
            // Regra de segurança: Um usuário anônimo só pode ver seus próprios humores
            String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails
            if (!humor.get().getColaboradorId().equals(anonymousUserId)) {
                log.warn("Acesso negado: Colaborador {} tentou acessar Humor ID {} que pertence a {}. Retornando 403 FORBIDDEN.",
                        anonymousUserId, id, humor.get().getColaboradorId()); // Mais detalhes no log
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            log.info("Humor ID {} encontrado com sucesso para o colaborador {}.", id, anonymousUserId);
            return ResponseEntity.ok(humor.get());
        } else {
            log.warn("Humor ID {} não encontrado para o colaborador {}. Retornando 404 NOT_FOUND.", id, currentUser.getUsername()); // Log mais específico
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Humor> atualizarHumor(
            @PathVariable String id,
            @RequestBody @Valid Humor humorAtualizado,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        log.info("Recebida requisição PUT para atualizar Humor ID: {} do colaborador {}.", id, currentUser != null ? currentUser.getUsername() : "não autenticado");
        log.debug("Dados de atualização para Humor ID {}: {}", id, humorAtualizado); // Detalhes da requisição em DEBUG

        if (currentUser == null) { // Verifica se há um usuário autenticado
            log.warn("Requisição não autenticada para atualizar Humor. Retornando 401 UNAUTHORIZED.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails

        Optional<Humor> humorExistenteOpt = repository.findById(id);

        if (humorExistenteOpt.isEmpty()) {
            log.warn("Humor ID {} não encontrado para atualização do colaborador {}. Retornando 404 NOT_FOUND.", id, anonymousUserId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Humor humorExistente = humorExistenteOpt.get();

        // **Verificação de propriedade:**
        // Garante que apenas o colaborador que criou o humor pode atualizá-lo.
        if (!humorExistente.getColaboradorId().equals(anonymousUserId)) {
            log.warn("Acesso negado: Colaborador {} tentou atualizar Humor ID {} que não lhe pertence (pertence a {}). Retornando 403 FORBIDDEN.",
                    anonymousUserId, id, humorExistente.getColaboradorId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        // Atualiza apenas os campos permitidos.
        // O `id`, `colaboradorId` e `dataRegistro` de criação não devem ser alterados aqui.
        humorExistente.setNivelHumor(humorAtualizado.getNivelHumor());

        Humor humorSalvo = repository.save(humorExistente);
        log.info("Humor ID {} atualizado com sucesso (novo nível: {}) pelo colaborador {}.", id, humorSalvo.getNivelHumor(), anonymousUserId);
        return ResponseEntity.ok(humorSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHumor(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        log.warn("Recebida requisição DELETE para Humor ID: {} do colaborador {}.", id, currentUser != null ? currentUser.getUsername() : "não autenticado"); // Nível WARN para DELETE

        if (currentUser == null) { // Verifica se há um usuário autenticado
            log.warn("Requisição não autenticada para deletar Humor. Retornando 401 UNAUTHORIZED.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails

        Optional<Humor> humorExistenteOpt = repository.findById(id);

        if (humorExistenteOpt.isEmpty()) {
            log.warn("Humor ID {} não encontrado para deleção do colaborador {}. Retornando 404 NOT_FOUND.", id, anonymousUserId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Humor humorExistente = humorExistenteOpt.get();

        // **Verificação de propriedade:**
        // Garante que apenas o colaborador que criou o humor pode deletá-lo.
        if (!humorExistente.getColaboradorId().equals(anonymousUserId)) {
            log.warn("Acesso negado: Colaborador {} tentou deletar Humor ID {} que não lhe pertence (pertence a {}). Retornando 403 FORBIDDEN.",
                    anonymousUserId, id, humorExistente.getColaboradorId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        repository.deleteById(id);
        log.info("Humor ID {} deletado com sucesso pelo colaborador {}.", id, anonymousUserId);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content para deleção bem-sucedida
    }
}