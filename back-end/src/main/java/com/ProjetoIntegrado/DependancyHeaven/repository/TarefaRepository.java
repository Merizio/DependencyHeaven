package com.ProjetoIntegrado.DependancyHeaven.repository;

import com.ProjetoIntegrado.DependancyHeaven.domain.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    
    @Query("SELECT MAX(t.indiceLocal) FROM Tarefa t WHERE t.template.id = :templateId")
    Integer findMaxIndiceLocalByTemplateId(@Param("templateId") Long templateId);
}
