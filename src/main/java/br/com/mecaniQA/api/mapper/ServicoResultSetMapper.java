package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;

// ResultSet (tabela servico) -> Servico
public class ServicoResultSetMapper implements ResultSetMapper<Servico> {

    @Override
    public Servico mapear(ResultSet rs) throws SQLException {
        Servico servico = new Servico();
        servico.setCodigoServico(rs.getLong("codigo_servico"));
        servico.setNomeServico(rs.getString("nome_servico"));
        servico.setDescricaoServico(rs.getString("descricao_servico"));
        servico.setTempoEstimadoMinutos(rs.getInt("tempo_estimado_minutos"));
        servico.setCustoTabelado(rs.getDouble("custo_tabelado"));
        servico.setDataCriacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_criacao")));
        servico.setDataAtualizacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_atualizacao")));
        return servico;
    }
}
