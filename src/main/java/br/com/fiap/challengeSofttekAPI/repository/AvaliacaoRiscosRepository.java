package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRiscosRepository extends JpaRepository<AvaliacaoRiscos, Long> {
}

