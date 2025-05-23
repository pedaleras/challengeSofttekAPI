package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.HumorRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.HumorResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.model.NivelHumor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HumorService {

    @Autowired
    private final HumorRepository repository;

    public HumorService(HumorRepository humorRepository) {
        this.repository = humorRepository;
    }

    @Transactional
    public HumorResponseDTO salvar(HumorRequestDTO dto) {
        Humor humor = new Humor(dto.nivel());

        Humor salvo = repository.save(humor);
        return new HumorResponseDTO(salvo);
    }

    public List<HumorResponseDTO> listar() {
        return repository.findAll().stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<HumorResponseDTO> buscarPorId(Long id) {
        return repository.findById(id).map(HumorResponseDTO::new);
    }

    @Transactional
    public Optional<HumorResponseDTO> atualizar(Long id, HumorRequestDTO dto) {
        return repository.findById(id).map(humor -> {
            humor.setNivelHumor(dto.nivel());
            humor.setDescricaoHumor(NivelHumor.fromNivel(dto.nivel()));
            humor.setDataRegistro(LocalDateTime.now());

            Humor atualizado = repository.save(humor);
            return new HumorResponseDTO(atualizado);
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

