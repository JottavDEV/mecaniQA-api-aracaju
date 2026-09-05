package br.com.mecaniQA.api.model;

import br.com.mecaniQA.api.enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoPecas {

    // ===================== ATRIBUTOS =====================
    private long codigoPedido;
    private List<ItemPedido> itens;
    private StatusPedido status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // ===================== CONSTRUTOR =====================
    public PedidoPecas() {
        this.itens = new ArrayList<>();
        this.status = StatusPedido.ORCANDO;
    }

    // ===================== GETTERS =====================
    public long getCodigoPedido() {
        return codigoPedido;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // ===================== SETTERS =====================
    public void setCodigoPedido(long codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // ===================== REGRAS =====================
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
    }

    public double getValorTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }
}
