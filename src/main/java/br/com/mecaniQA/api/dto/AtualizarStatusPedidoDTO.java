package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusPedidoDTO {

    @NotNull(message = "O status é obrigatório")
    private StatusPedido status;

    public AtualizarStatusPedidoDTO() {
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}
