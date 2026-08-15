package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;

public class Servico {

    private long codigoServico;
    private String nomeServico;
    private String descricaoServico;
    private int tempoEstimadoMinutos;
    private double custoTabelado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;


    public Servico() {
    }

    public long getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(long novoCodigoServico) {
        codigoServico = novoCodigoServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String novoNomeServico) {
        nomeServico = novoNomeServico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String novoDescricao) {
        descricaoServico = novoDescricao;
    }

    public int getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public void setTempoEstimadoMinutos(int novoTempoEstimadoMinutos) {
        tempoEstimadoMinutos = novoTempoEstimadoMinutos;
    }

    public double getCustoTabelado() {
        return custoTabelado;
    }

    public void setCustoTabelado(double custoTabelado) {
        custoTabelado = custoTabelado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

}
