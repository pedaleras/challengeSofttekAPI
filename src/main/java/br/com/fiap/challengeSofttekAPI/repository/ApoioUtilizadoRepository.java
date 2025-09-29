package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApoioUtilizadoRepository extends MongoRepository<ApoioUtilizado, String> {

    // Busca todos os ApoiosUtilizados associados a um dado colaboradorId
    List<ApoioUtilizado> findByColaboradorId(String colaboradorId);

    // Busca um ApoioUtilizado pelo ID e verifica se ele pertence a um colaboradorId específico
    Optional<ApoioUtilizado> findByIdAndColaboradorId(String id, String colaboradorId);

    // Verifica se um ApoioUtilizado com um dado ID existe e pertence a um colaboradorId específico
    boolean existsByIdAndColaboradorId(String id, String colaboradorId);
}