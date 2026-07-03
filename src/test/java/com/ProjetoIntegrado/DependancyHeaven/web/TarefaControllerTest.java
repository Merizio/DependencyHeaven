package com.ProjetoIntegrado.DependancyHeaven.web;

import com.ProjetoIntegrado.DependancyHeaven.domain.Estado;
import com.ProjetoIntegrado.DependancyHeaven.dto.MembroDto;
import com.ProjetoIntegrado.DependancyHeaven.dto.TarefaResponse;
import com.ProjetoIntegrado.DependancyHeaven.service.TarefaService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TarefaService tarefaService;

    private TarefaResponse tarefaResponsePadrao() {
        return new TarefaResponse(1L, "Configurar Banco", "Instalar PostgreSQL", "PENDENTE",
            List.of(new MembroDto("Davi")), List.of());
    }

    @Test
    void deveCriarTarefaERetornar201() throws Exception {
        when(tarefaService.criarTarefa(eq(1L), any()))
            .thenReturn(tarefaResponsePadrao());

        String payload = """
            {
                "titulo": "Configurar Banco",
                "descricao": "Instalar PostgreSQL",
                "membros": [{"nome": "Davi"}]
            }
            """;

        mockMvc.perform(post("/api/templates/1/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.titulo").value("Configurar Banco"))
            .andExpect(jsonPath("$.estado").value("PENDENTE"))
            .andExpect(jsonPath("$.membros[0].nome").value("Davi"));
    }

    @Test
    void deveAtualizarTarefaERetornar200() throws Exception {
        var response = new TarefaResponse(1L, "BD Atualizado", "PostgreSQL 15", "PENDENTE",
            List.of(new MembroDto("Davi"), new MembroDto("Paula")), List.of());

        when(tarefaService.atualizarTarefa(eq(1L), any())).thenReturn(response);

        String payload = """
            {
                "titulo": "BD Atualizado",
                "descricao": "PostgreSQL 15",
                "membros": [{"nome": "Davi"}, {"nome": "Paula"}]
            }
            """;

        mockMvc.perform(put("/api/tarefas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("BD Atualizado"))
            .andExpect(jsonPath("$.membros.length()").value(2));
    }

    @Test
    void deveAlterarEstadoERetornar200() throws Exception {
        var response = new TarefaResponse(1L, "Configurar Banco", null, "EM_ANDAMENTO",
            List.of(new MembroDto("Davi")), List.of());

        when(tarefaService.alterarEstado(eq(1L), eq(Estado.EM_ANDAMENTO))).thenReturn(response);

        String payload = """
            {"estado": "EM_ANDAMENTO"}
            """;

        mockMvc.perform(put("/api/tarefas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("EM_ANDAMENTO"));
    }

    @Test
    void deveRetornar400QuandoTransicaoInvalida() throws Exception {
        when(tarefaService.alterarEstado(eq(1L), eq(Estado.EM_ANDAMENTO)))
            .thenThrow(new IllegalStateException("A tarefa não pode ser iniciada sem pelo menos um membro associado."));

        String payload = """
            {"estado": "EM_ANDAMENTO"}
            """;

        mockMvc.perform(put("/api/tarefas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveAdicionarDependenciaERetornar200() throws Exception {
        var response = new TarefaResponse(1L, "Deploy", null, "BLOQUEADO",
            List.of(), List.of(2L));

        when(tarefaService.adicionarDependencia(eq(1L), eq(2L))).thenReturn(response);

        String payload = """
            {"dependenciaId": 2}
            """;

        mockMvc.perform(post("/api/tarefas/1/dependencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("BLOQUEADO"))
            .andExpect(jsonPath("$.dependenciasIds[0]").value(2));
    }

    @Test
    void deveRemoverDependenciaERetornar204() throws Exception {
        doNothing().when(tarefaService).removerDependencia(1L, 2L);

        mockMvc.perform(delete("/api/tarefas/1/dependencias/2"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404QuandoTarefaNaoExiste() throws Exception {
        when(tarefaService.atualizarTarefa(eq(999L), any()))
            .thenThrow(new EntityNotFoundException("Tarefa não encontrada com id: 999"));

        String payload = """
            {"titulo": "X", "descricao": "Y", "membros": []}
            """;

        mockMvc.perform(put("/api/tarefas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Tarefa não encontrada com id: 999"));
    }
}
