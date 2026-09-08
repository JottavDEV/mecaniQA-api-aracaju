package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.enums.StatusOS;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusOSDTO {

    @NotNull(message = "O status é obrigatório")
    private StatusOS status;

    public AtualizarStatusOSDTO() {
    }

    public StatusOS getStatus() {
        return status;
    }

    public void setStatus(StatusOS status) {
        this.status = status;
    }
}
