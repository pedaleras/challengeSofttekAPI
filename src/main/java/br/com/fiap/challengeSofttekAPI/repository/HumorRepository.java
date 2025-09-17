package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import org.springframework.data.mongodb.repository.MongoRepository; // Importar do Spring Data MongoDB
import org.springframework.stereotype.Repository;

@Repository
public interface HumorRepository extends MongoRepository<Humor, String> {
}