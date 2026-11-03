package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoFactory;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.mapper.ConversorData;
import br.com.mecaniQA.api.mapper.PecaResultSetMapper;
import br.com.mecaniQA.api.model.Peca;
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
public class PecaRepository implements Repository<Peca, Long> {

    // ===================== SQL =====================
    private static final String INSERT =
            "INSERT INTO peca (nome, codigo_barras, fornecedor, quantidade, preco_custo, "
                    + "preco_venda, tamanho, cor, categoria, data_cadastro, data_atualizacao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_POR_ID = "SELECT * FROM peca WHERE codigo_sku = ?";

    private static final String SELECT_TODAS = "SELECT * FROM peca ORDER BY codigo_sku";

    private static final String UPDATE =
            "UPDATE peca SET nome = ?, codigo_barras = ?, fornecedor = ?, quantidade = ?, "
                    + "preco_custo = ?, preco_venda = ?, tamanho = ?, cor = ?, categoria = ?, "
                    + "data_atualizacao = ? WHERE codigo_sku = ?";

    private static final String DELETE = "DELETE FROM peca WHERE codigo_sku = ?";

    private static final String EXISTE = "SELECT 1 FROM peca WHERE codigo_sku = ?";

    // ===================== DEPENDÊNCIAS =====================
    private final ConexaoFactory conexaoFactory;
    private final PecaResultSetMapper mapper = new PecaResultSetMapper();

    public PecaRepository(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // ===================== CREATE =====================
    @Override
    public Peca save(Peca peca) {
        LocalDateTime agora = LocalDateTime.now();
        peca.setDataCadastro(agora);
        peca.setDataAtualizacao(agora);

        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            preencherCampos(ps, peca);
            ps.setTimestamp(10, ConversorData.paraTimestamp(peca.getDataCadastro()));
            ps.setTimestamp(11, ConversorData.paraTimestamp(peca.getDataAtualizacao()));
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    peca.setCodigoSKU(chaves.getLong(1));
                }
            }
            return peca;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar a peça", e);
        }
    }

    // ===================== READ =====================
    @Override
    public Optional<Peca> findById(Long codigoSKU) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_POR_ID)) {

            ps.setLong(1, codigoSKU);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapear(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar a peça " + codigoSKU, e);
        }
    }

    @Override
    public List<Peca> findAll() {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_TODAS);
             ResultSet rs = ps.executeQuery()) {

            List<Peca> pecas = new ArrayList<>();
            while (rs.next()) {
                pecas.add(mapper.mapear(rs));
            }
            return pecas;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar as peças", e);
        }
    }

    // ===================== UPDATE =====================
    @Override
    public Peca update(Peca peca) {
        peca.setDataAtualizacao(LocalDateTime.now());

        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(UPDATE)) {

            preencherCampos(ps, peca);
            ps.setTimestamp(10, ConversorData.paraTimestamp(peca.getDataAtualizacao()));
            ps.setLong(11, peca.getCodigoSKU());

            if (ps.executeUpdate() == 0) {
                throw new PecaNaoEncontradaException(peca.getCodigoSKU());
            }
            return peca;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao atualizar a peça " + peca.getCodigoSKU(), e);
        }
    }

    // ===================== DELETE =====================
    @Override
    public void deleteById(Long codigoSKU) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(DELETE)) {

            ps.setLong(1, codigoSKU);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DadosInvalidosException(
                    "A peça " + codigoSKU + " não pode ser removida porque está em um pedido");
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover a peça " + codigoSKU, e);
        }
    }

    // ===================== EXISTS =====================
    @Override
    public boolean existsById(Long codigoSKU) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(EXISTE)) {

            ps.setLong(1, codigoSKU);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao verificar a peça " + codigoSKU, e);
        }
    }

    // ===================== AUXILIAR =====================
    // Preenche os parâmetros 1 a 9, comuns ao INSERT e ao UPDATE.
    private void preencherCampos(PreparedStatement ps, Peca peca) throws SQLException {
        ps.setString(1, peca.getNome());
        ps.setLong(2, peca.getCodigobarras());
        ps.setString(3, peca.getFornecedor());
        ps.setInt(4, peca.getQuantidade());
        ps.setDouble(5, peca.getPrecoCusto());
        ps.setDouble(6, peca.getPrecoVenda());
        ps.setString(7, peca.getTamanho());
        ps.setString(8, peca.getCor());
        ps.setString(9, peca.getCategoriaPeca().name());
    }
}
