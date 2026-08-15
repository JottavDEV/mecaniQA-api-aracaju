package br.com.mecaniQA.api.model;

import br.com.mecaniQA.api.enums.Categorias;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="pecas")
public class Peca {

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


    public Peca() {
    }

    public long getCodigoSKU() {
        return codigoSKU;
    }

    public void setCodigoSKU(long novoCodigo) {
        codigoSKU = novoCodigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
         nome = novoNome;
    }

    public long getCodigobarras() {
        return codigobarras;
    }

    public void setCodigobarras(long novoCodigobarras) {
        codigobarras = novoCodigobarras;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String novoFornecedor) {
        fornecedor = novoFornecedor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int novoQuantidade) {
        quantidade = novoQuantidade;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(double novoPrecoCusto) {
        precoCusto = novoPrecoCusto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double novoPrecoVenda) {
        precoVenda = novoPrecoVenda;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime novaDataCadastro) {
        dataCadastro = novaDataCadastro;

    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime novaDataAtualizacao) {
        dataAtualizacao = novaDataAtualizacao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String novoTamanho) {
        tamanho = novoTamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String novaCor) {
        cor = novaCor;
    }
}

