package com.ProjetoIntegrado.DependancyHeaven.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @ManyToMany
    @JoinTable(
        name = "tarefa_membro",
        joinColumns = @JoinColumn(name = "tarefa_id"),
        inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> membros = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "tarefa_dependencia",
        joinColumns = @JoinColumn(name = "tarefa_id"),
        inverseJoinColumns = @JoinColumn(name = "dependencia_id")
    )
    private List<Tarefa> dependencias = new ArrayList<>();

    @ManyToMany(mappedBy = "dependencias")
    private List<Tarefa> dependentes = new ArrayList<>();

    protected Tarefa() {
        // Construtor padrão exigido pelo JPA
    }

    public Tarefa(String titulo) {
        this.titulo = titulo;
    }

    // =========================================================================
    // Métodos de Negócio
    // =========================================================================

    /**
     * Adiciona uma dependência (pré-requisito) a esta tarefa.
     * Valida o DAG para impedir ciclos e ajusta o estado se necessário.
     */
    public void adicionarDependencia(Tarefa dependencia) {
        validarCiclo(dependencia);
        this.dependencias.add(dependencia);
        dependencia.dependentes.add(this);

        if (dependencia.getEstado() != Estado.FINALIZADO) {
            this.estado = Estado.BLOQUEADO;
        }
    }

    /**
     * Inicia a tarefa (muda para EM_ANDAMENTO).
     * Exige pelo menos um membro e que a tarefa não esteja bloqueada.
     */
    public void iniciar() {
        if (this.membros.isEmpty()) {
            throw new IllegalStateException(
                "A tarefa não pode ser iniciada sem pelo menos um membro associado.");
        }
        if (this.estado == Estado.BLOQUEADO) {
            throw new IllegalStateException(
                "A tarefa não pode ser iniciada enquanto estiver BLOQUEADA.");
        }
        this.estado = Estado.EM_ANDAMENTO;
    }

    /**
     * Finaliza a tarefa e propaga o desbloqueio para as tarefas dependentes.
     */
    public void finalizar() {
        this.estado = Estado.FINALIZADO;

        for (Tarefa dependente : this.dependentes) {
            if (dependente.todasDependenciasFinalizadas()) {
                dependente.estado = Estado.PENDENTE;
            }
        }
    }

    /**
     * Reabre a tarefa (volta para PENDENTE) e bloqueia automaticamente
     * todas as tarefas que dependem dela.
     */
    public void reabrir() {
        this.estado = Estado.PENDENTE;

        for (Tarefa dependente : this.dependentes) {
            dependente.estado = Estado.BLOQUEADO;
        }
    }

    // =========================================================================
    // Métodos Privados de Apoio
    // =========================================================================

    /**
     * Valida se adicionar {@code novaDependencia} criaria um ciclo no grafo.
     * Faz uma busca em profundidade (DFS) nas dependências de {@code novaDependencia}
     * verificando se alguma delas é {@code this}.
     */
    private void validarCiclo(Tarefa novaDependencia) {
        if (novaDependencia == this) {
            throw new IllegalStateException(
                "Uma tarefa não pode depender de si mesma.");
        }
        verificarCicloRecursivo(novaDependencia, this);
    }

    private void verificarCicloRecursivo(Tarefa atual, Tarefa alvo) {
        for (Tarefa dep : atual.dependencias) {
            if (dep == alvo) {
                throw new IllegalStateException(
                    "Dependência cíclica detectada! Adicionar esta dependência violaria a regra de DAG.");
            }
            verificarCicloRecursivo(dep, alvo);
        }
    }

    private boolean todasDependenciasFinalizadas() {
        for (Tarefa dep : this.dependencias) {
            if (dep.getEstado() != Estado.FINALIZADO) {
                return false;
            }
        }
        return true;
    }

    // =========================================================================
    // Getters e Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public void setMembros(List<Membro> membros) {
        this.membros = membros;
    }

    public List<Tarefa> getDependencias() {
        return dependencias;
    }

    public void setDependencias(List<Tarefa> dependencias) {
        this.dependencias = dependencias;
    }

    public List<Tarefa> getDependentes() {
        return dependentes;
    }
}
