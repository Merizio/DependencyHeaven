package com.ProjetoIntegrado.DependancyHeaven.web;

import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateDetalhadoResponse;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateResponse;
import com.ProjetoIntegrado.DependancyHeaven.service.TemplateService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> criarTemplate(@RequestBody TemplateRequest request) {
        TemplateResponse response = templateService.criarTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> listarTemplates() {
        List<TemplateResponse> response = templateService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDetalhadoResponse> buscarTemplate(@PathVariable Long id) {
        TemplateDetalhadoResponse response = templateService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTemplate(@PathVariable Long id) {
        templateService.excluirTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
