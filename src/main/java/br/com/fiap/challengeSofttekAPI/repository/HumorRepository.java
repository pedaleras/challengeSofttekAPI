package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumorRepository extends MongoRepository<Humor, String> {

    List<Humor> findAllByColaboradorId(String colaboradorId);

}