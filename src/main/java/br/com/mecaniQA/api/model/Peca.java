package br.com.mecaniQA.api.model;

import br.com.mecaniQA.api.enums.CategoriaPeca;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Entity
@Table(name = "pecas")
public class Peca {

    // ===================== ATRIBUTOS =====================
    private long codigoSKU;

    @NotBlank(message = "O nome da peça é obrigatório")
    private String nome;

    @Positive(message = "O código de barras deve ser um número positivo")
    private long codigobarras;

    @NotBlank(message = "O fornecedor é obrigatório")
    private String fornecedor;

    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    private int quantidade;

    @PositiveOrZero(message = "O preço de custo não pode ser negativo")
    private double precoCusto;

    @PositiveOrZero(message = "O preço de venda não pode ser negativo")
    private double precoVenda;

    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String tamanho;
    private String cor;

    @NotNull(message = "A categoria da peça é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaPeca categoriaPeca;

    // ===================== CONSTRUTOR =====================
    public Peca() {
    }

    // ===================== GETTERS =====================
    public long getCodigoSKU() {
        return codigoSKU;
    }

    public String getNome() {
        return nome;
    }

    public long getCodigobarras() {
        return codigobarras;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public String getCor() {
        return cor;
    }

    public CategoriaPeca getCategoriaPeca() {
        return categoriaPeca;
    }

    // ===================== SETTERS =====================
    public void setCodigoSKU(long novoCodigo) {
        codigoSKU = novoCodigo;
    }

    public void setNome(String novoNome) {
        nome = novoNome;
    }

    public void setCodigobarras(long novoCodigobarras) {
        codigobarras = novoCodigobarras;
    }

    public void setFornecedor(String novoFornecedor) {
        fornecedor = novoFornecedor;
    }

    public void setQuantidade(int novoQuantidade) {
        quantidade = novoQuantidade;
    }

    public void setPrecoCusto(double novoPrecoCusto) {
        precoCusto = novoPrecoCusto;
    }

    public void setPrecoVenda(double novoPrecoVenda) {
        precoVenda = novoPrecoVenda;
    }

    public void setDataCadastro(LocalDateTime novaDataCadastro) {
        dataCadastro = novaDataCadastro;
    }

    public void setDataAtualizacao(LocalDateTime novaDataAtualizacao) {
        dataAtualizacao = novaDataAtualizacao;
    }

    public void setTamanho(String novoTamanho) {
        tamanho = novoTamanho;
    }

    public void setCor(String novaCor) {
        cor = novaCor;
    }

    public void setCategoriaPeca(CategoriaPeca novaCategoriaPeca) {
        categoriaPeca = novaCategoriaPeca;
    }

}