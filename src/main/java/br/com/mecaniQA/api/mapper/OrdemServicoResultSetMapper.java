package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.enums.StatusOS;
import br.com.mecaniQA.api.model.OrdemServico;

import java.sql.ResultSet;
import java.sql.SQLException;

// ResultSet (tabela ordem_servico) -> OrdemServico
// Só lê as colunas da própria OS; a lista de serviços vem de outra
// consulta (tabela ordem_servico_servico), feita no repositório.
public class OrdemServicoResultSetMapper implements ResultSetMapper<OrdemServico> {

    @Override
    public OrdemServico mapear(ResultSet rs) throws SQLException {
        OrdemServico.Builder builder = OrdemServico.builder()
                .comCodigoOS(rs.getLong("codigo_os"))
                .comCliente(rs.getString("cliente_nome"))
                .comVeiculo(rs.getString("veiculo"))
                .comDescricaoProblema(rs.getString("descricao_problema"))
                .comDataAbertura(ConversorData.paraLocalDateTime(rs.getTimestamp("data_abertura")))
                .comDataAtualizacao(ConversorData.paraLocalDateTime(rs.getTimestamp("data_atualizacao")));

        String status = rs.getString("status");
        if (status != null) {
            builder.comStatus(StatusOS.valueOf(status));
        }

        return builder.build();
    }
}
