package br.com.mecaniQA.api.dto;

public class ItemPedidoResponseDTO {

    private long codigoSKU;
    private String nomePeca;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    public ItemPedidoResponseDTO() {
    }

    public long getCodigoSKU() {
        return codigoSKU;
    }

    public void setCodigoSKU(long codigoSKU) {
        this.codigoSKU = codigoSKU;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
