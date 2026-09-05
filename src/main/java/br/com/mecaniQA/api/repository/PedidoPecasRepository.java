package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.enums.StatusPedido;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.PedidoPecas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PedidoPecasRepository {

    // ===================== ATRIBUTO ESTÁTICO (Singleton) =====================
    private static PedidoPecasRepository instance;

    // ===================== ATRIBUTOS =====================
    private List<PedidoPecas> pedidos;
    private final AtomicLong contadorId = new AtomicLong(0);

    // ===================== CONSTRUTOR PRIVADO =====================
    private PedidoPecasRepository() {
        pedidos = new ArrayList<>();
    }

    // ===================== ACESSO À INSTÂNCIA =====================
    public static PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    // ===================== CRUD - CREATE =====================
    public PedidoPecas salvar(PedidoPecas pedido) {
        pedido.setCodigoPedido(contadorId.incrementAndGet());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidos.add(pedido);
        return pedido;
    }

    // ===================== CRUD - READ =====================
    public List<PedidoPecas> listarTodos() {
        return pedidos;
    }

    public PedidoPecas buscarPorId(long codigoPedido) {
        for (PedidoPecas pedido : pedidos) {
            if (pedido.getCodigoPedido() == codigoPedido) {
                return pedido;
            }
        }
        return null;
    }

    // ===================== UPDATE - ADICIONAR ITEM =====================
    public PedidoPecas adicionarItem(long codigoPedido, ItemPedido item) {
        PedidoPecas pedido = buscarPorId(codigoPedido);
        if (pedido == null) {
            return null;
        }
        pedido.adicionarItem(item);
        pedido.setDataAtualizacao(LocalDateTime.now());
        return pedido;
    }

    // ===================== UPDATE - STATUS =====================
    public PedidoPecas atualizarStatus(long codigoPedido, StatusPedido novoStatus) {
        PedidoPecas pedido = buscarPorId(codigoPedido);
        if (pedido == null) {
            return null;
        }
        pedido.setStatus(novoStatus);
        pedido.setDataAtualizacao(LocalDateTime.now());
        return pedido;
    }

    // ===================== CRUD - DELETE =====================
    public boolean deletar(long codigoPedido) {
        PedidoPecas pedido = buscarPorId(codigoPedido);
        if (pedido == null) {
            return false;
        }
        pedidos.remove(pedido);
        return true;
    }
}
