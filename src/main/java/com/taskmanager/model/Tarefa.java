package com.taskmanager.model;

import java.time.LocalDate;

public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private String prioridade;
    private LocalDate dataCriacao;
    private boolean concluida;

    public Tarefa() {
        this.dataCriacao = LocalDate.now();
        this.concluida = false;
    }

    public Tarefa(String titulo, String descricao, String prioridade) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
    }

    public Tarefa(int id, String titulo, String descricao, String prioridade,
                  LocalDate dataCriacao, boolean concluida) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
        this.concluida = concluida;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }


    public void concluir() {
        this.concluida = true;
    }

    public boolean isValida() {
        return titulo != null && !titulo.isBlank()
                && prioridade != null && !prioridade.isBlank();
    }

    @Override
    public String toString() {
        return String.format("Tarefa{id=%d, titulo='%s', prioridade='%s', concluida=%b}",
                id, titulo, prioridade, concluida);
    }
}