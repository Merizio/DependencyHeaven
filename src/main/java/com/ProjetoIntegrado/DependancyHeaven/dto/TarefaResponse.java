package com.ProjetoIntegrado.DependancyHeaven.dto;

import java.util.List;

public class TarefaResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private String estado;
    private List<MembroDto> membros;
    private List<Long> dependenciasIds;

    public TarefaResponse() {}

    public TarefaResponse(Long id, String titulo, String descricao, String estado,
                          List<MembroDto> membros, List<Long> dependenciasIds) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.estado = estado;
        this.membros = membros;
        this.dependenciasIds = dependenciasIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<MembroDto> getMembros() { return membros; }
    public void setMembros(List<MembroDto> membros) { this.membros = membros; }
    public List<Long> getDependenciasIds() { return dependenciasIds; }
    public void setDependenciasIds(List<Long> dependenciasIds) { this.dependenciasIds = dependenciasIds; }
}
