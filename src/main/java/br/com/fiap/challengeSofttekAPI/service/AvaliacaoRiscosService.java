package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import br.com.fiap.challengeSofttekAPI.repository.AvaliacaoRiscosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoRiscosService {

    private final AvaliacaoRiscosRepository repository;

    @Autowired
    public AvaliacaoRiscosService(AvaliacaoRiscosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO salvar(String colaboradorId, AvaliacaoRiscosRequestDTO dto) {
        // Lógica para calcular a média percentual a partir dos níveis de estresse, ansiedade e depressão
        double somaNiveis = dto.estresse() + dto.ansiedade() + dto.depressao();
        // Assumindo que a escala máxima para cada um é 5, o total máximo é 15 (5+5+5).
        // Convertemos para uma escala de 0 a 100.
        double mediaPercentualCalculada = (somaNiveis / 15.0) * 100.0;

        // Cria uma nova AvaliacaoRiscos
        AvaliacaoRiscos avaliacao = new AvaliacaoRiscos();
        avaliacao.setColaboradorId(colaboradorId);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        // Define a média percentual, que, por sua vez, calcula a categoriaFinal dentro do modelo
        avaliacao.setMediaPercentual(mediaPercentualCalculada);

        AvaliacaoRiscos salvo = repository.save(avaliacao);
        return new AvaliacaoRiscosResponseDTO(salvo);
    }

    public List<AvaliacaoRiscosResponseDTO> listarPorColaborador(String colaboradorId) {
        return repository.findByColaboradorId(colaboradorId)
                .stream()
                .map(AvaliacaoRiscosResponseDTO::new)
                .collect(Collectors.toList());
    }

    public AvaliacaoRiscosResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador"));
        return new AvaliacaoRiscosResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO atualizar(String id, String colaboradorId, AvaliacaoRiscosRequestDTO dto) {
        AvaliacaoRiscos avaliacao = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador"));

        // Recalcula a média percentual com base nos novos dados
        double somaNiveis = dto.estresse() + dto.ansiedade() + dto.depressao();
        double mediaPercentualCalculada = (somaNiveis / 15.0) * 100.0;

        avaliacao.setMediaPercentual(mediaPercentualCalculada); // Isso irá recalcular a categoriaFinal
        // avaliacao.setDataAvaliacao(LocalDateTime.now()); // Pode-se optar por atualizar a data da avaliação aqui

        AvaliacaoRiscos atualizada = repository.save(avaliacao);
        return new AvaliacaoRiscosResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(String id, String colaboradorId) {
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Avaliação não encontrada ou não pertence ao colaborador");
        }
        repository.deleteById(id);
    }
}