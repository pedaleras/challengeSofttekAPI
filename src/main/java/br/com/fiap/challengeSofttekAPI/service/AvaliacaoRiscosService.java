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

        AvaliacaoRiscos avaliacao = new AvaliacaoRiscos();
        avaliacao.setColaboradorId(colaboradorId);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        avaliacao.setMediaPercentual(dto.mediaPercentual());

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


        avaliacao.setMediaPercentual(dto.mediaPercentual());
        avaliacao.setDataAvaliacao(LocalDateTime.now());

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