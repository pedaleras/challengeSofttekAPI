package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import br.com.fiap.challengeSofttekAPI.repository.ApoioUtilizadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApoioUtilizadoService {

    private final ApoioUtilizadoRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ApoioUtilizadoService.class);

    public ApoioUtilizadoService(ApoioUtilizadoRepository repository) {
        this.repository = repository;
    }

    public ApoioUtilizadoResponseDTO salvar(ApoioUtilizadoRequestDTO dto) {
        ApoioUtilizado apoio = new ApoioUtilizado(dto.tipo(), dto.descricao());
        apoio.setDataRegistro(LocalDateTime.now());

        ApoioUtilizado salvo = repository.save(apoio);
        logger.info("Apoio salvo");
        return new ApoioUtilizadoResponseDTO(salvo);
    }

    public List<ApoioUtilizadoResponseDTO> listar() {
        List<ApoioUtilizadoResponseDTO> lista = repository.findAll()
                .stream()
                .map(ApoioUtilizadoResponseDTO::new)
                .collect(Collectors.toList());

        logger.info("Listagem de {} apoios ", lista.size());
        return lista;
    }

    public ApoioUtilizadoResponseDTO buscarPorId(String id) {
        ApoioUtilizado apoio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador"));

        logger.info("Apoio ID {} recuperado", id);
        return new ApoioUtilizadoResponseDTO(apoio);
    }

    public ApoioUtilizadoResponseDTO atualizar(String id, ApoioUtilizadoRequestDTO dto) {
        ApoioUtilizado apoio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador"));

        apoio.setTipoApoio(dto.tipo());
        apoio.setDescricao(dto.descricao());

        ApoioUtilizado atualizado = repository.save(apoio);
        logger.info("Apoio ID {} atualizado", id);
        return new ApoioUtilizadoResponseDTO(atualizado);
    }

    public void deletar(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
        logger.info("Apoio ID {} deletado", id);
    }
}
