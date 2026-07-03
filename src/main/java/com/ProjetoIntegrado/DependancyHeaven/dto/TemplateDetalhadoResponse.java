package com.ProjetoIntegrado.DependancyHeaven.dto;

import java.util.List;

public class TemplateDetalhadoResponse {

    private Long id;
    private String nome;
    private List<TarefaResponse> tarefas;

    public TemplateDetalhadoResponse() {}

    public TemplateDetalhadoResponse(Long id, String nome, List<TarefaResponse> tarefas) {
        this.id = id;
        this.nome = nome;
        this.tarefas = tarefas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<TarefaResponse> getTarefas() { return tarefas; }
    public void setTarefas(List<TarefaResponse> tarefas) { this.tarefas = tarefas; }
}
