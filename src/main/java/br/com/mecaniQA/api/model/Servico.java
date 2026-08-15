package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;

public class Servico {

    // ===================== ATRIBUTOS =====================
    private long codigoServico;
    private String nomeServico;
    private String descricaoServico;
    private int tempoEstimadoMinutos;
    private double custoTabelado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // ===================== CONSTRUTOR =====================
    public Servico() {
    }

    // ===================== GETTERS =====================
    public long getCodigoServico() {
        return codigoServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public int getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public double getCustoTabelado() {
        return custoTabelado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // ===================== SETTERS =====================
    public void setCodigoServico(long novoCodigoServico) {
        codigoServico = novoCodigoServico;
    }

    public void setNomeServico(String novoNomeServico) {
        nomeServico = novoNomeServico;
    }

    public void setDescricaoServico(String novoDescricao) {
        descricaoServico = novoDescricao;
    }

    public void setTempoEstimadoMinutos(int novoTempoEstimadoMinutos) {
        tempoEstimadoMinutos = novoTempoEstimadoMinutos;
    }

    public void setCustoTabelado(double custoTabelado) {
        this.custoTabelado = custoTabelado;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

}