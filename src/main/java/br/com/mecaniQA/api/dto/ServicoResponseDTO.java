package br.com.mecaniQA.api.dto;

import java.time.LocalDateTime;

// DTO devolvido ao cliente nas rotas de /api/servicos.
public class ServicoResponseDTO {

    private long codigoServico;
    private String nomeServico;
    private String descricaoServico;
    private int tempoEstimadoMinutos;
    private double custoTabelado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ServicoResponseDTO() {
    }

    public long getCodigoServico() { return codigoServico; }
    public void setCodigoServico(long codigoServico) { this.codigoServico = codigoServico; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public void setTempoEstimadoMinutos(int tempoEstimadoMinutos) { this.tempoEstimadoMinutos = tempoEstimadoMinutos; }

    public double getCustoTabelado() { return custoTabelado; }
    public void setCustoTabelado(double custoTabelado) { this.custoTabelado = custoTabelado; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
