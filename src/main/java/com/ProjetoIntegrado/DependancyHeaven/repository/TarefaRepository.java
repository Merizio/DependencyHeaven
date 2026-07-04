package com.ProjetoIntegrado.DependancyHeaven.repository;

import com.ProjetoIntegrado.DependancyHeaven.domain.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
