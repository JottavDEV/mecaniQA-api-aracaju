package br.com.mecaniQA.api.model;

import br.com.mecaniQA.api.enums.StatusOS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemServico {

    // ===================== ATRIBUTOS =====================
    private long codigoOS;
    private String clienteNome;
    private String veiculo;
    private String descricaoProblema;
    private List<Servico> servicos;
    private StatusOS status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtualizacao;

    // ===================== CONSTRUTOR PRIVADO =====================
    // Privado de propósito: a única forma de criar uma OrdemServico é
    // através de OrdemServico.builder()...build(). O construtor recebe
    // o próprio Builder e "copia" os valores acumulados nele.
    private OrdemServico(Builder builder) {
        this.clienteNome = builder.clienteNome;
        this.veiculo = builder.veiculo;
        this.descricaoProblema = builder.descricaoProblema;
        this.servicos = builder.servicos;
        this.status = StatusOS.ABERTO;
        this.dataAbertura = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ===================== PONTO DE ENTRADA DO BUILDER =====================
    public static Builder builder() {
        return new Builder();
    }

    // ===================== GETTERS =====================
    public long getCodigoOS() {
        return codigoOS;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public StatusOS getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // ===================== SETTERS =====================
    // Precisam continuar públicos porque o Repository é quem gera
    // o código (codigoOS) e atualiza a data quando o status muda.
    public void setCodigoOS(long codigoOS) {
        this.codigoOS = codigoOS;
    }

    public void setStatus(StatusOS status) {
        this.status = status;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // ===================== REGRA =====================
    public double getValorTotal() {
        double total = 0;
        for (Servico servico : servicos) {
            total += servico.getCustoTabelado();
        }
        return total;
    }

    // ===================== PADRÃO BUILDER =====================
    // Classe estática aninhada: acumula os dados passo a passo e só
    // "vira" uma OrdemServico de fato quando build() é chamado. Cada
    // método devolve "this" (o próprio Builder) para permitir o
    // encadeamento: builder().comCliente(...).comVeiculo(...).build()
    public static class Builder {

        private String clienteNome;
        private String veiculo;
        private String descricaoProblema;
        private List<Servico> servicos = new ArrayList<>();

        private Builder() {
            // privado: só se cria um Builder através de OrdemServico.builder()
        }

        public Builder comCliente(String clienteNome) {
            this.clienteNome = clienteNome;
            return this;
        }

        public Builder comVeiculo(String veiculo) {
            this.veiculo = veiculo;
            return this;
        }

        public Builder comDescricaoProblema(String descricaoProblema) {
            this.descricaoProblema = descricaoProblema;
            return this;
        }

        public Builder adicionarServico(Servico servico) {
            this.servicos.add(servico);
            return this;
        }

        public OrdemServico build() {
            return new OrdemServico(this);
        }
    }
}
