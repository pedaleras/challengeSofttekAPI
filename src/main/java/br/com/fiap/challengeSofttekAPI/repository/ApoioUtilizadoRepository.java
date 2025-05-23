package br.com.fiap.challengeSofttekAPI.repository;

import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApoioUtilizadoRepository extends JpaRepository<ApoioUtilizado, Long> {
}
