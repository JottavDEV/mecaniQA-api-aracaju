package br.com.mecaniQA.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PedidoPecasRequestDTO {

    @NotEmpty(message = "O pedido precisa ter ao menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;

    public PedidoPecasRequestDTO() {
    }

    public List<ItemPedidoRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequestDTO> itens) {
        this.itens = itens;
    }
}
