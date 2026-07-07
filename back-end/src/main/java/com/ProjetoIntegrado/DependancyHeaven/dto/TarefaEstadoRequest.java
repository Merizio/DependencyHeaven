package com.ProjetoIntegrado.DependancyHeaven.dto;

public class TarefaEstadoRequest {

    private String estado;

    public TarefaEstadoRequest() {}

    public TarefaEstadoRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
