package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.enums.CategoriaPeca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// DTO que o cliente envia no POST/PUT /api/pecas.
// Não expõe codigoSKU nem datas: quem gera isso é o servidor.
public class PecaRequestDTO {

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

    private String tamanho;
    private String cor;

    @NotNull(message = "A categoria da peça é obrigatória")
    private CategoriaPeca categoriaPeca;

    public PecaRequestDTO() {
    }

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
}
