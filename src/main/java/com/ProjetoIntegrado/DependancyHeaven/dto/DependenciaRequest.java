package com.ProjetoIntegrado.DependancyHeaven.dto;

public class DependenciaRequest {

    private Long dependenciaId;

    public DependenciaRequest() {}

    public DependenciaRequest(Long dependenciaId) {
        this.dependenciaId = dependenciaId;
    }

    public Long getDependenciaId() { return dependenciaId; }
    public void setDependenciaId(Long dependenciaId) { this.dependenciaId = dependenciaId; }
}
