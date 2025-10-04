package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.HumorRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.HumorResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.fiap.challengeSofttekAPI.util.SecurityUtils.getCurrentUserId;

@Service
public class HumorService {

    private final HumorRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(HumorService.class);

    public HumorService(HumorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HumorResponseDTO salvar(HumorRequestDTO dto) {
        String colaboradorId = getCurrentUserId();
        Humor humor = new Humor(colaboradorId, dto.nivel());
        Humor salvo = repository.save(humor);
        logger.info("Humor salvo pelo colaborador {}", colaboradorId);
        return new HumorResponseDTO(salvo);
    }

    public List<HumorResponseDTO> listarTodos() {
        List<HumorResponseDTO> lista = repository.findAll()
                .stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());

        String colaboradorId = getCurrentUserId();
        logger.info("Listagem de {} humores realizada pelo colaborador {}", lista.size(), colaboradorId);
        return lista;
    }

    public List<HumorResponseDTO> buscarPorIdColaborador(String idColaborador) {
        List<Humor> humores = repository.findAllByColaboradorId(idColaborador);

        if (humores.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhum registro de humor encontrado para o colaborador");
        }

        logger.info("Recuperados {} registros de humor para colaborador {}", humores.size(), idColaborador);

        return humores.stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());
    }
}
