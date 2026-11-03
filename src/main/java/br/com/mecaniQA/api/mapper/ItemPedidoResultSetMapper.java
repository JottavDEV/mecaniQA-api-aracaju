package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;

import java.sql.ResultSet;
import java.sql.SQLException;

// ResultSet (item_pedido JOIN peca) -> ItemPedido
// A consulta traz todas as colunas de peca (p.*) e a quantidade do item
// com o apelido "quantidade_item", porque peca também tem "quantidade".
public class ItemPedidoResultSetMapper implements ResultSetMapper<ItemPedido> {

    private final PecaResultSetMapper pecaMapper = new PecaResultSetMapper();

    @Override
    public ItemPedido mapear(ResultSet rs) throws SQLException {
        Peca peca = pecaMapper.mapear(rs);
        return new ItemPedido(peca, rs.getInt("quantidade_item"));
    }
}
