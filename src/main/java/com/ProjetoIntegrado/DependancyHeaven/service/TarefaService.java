package com.ProjetoIntegrado.DependancyHeaven.service;

import com.ProjetoIntegrado.DependancyHeaven.domain.Estado;
import com.ProjetoIntegrado.DependancyHeaven.domain.Membro;
import com.ProjetoIntegrado.DependancyHeaven.domain.Tarefa;
import com.ProjetoIntegrado.DependancyHeaven.domain.Template;
import com.ProjetoIntegrado.DependancyHeaven.dto.MembroDto;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaResponse;
import com.ProjetoIntegrado.DependancyHeaven.repository.MembroRepository;
import com.ProjetoIntegrado.DependancyHeaven.repository.TarefaRepository;
import com.ProjetoIntegrado.DependancyHeaven.repository.TemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final TemplateRepository templateRepository;
    private final MembroRepository membroRepository;

    public TarefaService(TarefaRepository tarefaRepository, TemplateRepository templateRepository,
                         MembroRepository membroRepository) {
        this.tarefaRepository = tarefaRepository;
        this.templateRepository = templateRepository;
        this.membroRepository = membroRepository;
    }

    @Transactional
    public TarefaResponse criarTarefa(Long templateId, TarefaRequest request) {
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new EntityNotFoundException("Template não encontrado com id: " + templateId));

        Tarefa tarefa = new Tarefa(request.getTitulo());
        
        Integer maxIndice = tarefaRepository.findMaxIndiceLocalByTemplateId(templateId);
        tarefa.setIndiceLocal(maxIndice == null ? 1 : maxIndice + 1);
        
        tarefa.setDescricao(request.getDescricao());
        template.adicionarTarefa(tarefa);

        if (request.getMembros() != null) {
            List<Membro> membros = resolverMembros(request.getMembros());
            tarefa.setMembros(membros);
        }

        tarefa = tarefaRepository.save(tarefa);
        return toResponse(tarefa);
    }

    @Transactional
    public TarefaResponse atualizarTarefa(Long id, TarefaRequest request) {
        Tarefa tarefa = buscarTarefaOuFalhar(id);

        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());

        if (request.getMembros() != null) {
            List<Membro> membros = resolverMembros(request.getMembros());
            tarefa.setMembros(membros);
        }

        tarefa = tarefaRepository.save(tarefa);
        return toResponse(tarefa);
    }

    @Transactional
    public TarefaResponse alterarEstado(Long id, Estado novoEstado) {
        Tarefa tarefa = buscarTarefaOuFalhar(id);

        switch (novoEstado) {
            case EM_ANDAMENTO -> tarefa.iniciar();
            case FINALIZADO -> tarefa.finalizar();
            case PENDENTE -> tarefa.reabrir();
            case BLOQUEADO -> throw new IllegalStateException(
                "O estado BLOQUEADO é gerenciado automaticamente pelo sistema de dependências.");
        }

        tarefa = tarefaRepository.save(tarefa);
        return toResponse(tarefa);
    }

    @Transactional
    public TarefaResponse adicionarDependencia(Long id, Long dependenciaId) {
        Tarefa tarefa = buscarTarefaOuFalhar(id);
        Tarefa dependencia = buscarTarefaOuFalhar(dependenciaId);

        tarefa.adicionarDependencia(dependencia);

        tarefa = tarefaRepository.save(tarefa);
        return toResponse(tarefa);
    }

    @Transactional
    public void removerDependencia(Long id, Long dependenciaId) {
        Tarefa tarefa = buscarTarefaOuFalhar(id);
        Tarefa dependencia = buscarTarefaOuFalhar(dependenciaId);

        tarefa.removerDependencia(dependencia);
        tarefaRepository.save(tarefa);
    }

    // =========================================================================
    // Métodos Privados
    // =========================================================================

    private Tarefa buscarTarefaOuFalhar(Long id) {
        return tarefaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com id: " + id));
    }

    /**
     * Busca membros pelo nome. Se não existir, cria um novo.
     */
    private List<Membro> resolverMembros(List<MembroDto> membrosDto) {
        List<Membro> membros = new ArrayList<>();
        for (MembroDto dto : membrosDto) {
            Membro membro = membroRepository.findByNome(dto.getNome())
                .orElseGet(() -> membroRepository.save(new Membro(dto.getNome())));
            membros.add(membro);
        }
        return membros;
    }

    private TarefaResponse toResponse(Tarefa tarefa) {
        return new TarefaResponse(
            tarefa.getId(),
            tarefa.getIndiceLocal(),
            tarefa.getTitulo(),
            tarefa.getDescricao(),
            tarefa.getEstado().name(),
            tarefa.getMembros().stream()
                .map(m -> new MembroDto(m.getNome()))
                .collect(Collectors.toList()),
            tarefa.getDependencias().stream()
                .map(d -> d.getId())
                .collect(Collectors.toList())
        );
    }
}
