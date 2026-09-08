package br.com.mecaniQA.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemPedidoRequestDTO {

    @NotNull(message = "O código SKU da peça é obrigatório")
    private Long codigoSKU;

    @Positive(message = "A quantidade deve ser maior que zero")
    private int quantidade;

    public ItemPedidoRequestDTO() {
    }

    public Long getCodigoSKU() {
        return codigoSKU;
    }

    public void setCodigoSKU(Long codigoSKU) {
        this.codigoSKU = codigoSKU;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
