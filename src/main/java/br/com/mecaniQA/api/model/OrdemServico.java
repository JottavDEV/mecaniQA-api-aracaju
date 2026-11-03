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
        LocalDateTime agora = LocalDateTime.now();
        this.codigoOS = builder.codigoOS;
        this.clienteNome = builder.clienteNome;
        this.veiculo = builder.veiculo;
        this.descricaoProblema = builder.descricaoProblema;
        this.servicos = builder.servicos;
        // Uma OS nova nasce ABERTO e com datas de "agora". Os valores só
        // vêm preenchidos no Builder quando o Mapper reconstrói uma OS
        // que já existe no banco.
        this.status = builder.status != null ? builder.status : StatusOS.ABERTO;
        this.dataAbertura = builder.dataAbertura != null ? builder.dataAbertura : agora;
        this.dataAtualizacao = builder.dataAtualizacao != null ? builder.dataAtualizacao : agora;
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

        private long codigoOS;
        private String clienteNome;
        private String veiculo;
        private String descricaoProblema;
        private List<Servico> servicos = new ArrayList<>();
        private StatusOS status;
        private LocalDateTime dataAbertura;
        private LocalDateTime dataAtualizacao;

        private Builder() {
            // privado: só se cria um Builder através de OrdemServico.builder()
        }

        // Usados pelo Mapper de ResultSet para reconstruir uma OS já salva.
        public Builder comCodigoOS(long codigoOS) {
            this.codigoOS = codigoOS;
            return this;
        }

        public Builder comStatus(StatusOS status) {
            this.status = status;
            return this;
        }

        public Builder comDataAbertura(LocalDateTime dataAbertura) {
            this.dataAbertura = dataAbertura;
            return this;
        }

        public Builder comDataAtualizacao(LocalDateTime dataAtualizacao) {
            this.dataAtualizacao = dataAtualizacao;
            return this;
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
