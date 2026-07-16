package com.ProjetoIntegrado.DependancyHeaven.dto;

public class LoginResponse {
    private Long id;
    private String nome;
    private String email;

    public LoginResponse(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
