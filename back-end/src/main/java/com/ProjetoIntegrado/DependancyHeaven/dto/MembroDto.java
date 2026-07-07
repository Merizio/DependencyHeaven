package com.ProjetoIntegrado.DependancyHeaven.dto;

public class MembroDto {

    private String nome;

    public MembroDto() {}

    public MembroDto(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
