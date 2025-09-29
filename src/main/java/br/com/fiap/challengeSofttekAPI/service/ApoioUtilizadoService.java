package br.com.fiap.challengeSofttekAPI.service;

import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoRequestDTO;
import br.com.fiap.challengeSofttekAPI.dto.ApoioUtilizadoResponseDTO;
import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import br.com.fiap.challengeSofttekAPI.repository.ApoioUtilizadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; // Import adicionado

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApoioUtilizadoService {

    @Autowired
    private ApoioUtilizadoRepository repository;

    // SALVAR ATUALIZADO PARA INCLUIR colaboradorId
    public ApoioUtilizadoResponseDTO salvar(String colaboradorId, ApoioUtilizadoRequestDTO dto) {
        // Cria um novo ApoioUtilizado, passando o colaboradorId
        ApoioUtilizado apoio = new ApoioUtilizado(colaboradorId, dto.tipo(), dto.descricao());
        apoio.setDataRegistro(LocalDateTime.now()); // Garante que a data de registro seja setada

        ApoioUtilizado salvo = repository.save(apoio);
        return new ApoioUtilizadoResponseDTO(salvo);
    }

    // LISTAR APOIOS POR COLABORADOR (SUBSTITUI listarTodos)
    public List<ApoioUtilizadoResponseDTO> listarPorColaborador(String colaboradorId) {
        return repository.findByColaboradorId(colaboradorId) // Chama novo método no repositório
                .stream()
                .map(ApoioUtilizadoResponseDTO::new)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID E COLABORADOR (SUBSTITUI buscarPorId)
    public ApoioUtilizadoResponseDTO buscarPorIdEColaborador(String id, String colaboradorId) {
        ApoioUtilizado apoio = repository.findByIdAndColaboradorId(id, colaboradorId) // Chama novo método no repositório
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador")); // Lança exceção 404
        return new ApoioUtilizadoResponseDTO(apoio);
    }

    // MÉTODO ATUALIZAR REVISADO PARA INCLUIR colaboradorId
    public ApoioUtilizadoResponseDTO atualizar(String id, String colaboradorId, ApoioUtilizadoRequestDTO dto) {
        // Busca o apoio, garantindo que ele exista E pertença ao colaborador
        ApoioUtilizado apoio = repository.findByIdAndColaboradorId(id, colaboradorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador"));

        apoio.setTipoApoio(dto.tipo());
        apoio.setDescricao(dto.descricao());
        // Não atualizamos o dataRegistro aqui, pois geralmente é um timestamp de criação.
        // Se precisar de um "dataAtualizacao", adicione um novo campo no model.

        ApoioUtilizado atualizado = repository.save(apoio);
        return new ApoioUtilizadoResponseDTO(atualizado);
    }

    // DELETAR REVISADO PARA INCLUIR colaboradorId
    public void deletar(String id, String colaboradorId) {
        // Primeiro, verifica se o apoio existe e pertence ao colaborador
        if (!repository.existsByIdAndColaboradorId(id, colaboradorId)) { // Chama novo método no repositório
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Apoio não encontrado ou não pertence ao colaborador");
        }
        repository.deleteById(id);
    }

}