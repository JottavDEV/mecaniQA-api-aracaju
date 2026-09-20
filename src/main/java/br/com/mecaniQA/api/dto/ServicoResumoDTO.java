package br.com.mecaniQA.api.dto;

// Representação enxuta de um Servico dentro da resposta de uma OS
// (não precisa da descrição inteira, tempo estimado, etc.).
public class ServicoResumoDTO {

    private long codigoServico;
    private String nomeServico;
    private double custoTabelado;

    public ServicoResumoDTO() {
    }

    public long getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(long codigoServico) {
        this.codigoServico = codigoServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public double getCustoTabelado() {
        return custoTabelado;
    }

    public void setCustoTabelado(double custoTabelado) {
        this.custoTabelado = custoTabelado;
    }
}
