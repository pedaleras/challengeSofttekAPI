package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import br.com.fiap.challengeSofttekAPI.repository.ApoioUtilizadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApoioUtilizadoService {

    @Autowired
    private ApoioUtilizadoRepository repository;

    // Construtor, se houver lógica de inicialização. No caso, @Autowired já inicializa.
    public ApoioUtilizadoService(ApoioUtilizadoRepository repository) {
        this.repository = repository;
        // Log de INFO para indicar que o serviço foi inicializado.
        log.info("ApoioUtilizadoService inicializado.");
    }

    // SALVAR ATUALIZADO PARA INCLUIR colaboradorId
    public ApoioUtilizadoResponseDTO salvar(String colaboradorId, ApoioUtilizadoRequestDTO dto) {
        log.info("Iniciando processo de salvamento de Apoio Utilizado para colaborador: {}.", colaboradorId);
        log.debug("DTO recebido: {}", dto);

        // Cria um novo ApoioUtilizado, passando o colaboradorId
        ApoioUtilizado apoio = new ApoioUtilizado(colaboradorId, dto.tipo(), dto.descricao());
        apoio.setDataRegistro(LocalDateTime.now()); // Garante que a data de registro seja setada
        log.debug("Objeto ApoioUtilizado preparado para salvar: {}", apoio);

        ApoioUtilizado salvo = repository.save(apoio);
        log.info("Apoio Utilizado ID {} salvo com sucesso para o colaborador {}.", salvo.getId(), colaboradorId);
        return new ApoioUtilizadoResponseDTO(salvo);
    }

    // LISTAR APOIOS POR COLABORADOR (SUBSTITUI listarTodos)
    public List<ApoioUtilizadoResponseDTO> listarPorColaborador(String colaboradorId) {
        log.info("Buscando Apoios Utilizados para o colaborador: {}.", colaboradorId);
        List<ApoioUtilizado> apoios = repository.findByColaboradorId(colaboradorId); // Chama novo método no repositório
        log.info("Encontrados {} Apoios Utilizados para o colaborador {}.", apoios.size(), colaboradorId);
        return apoios.stream()
                .map(ApoioUtilizadoResponseDTO::new)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID E COLABORADOR (SUBSTITUI buscarPorId)
    public ApoioUtilizadoResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        log.info("Buscando Apoio Utilizado ID {} para o colaborador {}.", id, colaboradorId);
        ApoioUtilizado apoio = repository.findByIdAndColaboradorId(id, colaboradorId) // Chama novo método no repositório
                .orElseThrow(() -> {
                    log.warn("Apoio Utilizado ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador");
                });
        log.info("Apoio Utilizado ID {} encontrado com sucesso para o colaborador {}.", id, colaboradorId);
        return new ApoioUtilizadoResponseDTO(apoio);
    }

    // MÉTODO ATUALIZAR REVISADO PARA INCLUIR colaboradorId
    public ApoioUtilizadoResponseDTO atualizar(String id, String colaboradorId, ApoioUtilizadoRequestDTO dto) {
        log.info("Iniciando atualização do Apoio Utilizado ID {} para o colaborador {}.", id, colaboradorId);
        log.debug("DTO de atualização recebido: {}", dto);

        // Busca o apoio, garantindo que ele exista E pertença ao colaborador
        ApoioUtilizado apoio = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualização falhou: Apoio Utilizado ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador");
                });

        log.debug("Apoio Utilizado existente encontrado: {}. Aplicando atualizações.", apoio);
        apoio.setTipoApoio(dto.tipo());
        apoio.setDescricao(dto.descricao());
        // Não atualizamos o dataRegistro aqui, pois geralmente é um timestamp de criação.
        // Se precisar de um "dataAtualizacao", adicione um novo campo no model.

        ApoioUtilizado atualizado = repository.save(apoio);
        log.info("Apoio Utilizado ID {} atualizado com sucesso para o colaborador {}.", atualizado.getId(), colaboradorId);
        return new ApoioUtilizadoResponseDTO(atualizado);
    }

    // DELETAR REVISADO PARA INCLUIR colaboradorId
    public void deletar(String id, String colaboradorId) {
        log.warn("Iniciando deleção do Apoio Utilizado ID {} para o colaborador {}.", id, colaboradorId);

        // Primeiro, verifica se o apoio existe e pertence ao colaborador
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) { // Chama novo método no repositório
            log.warn("Tentativa de deleção falhou: Apoio Utilizado ID {} não encontrado ou não pertence ao colaborador {}. Lançando ResponseStatusException NOT_FOUND.", id, colaboradorId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
        log.info("Apoio Utilizado ID {} deletado com sucesso para o colaborador {}.", id, colaboradorId);
    }

}