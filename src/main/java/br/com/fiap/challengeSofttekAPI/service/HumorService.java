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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HumorService {

    private final HumorRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(HumorService.class);

    public HumorService(HumorRepository repository) {
        this.repository = repository;
    }

    // Pega o ID do usuário logado via SecurityContext
    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
    }

    @Transactional
    public HumorResponseDTO salvar(HumorRequestDTO dto) {
        String colaboradorId = getCurrentUserId();
        Humor humor = new Humor(colaboradorId, dto.nivel());
        Humor salvo = repository.save(humor);
        logger.info("Humor salvo pelo colaborador {}", colaboradorId);
        return new HumorResponseDTO(salvo);
    }

    public List<HumorResponseDTO> listarPorColaborador() {
        String colaboradorId = getCurrentUserId();
        List<HumorResponseDTO> lista = repository.findByColaboradorId(colaboradorId)
                .stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());
        logger.info("Listagem de {} humores realizada para colaborador {}", lista.size(), colaboradorId);
        return lista;
    }

    public HumorResponseDTO buscarPorId(String id) {
        String colaboradorId = getCurrentUserId();
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Registro de humor não encontrado ou não pertence ao colaborador"));
        logger.info("Humor ID {} recuperado para colaborador {}", id, colaboradorId);
        return new HumorResponseDTO(humor);
    }

    @Transactional
    public HumorResponseDTO atualizar(String id, HumorRequestDTO dto) {
        String colaboradorId = getCurrentUserId();
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Registro de humor não encontrado ou não pertence ao colaborador"));
        humor.setNivelHumor(dto.nivel());
        humor.setDataRegistro(LocalDateTime.now());
        Humor atualizado = repository.save(humor);
        logger.info("Humor ID {} atualizado pelo colaborador {}", id, colaboradorId);
        return new HumorResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(String id) {
        String colaboradorId = getCurrentUserId();
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Registro de humor não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
        logger.info("Humor ID {} deletado pelo colaborador {}", id, colaboradorId);
    }
}
