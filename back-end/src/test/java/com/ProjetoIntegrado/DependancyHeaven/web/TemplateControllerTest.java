package com.ProjetoIntegrado.DependancyHeaven.web;

import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateDetalhadoResponse;
import com.ProjetoIntegrado.DependancyHeaven.dto.TemplateResponse;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaResponse;
import com.ProjetoIntegrado.DependancyHeaven.service.TemplateService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateService templateService;

    @Test
    void deveCriarTemplateERetornar201() throws Exception {
        when(templateService.criarTemplate(any()))
            .thenReturn(new TemplateResponse(1L, "Projeto de Migração"));

        String payload = """
            {"nome": "Projeto de Migração", "usuarioId": 1}
            """;

        mockMvc.perform(post("/api/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Projeto de Migração"));
    }

    @Test
    void deveBuscarTemplateComTarefasERetornar200() throws Exception {
        var tarefas = List.of(
            new TarefaResponse(10L, 1, "Configurar Banco", null, "PENDENTE",
                List.of(), List.of())
        );
        when(templateService.buscarPorId(1L))
            .thenReturn(new TemplateDetalhadoResponse(1L, "Projeto de Migração", tarefas));

        mockMvc.perform(get("/api/templates/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Projeto de Migração"))
            .andExpect(jsonPath("$.tarefas[0].id").value(10))
            .andExpect(jsonPath("$.tarefas[0].titulo").value("Configurar Banco"))
            .andExpect(jsonPath("$.tarefas[0].estado").value("PENDENTE"));
    }

    @Test
    void deveRetornar404QuandoTemplateNaoExiste() throws Exception {
        when(templateService.buscarPorId(999L))
            .thenThrow(new EntityNotFoundException("Template não encontrado com id: 999"));

        mockMvc.perform(get("/api/templates/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Template não encontrado com id: 999"));
    }

    @Test
    void deveListarTodosOsTemplatesERetornar200() throws Exception {
        when(templateService.listarTodos())
            .thenReturn(List.of(
                new TemplateResponse(1L, "Template 1"),
                new TemplateResponse(2L, "Template 2")
            ));

        mockMvc.perform(get("/api/templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Template 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].nome").value("Template 2"));
    }
}
