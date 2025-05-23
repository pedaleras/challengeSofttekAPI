package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import br.com.fiap.challengeSofttekAPI.repository.ApoioUtilizadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApoioUtilizadoService {

    @Autowired
    private ApoioUtilizadoRepository repository;

    public ApoioUtilizadoResponseDTO salvar(ApoioUtilizadoRequestDTO dto) {
        ApoioUtilizado apoio = new ApoioUtilizado(dto.tipo(),dto.descricao());

        ApoioUtilizado salvo = repository.save(apoio);
        return new ApoioUtilizadoResponseDTO(salvo);
    }

    public List<ApoioUtilizadoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ApoioUtilizadoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ApoioUtilizadoResponseDTO buscarPorId(Long id) {
        ApoioUtilizado apoio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apoio não encontrado"));
        return new ApoioUtilizadoResponseDTO(apoio);
    }

    public ApoioUtilizadoResponseDTO atualizar(Long id, ApoioUtilizadoRequestDTO dto) {
        ApoioUtilizado apoio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apoio não encontrado"));

        apoio.setTipoApoio(dto.tipo());
        apoio.setDescricao(dto.descricao());
        apoio.setDataRegistro(LocalDateTime.now());

        ApoioUtilizado atualizado = repository.save(apoio);
        return new ApoioUtilizadoResponseDTO(atualizado);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Apoio não encontrado");
        }
        repository.deleteById(id);
    }
}
