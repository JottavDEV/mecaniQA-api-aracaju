package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.enums.CategoriaPeca;
import br.com.mecaniQA.api.model.Peca;

import java.sql.ResultSet;
import java.sql.SQLException;

// ResultSet (tabela peca) -> Peca
public class PecaResultSetMapper implements ResultSetMapper<Peca> {

    @Override
    public Peca mapear(ResultSet rs) throws SQLException {
        Peca peca = new Peca();
        peca.setCodigoSKU(rs.getLong("codigo_sku"));
        peca.setNome(rs.getString("nome"));
        peca.setCodigobarras(rs.getLong("codigo_barras"));
        peca.setFornecedor(rs.getString("fornecedor"));
        peca.setQuantidade(rs.getInt("quantidade"));
        peca.setPrecoCusto(rs.getDouble("preco_custo"));
        peca.setPrecoVenda(rs.getDouble("preco_venda"));
        peca.setTamanho(rs.getString("tamanho"));
        peca.setCor(rs.getString("cor"));

        String categoria = rs.getString("categoria");
        if (categoria != null) {
            peca.setCategoriaPeca(CategoriaPeca.valueOf(categoria));
        }

        peca.setDataCadastro(ConversorData.paraLocalDateTime(rs.getTimestamp("data_cadastro")));
        peca.setDataAtualizacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_atualizacao")));
        return peca;
    }
}
