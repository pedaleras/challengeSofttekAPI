package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRiscosRepository extends MongoRepository<AvaliacaoRiscos, String> {

    // Busca todas as AvaliacoesRiscos associadas a um dado colaboradorId
    List<AvaliacaoRiscos> findByColaboradorId(String colaboradorId);

    // Busca uma AvaliacaoRiscos pelo ID e verifica se ela pertence a um colaboradorId específico
    Optional<AvaliacaoRiscos> findByIdAndColaboradorId(String id, String colaboradorId);

    // Verifica se uma AvaliacaoRiscos com um dado ID existe e pertence a um colaboradorId específico
    boolean existsByIdAndColaboradorId(String id, String colaboradorId);
}