package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoFactory;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.ServicoNaoEncontradoException;
import br.com.mecaniQA.api.mapper.ConversorData;
import br.com.mecaniQA.api.mapper.ServicoResultSetMapper;
import br.com.mecaniQA.api.model.Servico;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ServicoRepository implements Repository<Servico, Long> {

    // ===================== SQL =====================
    private static final String INSERT =
            "INSERT INTO servico (nome_servico, descricao_servico, tempo_estimado_minutos, "
                    + "custo_tabelado, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_POR_ID = "SELECT * FROM servico WHERE codigo_servico = ?";

    private static final String SELECT_TODOS = "SELECT * FROM servico ORDER BY codigo_servico";

    private static final String UPDATE =
            "UPDATE servico SET nome_servico = ?, descricao_servico = ?, tempo_estimado_minutos = ?, "
                    + "custo_tabelado = ?, data_atualizacao = ? WHERE codigo_servico = ?";

    private static final String DELETE = "DELETE FROM servico WHERE codigo_servico = ?";

    private static final String EXISTE = "SELECT 1 FROM servico WHERE codigo_servico = ?";

    // ===================== DEPENDÊNCIAS =====================
    private final ConexaoFactory conexaoFactory;
    private final ServicoResultSetMapper mapper = new ServicoResultSetMapper();

    public ServicoRepository(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // ===================== CREATE =====================
    @Override
    public Servico save(Servico servico) {
        LocalDateTime agora = LocalDateTime.now();
        servico.setDataCriacao(agora);
        servico.setDataAtualizacao(agora);

        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, servico.getNomeServico());
            ps.setString(2, servico.getDescricaoServico());
            ps.setInt(3, servico.getTempoEstimadoMinutos());
            ps.setDouble(4, servico.getCustoTabelado());
            ps.setTimestamp(5, ConversorData.paraTimestamp(servico.getDataCriacao()));
            ps.setTimestamp(6, ConversorData.paraTimestamp(servico.getDataAtualizacao()));
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    servico.setCodigoServico(chaves.getLong(1));
                }
            }
            return servico;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar o serviço", e);
        }
    }

    // ===================== READ =====================
    @Override
    public Optional<Servico> findById(Long codigoServico) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_POR_ID)) {

            ps.setLong(1, codigoServico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapear(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar o serviço " + codigoServico, e);
        }
    }

    @Override
    public List<Servico> findAll() {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            List<Servico> servicos = new ArrayList<>();
            while (rs.next()) {
                servicos.add(mapper.mapear(rs));
            }
            return servicos;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar os serviços", e);
        }
    }

    // ===================== UPDATE =====================
    @Override
    public Servico update(Servico servico) {
        servico.setDataAtualizacao(LocalDateTime.now());

        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(UPDATE)) {

            ps.setString(1, servico.getNomeServico());
            ps.setString(2, servico.getDescricaoServico());
            ps.setInt(3, servico.getTempoEstimadoMinutos());
            ps.setDouble(4, servico.getCustoTabelado());
            ps.setTimestamp(5, ConversorData.paraTimestamp(servico.getDataAtualizacao()));
            ps.setLong(6, servico.getCodigoServico());

            if (ps.executeUpdate() == 0) {
                throw new ServicoNaoEncontradoException(servico.getCodigoServico());
            }
            return servico;
        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao atualizar o serviço " + servico.getCodigoServico(), e);
        }
    }

    // ===================== DELETE =====================
    @Override
    public void deleteById(Long codigoServico) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(DELETE)) {

            ps.setLong(1, codigoServico);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DadosInvalidosException(
                    "O serviço " + codigoServico + " não pode ser removido porque está em uma ordem de serviço");
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover o serviço " + codigoServico, e);
        }
    }

    // ===================== EXISTS =====================
    @Override
    public boolean existsById(Long codigoServico) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(EXISTE)) {

            ps.setLong(1, codigoServico);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao verificar o serviço " + codigoServico, e);
        }
    }
}
