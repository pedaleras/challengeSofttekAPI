package br.com.fiap.challengeSofttekAPI.controller;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/humores")
public class HumorController {

    private static final Logger logger = LoggerFactory.getLogger(HumorController.class);

    @Autowired
    private HumorRepository repository;

    @PostMapping
    public ResponseEntity<Humor> criar(
            @RequestBody @Valid Humor humor,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        logger.info("Tentativa de criação de Humor.");

        // O Spring Security injeta 'currentUser' com o AnonymousUserDetails se autenticado.
        // Se 'currentUser' for null, significa que não há autenticação (o filtro não encontrou token válido ou a rota não é permitAll).
        if (currentUser == null) {
            logger.warn("Requisição não autenticada para /humores. Retornando 401.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails
        logger.info("anonymousUserId extraído do principal: {}", anonymousUserId);

        humor.setColaboradorId(anonymousUserId);
        humor.setDataRegistro(LocalDateTime.now());

        repository.save(humor);
        logger.info("Humor registrado com sucesso para o colaborador {}", anonymousUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(humor);
    }

    @GetMapping
    public ResponseEntity<List<Humor>> listarTodosHumores(
            @AuthenticationPrincipal UserDetails currentUser) { // Adicionado para consistência na verificação de autenticação
        logger.info("Tentativa de listar todos os Humores.");

        if (currentUser == null) { // Verifica se há um usuário autenticado
            logger.warn("Requisição não autenticada para listar Humores. Retornando 401.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // Decisão de negócio: Listar todos os humores registrados, independente do colaborador
        // Se a regra fosse para listar apenas os humores do usuário logado, a query seria diferente (ex: repository.findByColaboradorId(currentUser.getUsername());)
        List<Humor> humores = repository.findAll();
        logger.info("Listagem de {} humores realizada com sucesso.", humores.size());
        return ResponseEntity.ok(humores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Humor> buscarHumorPorId(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        logger.info("Tentativa de buscar Humor pelo ID: {}", id);

        if (currentUser == null) { // Verifica se há um usuário autenticado
            logger.warn("Requisição não autenticada para buscar Humor por ID. Retornando 401.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Humor> humor = repository.findById(id);

        if (humor.isPresent()) {
            // Regra de segurança: Um usuário anônimo só pode ver seus próprios humores
            String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails
            if (!humor.get().getColaboradorId().equals(anonymousUserId)) {
                logger.warn("Acesso negado: Humor ID {} não pertence ao colaborador {}.", id, anonymousUserId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            logger.info("Humor ID {} encontrado com sucesso.", id);
            return ResponseEntity.ok(humor.get());
        } else {
            logger.warn("Humor ID {} não encontrado.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Humor> atualizarHumor(
            @PathVariable String id,
            @RequestBody @Valid Humor humorAtualizado,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        logger.info("Tentativa de atualizar Humor ID: {}", id);

        if (currentUser == null) { // Verifica se há um usuário autenticado
            logger.warn("Requisição não autenticada para atualizar Humor. Retornando 401.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails

        Optional<Humor> humorExistenteOpt = repository.findById(id);

        if (humorExistenteOpt.isEmpty()) {
            logger.warn("Humor ID {} não encontrado para atualização.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Humor humorExistente = humorExistenteOpt.get();

        // **Verificação de propriedade:**
        // Garante que apenas o colaborador que criou o humor pode atualizá-lo.
        if (!humorExistente.getColaboradorId().equals(anonymousUserId)) {
            logger.warn("Acesso negado: Colaborador {} tentou atualizar Humor ID {} que não lhe pertence.", anonymousUserId, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        // Atualiza apenas os campos permitidos.
        // O `id`, `colaboradorId` e `dataRegistro` de criação não devem ser alterados aqui.
        humorExistente.setNivelHumor(humorAtualizado.getNivelHumor());

        Humor humorSalvo = repository.save(humorExistente);
        logger.info("Humor ID {} atualizado com sucesso pelo colaborador {}.", id, anonymousUserId);
        return ResponseEntity.ok(humorSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHumor(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails currentUser) { // Usar @AuthenticationPrincipal UserDetails
        logger.info("Tentativa de deletar Humor ID: {}", id);

        if (currentUser == null) { // Verifica se há um usuário autenticado
            logger.warn("Requisição não autenticada para deletar Humor. Retornando 401.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String anonymousUserId = currentUser.getUsername(); // Extrai o ID do usuário do UserDetails

        Optional<Humor> humorExistenteOpt = repository.findById(id);

        if (humorExistenteOpt.isEmpty()) {
            logger.warn("Humor ID {} não encontrado para deleção.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Humor humorExistente = humorExistenteOpt.get();

        // **Verificação de propriedade:**
        // Garante que apenas o colaborador que criou o humor pode deletá-lo.
        if (!humorExistente.getColaboradorId().equals(anonymousUserId)) {
            logger.warn("Acesso negado: Colaborador {} tentou deletar Humor ID {} que não lhe pertence.", anonymousUserId, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        repository.deleteById(id);
        logger.info("Humor ID {} deletado com sucesso pelo colaborador {}.", id, anonymousUserId);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content para deleção bem-sucedida
    }
}