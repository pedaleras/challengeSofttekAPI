package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import br.com.fiap.challengeSofttekAPI.repository.AvaliacaoRiscosRepository;
import br.com.fiap.challengeSofttekAPI.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.fiap.challengeSofttekAPI.util.SecurityUtils.*;

@Slf4j
@Service
public class AvaliacaoRiscosService {

    private final AvaliacaoRiscosRepository repository;

    public AvaliacaoRiscosService(AvaliacaoRiscosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO salvar(AvaliacaoRiscosRequestDTO dto) {
        String colaboradorId = getCurrentUserId();
        log.debug("Criando avaliação de risco | colaborador={} | mediaPercentual={}", colaboradorId, dto.mediaPercentual());

        AvaliacaoRiscos avaliacao = new AvaliacaoRiscos();
        avaliacao.setColaboradorId(colaboradorId);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        avaliacao.setMediaPercentual(dto.mediaPercentual());

        AvaliacaoRiscos salvo = repository.save(avaliacao);
        log.info("Avaliação de risco criada | id={} | colaborador={}", salvo.getId(), colaboradorId);

        return new AvaliacaoRiscosResponseDTO(salvo);
    }

    public List<AvaliacaoRiscosResponseDTO> listarPorColaborador() {
        String colaboradorId = getCurrentUserId();
        log.debug("Listando avaliações | colaborador={}", colaboradorId);

        List<AvaliacaoRiscosResponseDTO> avaliacoes = repository.findByColaboradorId(colaboradorId)
                .stream()
                .map(AvaliacaoRiscosResponseDTO::new)
                .toList();

        log.info("Avaliações encontradas | colaborador={} | total={}", colaboradorId, avaliacoes.size());
        return avaliacoes;
    }

    public AvaliacaoRiscosResponseDTO buscarPorIdEColaborador(String id) {
        String colaboradorId = getCurrentUserId();
        log.debug("Buscando avaliação | id={} | colaborador={}", id, colaboradorId);

        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Avaliação não encontrada | id={} | colaborador={}", id, colaboradorId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
                });

        log.info("Avaliação encontrada | id={} | colaborador={}", id, colaboradorId);
        return new AvaliacaoRiscosResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO atualizar(String id, AvaliacaoRiscosRequestDTO dto) {
        String colaboradorId = getCurrentUserId();
        log.debug("Atualizando avaliação | id={} | colaborador={} | mediaPercentual={}", id, colaboradorId, dto.mediaPercentual());

        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar falhou | id={} | colaborador={}", id, colaboradorId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
                });

        avaliacao.setMediaPercentual(dto.mediaPercentual());
        avaliacao.setDataAvaliacao(LocalDateTime.now());

        AvaliacaoRiscos atualizado = repository.save(avaliacao);
        log.info("Avaliação atualizada | id={} | colaborador={}", atualizado.getId(), colaboradorId);

        return new AvaliacaoRiscosResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(String id) {
        String colaboradorId = getCurrentUserId();
        log.debug("Removendo avaliação | id={} | colaborador={}", id, colaboradorId);

        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) {
            log.warn("Tentativa de exclusão falhou | id={} | colaborador={}", id, colaboradorId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
        }

        repository.deleteById(id);
        log.info("Avaliação removida | id={} | colaborador={}", id, colaboradorId);
    }
}
