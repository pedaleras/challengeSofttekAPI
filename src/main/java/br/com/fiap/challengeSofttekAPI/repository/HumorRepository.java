package br.com.fiap.challengeSofttekAPI.repository;


import br.com.fiap.challengeSofttekAPI.model.Humor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HumorRepository extends JpaRepository<Humor, Long> {
}

