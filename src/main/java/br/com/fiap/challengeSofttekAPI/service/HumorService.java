package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.HumorRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.HumorResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.model.NivelHumor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import adicionado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HumorService {

    private final HumorRepository repository;

    @Autowired // @Autowired no construtor é uma boa prática
    public HumorService(HumorRepository humorRepository) {
        this.repository = humorRepository;
    }

    @Transactional
    // SALVAR ATUALIZADO PARA INCLUIR colaboradorId
    public HumorResponseDTO salvar(String colaboradorId, HumorRequestDTO dto) {
        // Cria um novo Humor, passando o colaboradorId
        Humor humor = new Humor(colaboradorId, dto.nivel()); // Usa o novo construtor do Model
        // dataRegistro e descricaoHumor já são setados no construtor do model

        Humor salvo = repository.save(humor);
        return new HumorResponseDTO(salvo);
    }

    // LISTAR HUMORES POR COLABORADOR (SUBSTITUI listar)
    public List<HumorResponseDTO> listarPorColaborador(String colaboradorId) {
        return repository.findByColaboradorId(colaboradorId) // Chama novo método no repositório
                .stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID E COLABORADOR (SUBSTITUI buscarPorId)
    public HumorResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId) // Chama novo método no repositório
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador")); // Lança exceção 404
        return new HumorResponseDTO(humor);
    }

    @Transactional
    // ATUALIZAR REVISADO PARA INCLUIR colaboradorId
    public HumorResponseDTO atualizar(String id, String colaboradorId, HumorRequestDTO dto) {
        // Busca o humor, garantindo que ele exista E pertença ao colaborador
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador"));

        humor.setNivelHumor(dto.nivel());
        humor.setDescricaoHumor(NivelHumor.fromNivel(dto.nivel())); // Atualiza a descrição com base no novo nível
        humor.setDataRegistro(LocalDateTime.now()); // Atualiza a data do registro

        Humor atualizado = repository.save(humor);
        return new HumorResponseDTO(atualizado);
    }

    @Transactional
    // DELETAR REVISADO PARA INCLUIR colaboradorId
    public void deletar(String id, String colaboradorId) {
        // Primeiro, verifica se o registro de humor existe e pertence ao colaborador
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) { // Chama novo método no repositório
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
    }

}