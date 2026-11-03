package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.enums.CategoriaPeca;

import java.time.LocalDateTime;

// DTO devolvido ao cliente nas rotas de /api/pecas.
public class PecaResponseDTO {

    private long codigoSKU;
    private String nome;
    private long codigobarras;
    private String fornecedor;
    private int quantidade;
    private double precoCusto;
    private double precoVenda;
    private String tamanho;
    private String cor;
    private CategoriaPeca categoriaPeca;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public PecaResponseDTO() {
    }

    public long getCodigoSKU() { return codigoSKU; }
    public void setCodigoSKU(long codigoSKU) { this.codigoSKU = codigoSKU; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public long getCodigobarras() { return codigobarras; }
    public void setCodigobarras(long codigobarras) { this.codigobarras = codigobarras; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public CategoriaPeca getCategoriaPeca() { return categoriaPeca; }
    public void setCategoriaPeca(CategoriaPeca categoriaPeca) { this.categoriaPeca = categoriaPeca; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
