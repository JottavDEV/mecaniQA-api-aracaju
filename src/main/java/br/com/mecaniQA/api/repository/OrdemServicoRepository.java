package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoFactory;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.OrdemServicoNaoEncontradaException;
import br.com.mecaniQA.api.mapper.ConversorData;
import br.com.mecaniQA.api.mapper.OrdemServicoResultSetMapper;
import br.com.mecaniQA.api.mapper.ServicoResultSetMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OrdemServicoRepository implements Repository<OrdemServico, Long> {

    // ===================== SQL =====================
    private static final String INSERT_OS =
            "INSERT INTO ordem_servico (cliente_nome, veiculo, descricao_problema, status, "
                    + "data_abertura, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_OS_SERVICO =
            "INSERT INTO ordem_servico_servico (codigo_os, codigo_servico) VALUES (?, ?)";

    private static final String SELECT_POR_ID = "SELECT * FROM ordem_servico WHERE codigo_os = ?";

    private static final String SELECT_TODAS = "SELECT * FROM ordem_servico ORDER BY codigo_os";

    private static final String SELECT_SERVICOS_DA_OS =
            "SELECT s.* FROM servico s "
                    + "JOIN ordem_servico_servico oss ON oss.codigo_servico = s.codigo_servico "
                    + "WHERE oss.codigo_os = ? ORDER BY oss.id";

    private static final String UPDATE_OS =
            "UPDATE ordem_servico SET cliente_nome = ?, veiculo = ?, descricao_problema = ?, "
                    + "status = ?, data_atualizacao = ? WHERE codigo_os = ?";

    private static final String DELETE_SERVICOS_DA_OS =
            "DELETE FROM ordem_servico_servico WHERE codigo_os = ?";

    private static final String DELETE = "DELETE FROM ordem_servico WHERE codigo_os = ?";

    private static final String EXISTE = "SELECT 1 FROM ordem_servico WHERE codigo_os = ?";

    // ===================== DEPENDÊNCIAS =====================
    private final ConexaoFactory conexaoFactory;
    private final OrdemServicoResultSetMapper mapper = new OrdemServicoResultSetMapper();
    private final ServicoResultSetMapper servicoMapper = new ServicoResultSetMapper();

    public OrdemServicoRepository(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // ===================== CREATE =====================
    // A OS e as linhas da tabela associativa são gravadas na mesma
    // transação: ou grava tudo, ou nada.
    @Override
    public OrdemServico save(OrdemServico os) {
        try (Connection conexao = conexaoFactory.abrir()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(INSERT_OS, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, os.getClienteNome());
                    ps.setString(2, os.getVeiculo());
                    ps.setString(3, os.getDescricaoProblema());
                    ps.setString(4, os.getStatus().name());
                    ps.setTimestamp(5, ConversorData.paraTimestamp(os.getDataAbertura()));
                    ps.setTimestamp(6, ConversorData.paraTimestamp(os.getDataAtualizacao()));
                    ps.executeUpdate();

                    try (ResultSet chaves = ps.getGeneratedKeys()) {
                        if (chaves.next()) {
                            os.setCodigoOS(chaves.getLong(1));
                        }
                    }
                }
                inserirServicos(conexao, os);
                conexao.commit();
                return os;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar a ordem de serviço", e);
        }
    }

    // ===================== READ =====================
    @Override
    public Optional<OrdemServico> findById(Long codigoOS) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_POR_ID)) {

            ps.setLong(1, codigoOS);
            OrdemServico os = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    os = mapper.mapear(rs);
                }
            }
            if (os == null) {
                return Optional.empty();
            }
            carregarServicos(conexao, os);
            return Optional.of(os);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar a ordem de serviço " + codigoOS, e);
        }
    }

    @Override
    public List<OrdemServico> findAll() {
        try (Connection conexao = conexaoFactory.abrir()) {
            List<OrdemServico> ordens = new ArrayList<>();
            try (PreparedStatement ps = conexao.prepareStatement(SELECT_TODAS);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordens.add(mapper.mapear(rs));
                }
            }
            for (OrdemServico os : ordens) {
                carregarServicos(conexao, os);
            }
            return ordens;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar as ordens de serviço", e);
        }
    }

    // ===================== UPDATE =====================
    @Override
    public OrdemServico update(OrdemServico os) {
        os.setDataAtualizacao(LocalDateTime.now());

        try (Connection conexao = conexaoFactory.abrir()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(UPDATE_OS)) {
                    ps.setString(1, os.getClienteNome());
                    ps.setString(2, os.getVeiculo());
                    ps.setString(3, os.getDescricaoProblema());
                    ps.setString(4, os.getStatus().name());
                    ps.setTimestamp(5, ConversorData.paraTimestamp(os.getDataAtualizacao()));
                    ps.setLong(6, os.getCodigoOS());

                    if (ps.executeUpdate() == 0) {
                        conexao.rollback();
                        throw new OrdemServicoNaoEncontradaException(os.getCodigoOS());
                    }
                }
                try (PreparedStatement ps = conexao.prepareStatement(DELETE_SERVICOS_DA_OS)) {
                    ps.setLong(1, os.getCodigoOS());
                    ps.executeUpdate();
                }
                inserirServicos(conexao, os);
                conexao.commit();
                return os;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao atualizar a ordem de serviço " + os.getCodigoOS(), e);
        }
    }

    // ===================== DELETE =====================
    // As linhas de ordem_servico_servico caem junto (ON DELETE CASCADE no DDL).
    @Override
    public void deleteById(Long codigoOS) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(DELETE)) {

            ps.setLong(1, codigoOS);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover a ordem de serviço " + codigoOS, e);
        }
    }

    // ===================== EXISTS =====================
    @Override
    public boolean existsById(Long codigoOS) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(EXISTE)) {

            ps.setLong(1, codigoOS);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao verificar a ordem de serviço " + codigoOS, e);
        }
    }

    // ===================== AUXILIARES =====================
    private void inserirServicos(Connection conexao, OrdemServico os) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(INSERT_OS_SERVICO)) {
            for (Servico servico : os.getServicos()) {
                ps.setLong(1, os.getCodigoOS());
                ps.setLong(2, servico.getCodigoServico());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void carregarServicos(Connection conexao, OrdemServico os) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(SELECT_SERVICOS_DA_OS)) {
            ps.setLong(1, os.getCodigoOS());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    os.getServicos().add(servicoMapper.mapear(rs));
                }
            }
        }
    }
}
