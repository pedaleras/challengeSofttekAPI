package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.HumorRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.HumorResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.model.NivelHumor;
import br.com.fiap.challengeSofttekAPI.repository.HumorRepository;
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
public class HumorService {

    private final HumorRepository repository;

    @Autowired // @Autowired no construtor é uma boa prática
    public HumorService(HumorRepository humorRepository) {
        this.repository = humorRepository;
        // Log de INFO para indicar que o serviço foi inicializado.
        log.info("HumorService inicializado.");
    }

    @Transactional
    // SALVAR ATUALIZADO PARA INCLUIR colaboradorId
    public HumorResponseDTO salvar(String colaboradorId, HumorRequestDTO dto) {
        log.info("Iniciando processo de salvamento de Humor para colaborador: {}.", colaboradorId);
        log.debug("DTO recebido: {}", dto);

        // Cria um novo Humor, passando o colaboradorId
        Humor humor = new Humor(colaboradorId, dto.nivel()); // Usa o novo construtor do Model
        // dataRegistro e descricaoHumor já são setados no construtor do model
        log.debug("Objeto Humor preparado para salvar: {}", humor);

        Humor salvo = repository.save(humor);
        log.info("Humor ID {} (Nível: {}) salvo com sucesso para o colaborador {}.", salvo.getId(), salvo.getNivelHumor(), colaboradorId);
        return new HumorResponseDTO(salvo);
    }

    // LISTAR HUMORES POR COLABORADOR (SUBSTITUI listar)
    public List<HumorResponseDTO> listarPorColaborador(String colaboradorId) {
        log.info("Buscando Humores para o colaborador: {}.", colaboradorId);
        List<Humor> humores = repository.findByColaboradorId(colaboradorId); // Chama novo método no repositório
        log.info("Encontrados {} Humores para o colaborador {}.", humores.size(), colaboradorId);
        return humores.stream()
                .map(HumorResponseDTO::new)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID E COLABORADOR (SUBSTITUI buscarPorId)
    public HumorResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        log.info("Buscando Humor ID {} para o colaborador {}.", id, colaboradorId);
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId) // Chama novo método no repositório
                .orElseThrow(() -> {
                    log.warn("Humor ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador");
                });
        log.info("Humor ID {} encontrado com sucesso para o colaborador {}.", id, colaboradorId);
        return new HumorResponseDTO(humor);
    }

    @Transactional
    // ATUALIZAR REVISADO PARA INCLUIR colaboradorId
    public HumorResponseDTO atualizar(String id, String colaboradorId, HumorRequestDTO dto) {
        log.info("Iniciando atualização do Humor ID {} para o colaborador {}.", id, colaboradorId);
        log.debug("DTO de atualização recebido: {}", dto);

        // Busca o humor, garantindo que ele exista E pertença ao colaborador
        Humor humor = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualização falhou: Humor ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador");
                });

        log.debug("Humor existente ID {} encontrado. Aplicando atualizações. Nível atual: {}. Novo nível: {}.",
                humor.getId(), humor.getNivelHumor(), dto.nivel());

        humor.setNivelHumor(dto.nivel());
        humor.setDescricaoHumor(NivelHumor.fromNivel(dto.nivel())); // Atualiza a descrição com base no novo nível
        humor.setDataRegistro(LocalDateTime.now()); // Atualiza a data do registro

        Humor atualizado = repository.save(humor);
        log.info("Humor ID {} atualizado com sucesso para o colaborador {}. Novo nível: {}.",
                atualizado.getId(), colaboradorId, atualizado.getNivelHumor());
        return new HumorResponseDTO(atualizado);
    }

    @Transactional
    // DELETAR REVISADO PARA INCLUIR colaboradorId
    public void deletar(String id, String colaboradorId) {
        log.warn("Iniciando deleção do Humor ID {} para o colaborador {}.", id, colaboradorId); // Nível WARN para deleção

        // Primeiro, verifica se o registro de humor existe e pertence ao colaborador
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) { // Chama novo método no repositório
            log.warn("Tentativa de deleção falhou: Humor ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Registro de humor não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
        log.info("Humor ID {} deletado com sucesso para o colaborador {}.", id, colaboradorId);
    }

}