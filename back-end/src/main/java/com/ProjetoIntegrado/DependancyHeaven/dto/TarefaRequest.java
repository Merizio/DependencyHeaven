package com.ProjetoIntegrado.DependancyHeaven.dto;

import java.util.List;

public class TarefaRequest {

    private String titulo;
    private String descricao;
    private List<MembroDto> membros;

    public TarefaRequest() {}

    public TarefaRequest(String titulo, String descricao, List<MembroDto> membros) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.membros = membros;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<MembroDto> getMembros() { return membros; }
    public void setMembros(List<MembroDto> membros) { this.membros = membros; }
}
