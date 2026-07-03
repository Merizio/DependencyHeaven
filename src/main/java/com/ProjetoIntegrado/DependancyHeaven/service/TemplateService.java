package com.ProjetoIntegrado.DependancyHeaven.service;

import com.ProjetoIntegrado.DependancyHeaven.domain.Template;
import com.ProjetoIntegrado.DependancyHeaven.domain.Usuario;
import com.ProjetoIntegrado.DependancyHeaven.dto.MembroDto;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaResponse;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateDetalhadoResponse;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateResponse;
import com.ProjetoIntegrado.DependancyHeaven.repository.TemplateRepository;
import com.ProjetoIntegrado.DependancyHeaven.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UsuarioRepository usuarioRepository;

    public TemplateService(TemplateRepository templateRepository, UsuarioRepository usuarioRepository) {
        this.templateRepository = templateRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TemplateResponse criarTemplate(TemplateRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + request.getUsuarioId()));

        Template template = new Template(request.getNome());
        template.setUsuario(usuario);
        template = templateRepository.save(template);

        return new TemplateResponse(template.getId(), template.getNome());
    }

    @Transactional(readOnly = true)
    public TemplateDetalhadoResponse buscarPorId(Long id) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Template não encontrado com id: " + id));

        var tarefas = template.getTarefas().stream()
            .map(t -> new TarefaResponse(
                t.getId(),
                t.getTitulo(),
                t.getDescricao(),
                t.getEstado().name(),
                t.getMembros().stream()
                    .map(m -> new MembroDto(m.getNome()))
                    .collect(Collectors.toList()),
                t.getDependencias().stream()
                    .map(d -> d.getId())
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        return new TemplateDetalhadoResponse(template.getId(), template.getNome(), tarefas);
    }
}
