package br.com.mecaniQA.api.model;

// ===================== ENTIDADE ASSOCIATIVA =====================
// Liga um Pedido de Peças a uma Peça, carregando o dado extra da
// relação (a quantidade), que não pertence nem à Peça nem ao Pedido.
public class ItemPedido {

    private Peca peca;
    private int quantidade;

    // ===================== CONSTRUTORES =====================
    public ItemPedido() {
    }

    public ItemPedido(Peca peca, int quantidade) {
        this.peca = peca;
        this.quantidade = quantidade;
    }

    // ===================== GETTERS =====================
    public Peca getPeca() {
        return peca;
    }

    public int getQuantidade() {
        return quantidade;
    }

    // ===================== SETTERS =====================
    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // ===================== REGRA =====================
    public double getSubtotal() {
        if (peca == null) {
            return 0;
        }
        return peca.getPrecoVenda() * quantidade;
    }
}
