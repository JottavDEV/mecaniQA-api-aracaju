package br.com.mecaniQA.api.model;

import br.com.mecaniQA.api.enums.Categorias;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pecas")
public class Peca {

    // ===================== ATRIBUTOS =====================
    private long codigoSKU;
    private String nome;
    private long codigobarras;
    private String fornecedor;
    private int quantidade;
    private double precoCusto;
    private double precoVenda;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String tamanho;
    private String cor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categorias categoriaPeca;

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

    public Categorias getCategoriaPeca() {
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

    public void setCategoriaPeca(Categorias novaCategoriaPeca) {
        categoriaPeca = novaCategoriaPeca;
    }

}