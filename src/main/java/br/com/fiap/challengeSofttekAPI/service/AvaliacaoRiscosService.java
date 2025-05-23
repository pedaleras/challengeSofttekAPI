package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.AvaliacaoRiscosResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import br.com.fiap.challengeSofttekAPI.repository.AvaliacaoRiscosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.LambdaConversionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvaliacaoRiscosService {

    @Autowired
    private final AvaliacaoRiscosRepository repository;

    public AvaliacaoRiscosService(AvaliacaoRiscosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AvaliacaoRiscosResponseDTO salvar(AvaliacaoRiscosRequestDTO dto) {
        AvaliacaoRiscos avaliacao = new AvaliacaoRiscos(dto.mediaPercentual(), dto.categoriaFinal());

        AvaliacaoRiscos salvo = repository.save(avaliacao);
        return new AvaliacaoRiscosResponseDTO(salvo);
    }

    public List<AvaliacaoRiscosResponseDTO> listar() {
        return repository.findAll().stream()
                .map(AvaliacaoRiscosResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<AvaliacaoRiscosResponseDTO> buscarPorId(Long id) {
        return repository.findById(id).map(AvaliacaoRiscosResponseDTO::new);
    }

    @Transactional
    public Optional<AvaliacaoRiscosResponseDTO> atualizar(Long id, AvaliacaoRiscosRequestDTO dto) {
        return repository.findById(id).map(avaliacao -> {
            avaliacao.setDataAvaliacao(LocalDateTime.now());
            avaliacao.setMediaPercentual(dto.mediaPercentual());
            avaliacao.setCategoriaFinal(dto.categoriaFinal());

            AvaliacaoRiscos atualizado = repository.save(avaliacao);
            return new AvaliacaoRiscosResponseDTO(atualizado);
        });
    }

    @Transactional
    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
