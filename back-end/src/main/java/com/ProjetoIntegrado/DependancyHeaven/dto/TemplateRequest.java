package com.ProjetoIntegrado.DependancyHeaven.dto;

public class TemplateRequest {

    private String nome;
    private Long usuarioId;

    public TemplateRequest() {}

    public TemplateRequest(String nome, Long usuarioId) {
        this.nome = nome;
        this.usuarioId = usuarioId;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
