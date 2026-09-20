package br.com.mecaniQA.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// DTO que o cliente envia no POST /api/ordens-servico.
// Não expõe codigoOS, status nem datas: quem gera isso é o servidor.
public class OrdemServicoRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String clienteNome;

    @NotBlank(message = "O veículo é obrigatório")
    private String veiculo;

    @NotBlank(message = "A descrição do problema é obrigatória")
    private String descricaoProblema;

    @NotEmpty(message = "A ordem de serviço precisa ter ao menos um serviço")
    private List<Long> codigosServicos;

    public OrdemServicoRequestDTO() {
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

    public List<Long> getCodigosServicos() {
        return codigosServicos;
    }

    public void setCodigosServicos(List<Long> codigosServicos) {
        this.codigosServicos = codigosServicos;
    }
}
