package com.ProjetoIntegrado.DependancyHeaven.web;

import com.ProjetoIntegrado.DependancyHeaven.domain.Estado;
import com.ProjetoIntegrado.DependancyHeaven.dto.DependenciaRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaEstadoRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaResponse;
import com.ProjetoIntegrado.DependancyHeaven.service.TarefaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping("/templates/{templateId}/tarefas")
    public ResponseEntity<TarefaResponse> criarTarefa(
            @PathVariable Long templateId,
            @RequestBody TarefaRequest request) {
        TarefaResponse response = tarefaService.criarTarefa(templateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponse> atualizarTarefa(
            @PathVariable Long id,
            @RequestBody TarefaRequest request) {
        TarefaResponse response = tarefaService.atualizarTarefa(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tarefas/{id}/estado")
    public ResponseEntity<TarefaResponse> alterarEstado(
            @PathVariable Long id,
            @RequestBody TarefaEstadoRequest request) {
        Estado novoEstado = Estado.valueOf(request.getEstado());
        TarefaResponse response = tarefaService.alterarEstado(id, novoEstado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tarefas/{id}/dependencias")
    public ResponseEntity<TarefaResponse> adicionarDependencia(
            @PathVariable Long id,
            @RequestBody DependenciaRequest request) {
        TarefaResponse response = tarefaService.adicionarDependencia(id, request.getDependenciaId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tarefas/{id}/dependencias/{dependenciaId}")
    public ResponseEntity<Void> removerDependencia(
            @PathVariable Long id,
            @PathVariable Long dependenciaId) {
        tarefaService.removerDependencia(id, dependenciaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tarefas/{id}")
    public ResponseEntity<Void> excluirTarefa(@PathVariable Long id) {
        tarefaService.excluirTarefa(id);
        return ResponseEntity.noContent().build();
    }
}
