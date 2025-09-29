package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumorRepository extends MongoRepository<Humor, String> {

    // Busca todos os registros de Humor associados a um dado colaboradorId
    List<Humor> findByColaboradorId(String colaboradorId);

    // Busca um registro de Humor pelo ID e verifica se ele pertence a um colaboradorId específico
    Optional<Humor> findByIdAndColaboradorId(String id, String colaboradorId);

    // Verifica se um registro de Humor com um dado ID existe e pertence a um colaboradorId específico
    boolean existsByIdAndColaboradorId(String id, String colaboradorId);
}