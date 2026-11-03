package br.com.mecaniQA.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// DTO que o cliente envia no POST/PUT /api/servicos.
// Não expõe codigoServico nem datas: quem gera isso é o servidor.
public class ServicoRequestDTO {

    @NotBlank(message = "O nome do serviço é obrigatório")
    private String nomeServico;

    @NotBlank(message = "A descrição do serviço é obrigatória")
    private String descricaoServico;

    @Positive(message = "O tempo estimado deve ser maior que zero")
    private int tempoEstimadoMinutos;

    @PositiveOrZero(message = "O custo tabelado não pode ser negativo")
    private double custoTabelado;

    public ServicoRequestDTO() {
    }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public void setTempoEstimadoMinutos(int tempoEstimadoMinutos) { this.tempoEstimadoMinutos = tempoEstimadoMinutos; }

    public double getCustoTabelado() { return custoTabelado; }
    public void setCustoTabelado(double custoTabelado) { this.custoTabelado = custoTabelado; }
}
