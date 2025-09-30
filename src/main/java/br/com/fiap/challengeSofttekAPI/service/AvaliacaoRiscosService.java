package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import br.com.fiap.challengeSofttekAPI.repository.AvaliacaoRiscosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AvaliacaoRiscosService {

    private final AvaliacaoRiscosRepository repository;

    @Autowired
    public AvaliacaoRiscosService(AvaliacaoRiscosRepository repository) {
        this.repository = repository;
        // Log de INFO para indicar que o serviço foi inicializado.
        log.info("AvaliacaoRiscosService inicializado.");
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO salvar(String colaboradorId, AvaliacaoRiscosRequestDTO dto) {
        log.info("Iniciando salvamento de Avaliação de Riscos para colaborador: {}.", colaboradorId);
        log.debug("DTO recebido: {}", dto);

        // Lógica para calcular a média percentual a partir dos níveis de estresse, ansiedade e depressão
        double somaNiveis = dto.estresse() + dto.ansiedade() + dto.depressao();
        // Assumindo que a escala máxima para cada um é 5, o total máximo é 15 (5+5+5).
        // Convertemos para uma escala de 0 a 100.
        double mediaPercentualCalculada = (somaNiveis / 15.0) * 100.0;
        log.debug("Média percentual calculada: {} para estresse={}, ansiedade={}, depressao={}.",
                mediaPercentualCalculada, dto.estresse(), dto.ansiedade(), dto.depressao());

        // Cria uma nova AvaliacaoRiscos
        AvaliacaoRiscos avaliacao = new AvaliacaoRiscos();
        avaliacao.setColaboradorId(colaboradorId);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        // Define a média percentual, que, por sua vez, calcula a categoriaFinal dentro do modelo
        avaliacao.setMediaPercentual(mediaPercentualCalculada);
        log.debug("Objeto AvaliacaoRiscos preparado para salvar: {}", avaliacao);

        AvaliacaoRiscos salvo = repository.save(avaliacao);
        log.info("Avaliação de Riscos ID {} salva com sucesso para o colaborador {}. Categoria final: {}.",
                salvo.getId(), colaboradorId, salvo.getCategoriaFinal());
        return new AvaliacaoRiscosResponseDTO(salvo);
    }

    public List<AvaliacaoRiscosResponseDTO> listarPorColaborador(String colaboradorId) {
        log.info("Buscando Avaliações de Riscos para o colaborador: {}.", colaboradorId);
        List<AvaliacaoRiscos> avaliacoes = repository.findByColaboradorId(colaboradorId);
        log.info("Encontradas {} Avaliações de Riscos para o colaborador {}.", avaliacoes.size(), colaboradorId);
        return avaliacoes.stream()
                .map(AvaliacaoRiscosResponseDTO::new)
                .collect(Collectors.toList());
    }

    public AvaliacaoRiscosResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        log.info("Buscando Avaliação de Riscos ID {} para o colaborador {}.", id, colaboradorId);
        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Avaliação de Riscos ID {} não encontrada ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
                });
        log.info("Avaliação de Riscos ID {} encontrada com sucesso para o colaborador {}.", id, colaboradorId);
        return new AvaliacaoRiscosResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO atualizar(String id, String colaboradorId, AvaliacaoRiscosRequestDTO dto) {
        log.info("Iniciando atualização da Avaliação de Riscos ID {} para o colaborador {}.", id, colaboradorId);
        log.debug("DTO de atualização recebido: {}", dto);

        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualização falhou: Avaliação de Riscos ID {} não encontrada ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
                });

        log.debug("Avaliação de Riscos existente encontrada: {}. Aplicando atualizações.", avaliacao.getId());

        // Recalcula a média percentual com base nos novos dados
        double somaNiveis = dto.estresse() + dto.ansiedade() + dto.depressao();
        double mediaPercentualCalculada = (somaNiveis / 15.0) * 100.0;
        log.debug("Recalculada média percentual para {}. Estresse={}, Ansiedade={}, Depressão={}.",
                mediaPercentualCalculada, dto.estresse(), dto.ansiedade(), dto.depressao());

        avaliacao.setMediaPercentual(mediaPercentualCalculada); // Isso irá recalcular a categoriaFinal
        // avaliacao.setDataAvaliacao(LocalDateTime.now()); // Pode-se optar por atualizar a data da avaliação aqui

        AvaliacaoRiscos atualizada = repository.save(avaliacao);
        log.info("Avaliação de Riscos ID {} atualizada com sucesso para o colaborador {}. Nova Categoria final: {}.",
                atualizada.getId(), colaboradorId, atualizada.getCategoriaFinal());
        return new AvaliacaoRiscosResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(String id, String colaboradorId) {
        log.warn("Iniciando deleção da Avaliação de Riscos ID {} para o colaborador {}.", id, colaboradorId);

        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) {
            log.warn("Tentativa de deleção falhou: Avaliação de Riscos ID {} não encontrada ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
        }
        repository.deleteById(id);
        log.info("Avaliação de Riscos ID {} deletada com sucesso para o colaborador {}.", id, colaboradorId);
    }
}