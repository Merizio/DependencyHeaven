package com.ProjetoIntegrado.DependancyHeaven.repository;

import com.ProjetoIntegrado.DependancyHeaven.domain.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {
    Optional<Membro> findByNome(String nome);
}
