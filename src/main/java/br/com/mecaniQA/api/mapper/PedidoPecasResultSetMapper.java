package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.enums.StatusPedido;
import br.com.mecaniQA.api.model.PedidoPecas;

import java.sql.ResultSet;
import java.sql.SQLException;

// ResultSet (tabela pedido_pecas) -> PedidoPecas
// Só lê as colunas do próprio pedido; os itens vêm de outra consulta
// (tabela item_pedido), feita no repositório.
public class PedidoPecasResultSetMapper implements ResultSetMapper<PedidoPecas> {

    @Override
    public PedidoPecas mapear(ResultSet rs) throws SQLException {
        PedidoPecas pedido = new PedidoPecas();
        pedido.setCodigoPedido(rs.getLong("codigo_pedido"));
        pedido.setDataCriacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_criacao")));
        pedido.setDataAtualizacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_atualizacao")));

        String status = rs.getString("status");
        if (status != null) {
            pedido.setStatus(StatusPedido.valueOf(status));
        }

        return pedido;
    }
}
