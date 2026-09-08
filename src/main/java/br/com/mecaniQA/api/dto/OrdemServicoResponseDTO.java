package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.enums.StatusOS;

import java.time.LocalDateTime;
import java.util.List;

// DTO que a API devolve para o cliente. Nunca devolvemos a entidade
// OrdemServico diretamente.
public class OrdemServicoResponseDTO {

    private long codigoOS;
    private String clienteNome;
    private String veiculo;
    private String descricaoProblema;
    private List<ServicoResumoDTO> servicos;
    private StatusOS status;
    private double valorTotal;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtualizacao;

    public OrdemServicoResponseDTO() {
    }

    public long getCodigoOS() {
        return codigoOS;
    }

    public void setCodigoOS(long codigoOS) {
        this.codigoOS = codigoOS;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public List<ServicoResumoDTO> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicoResumoDTO> servicos) {
        this.servicos = servicos;
    }

    public StatusOS getStatus() {
        return status;
    }

    public void setStatus(StatusOS status) {
        this.status = status;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
