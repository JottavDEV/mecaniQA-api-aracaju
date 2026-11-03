package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoFactory;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.PedidoPecasNaoEncontradoException;
import br.com.mecaniQA.api.mapper.ConversorData;
import br.com.mecaniQA.api.mapper.ItemPedidoResultSetMapper;
import br.com.mecaniQA.api.mapper.PedidoPecasResultSetMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.PedidoPecas;
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
public class PedidoPecasRepository implements Repository<PedidoPecas, Long> {

    // ===================== SQL =====================
    private static final String INSERT_PEDIDO =
            "INSERT INTO pedido_pecas (status, data_criacao, data_atualizacao) VALUES (?, ?, ?)";

    private static final String INSERT_ITEM =
            "INSERT INTO item_pedido (codigo_pedido, codigo_sku, quantidade) VALUES (?, ?, ?)";

    private static final String SELECT_POR_ID = "SELECT * FROM pedido_pecas WHERE codigo_pedido = ?";

    private static final String SELECT_TODOS = "SELECT * FROM pedido_pecas ORDER BY codigo_pedido";

    // "quantidade_item" é apelido porque peca também tem uma coluna "quantidade".
    private static final String SELECT_ITENS_DO_PEDIDO =
            "SELECT p.*, i.quantidade AS quantidade_item FROM item_pedido i "
                    + "JOIN peca p ON p.codigo_sku = i.codigo_sku "
                    + "WHERE i.codigo_pedido = ? ORDER BY i.id";

    private static final String UPDATE_PEDIDO =
            "UPDATE pedido_pecas SET status = ?, data_atualizacao = ? WHERE codigo_pedido = ?";

    private static final String DELETE_ITENS_DO_PEDIDO =
            "DELETE FROM item_pedido WHERE codigo_pedido = ?";

    private static final String DELETE = "DELETE FROM pedido_pecas WHERE codigo_pedido = ?";

    private static final String EXISTE = "SELECT 1 FROM pedido_pecas WHERE codigo_pedido = ?";

    // ===================== DEPENDÊNCIAS =====================
    private final ConexaoFactory conexaoFactory;
    private final PedidoPecasResultSetMapper mapper = new PedidoPecasResultSetMapper();
    private final ItemPedidoResultSetMapper itemMapper = new ItemPedidoResultSetMapper();

    public PedidoPecasRepository(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // ===================== CREATE =====================
    // O pedido e seus itens são gravados na mesma transação.
    @Override
    public PedidoPecas save(PedidoPecas pedido) {
        LocalDateTime agora = LocalDateTime.now();
        pedido.setDataCriacao(agora);
        pedido.setDataAtualizacao(agora);

        try (Connection conexao = conexaoFactory.abrir()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(INSERT_PEDIDO, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, pedido.getStatus().name());
                    ps.setTimestamp(2, ConversorData.paraTimestamp(pedido.getDataCriacao()));
                    ps.setTimestamp(3, ConversorData.paraTimestamp(pedido.getDataAtualizacao()));
                    ps.executeUpdate();

                    try (ResultSet chaves = ps.getGeneratedKeys()) {
                        if (chaves.next()) {
                            pedido.setCodigoPedido(chaves.getLong(1));
                        }
                    }
                }
                inserirItens(conexao, pedido);
                conexao.commit();
                return pedido;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao salvar o pedido de peças", e);
        }
    }

    // ===================== READ =====================
    @Override
    public Optional<PedidoPecas> findById(Long codigoPedido) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(SELECT_POR_ID)) {

            ps.setLong(1, codigoPedido);
            PedidoPecas pedido = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedido = mapper.mapear(rs);
                }
            }
            if (pedido == null) {
                return Optional.empty();
            }
            carregarItens(conexao, pedido);
            return Optional.of(pedido);
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao buscar o pedido de peças " + codigoPedido, e);
        }
    }

    @Override
    public List<PedidoPecas> findAll() {
        try (Connection conexao = conexaoFactory.abrir()) {
            List<PedidoPecas> pedidos = new ArrayList<>();
            try (PreparedStatement ps = conexao.prepareStatement(SELECT_TODOS);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapper.mapear(rs));
                }
            }
            for (PedidoPecas pedido : pedidos) {
                carregarItens(conexao, pedido);
            }
            return pedidos;
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao listar os pedidos de peças", e);
        }
    }

    // ===================== UPDATE =====================
    // Atualiza o status/data e regrava os itens (apaga e reinsere), tudo
    // na mesma transação. Serve tanto para mudar status quanto para
    // adicionar um item ao pedido.
    @Override
    public PedidoPecas update(PedidoPecas pedido) {
        pedido.setDataAtualizacao(LocalDateTime.now());

        try (Connection conexao = conexaoFactory.abrir()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(UPDATE_PEDIDO)) {
                    ps.setString(1, pedido.getStatus().name());
                    ps.setTimestamp(2, ConversorData.paraTimestamp(pedido.getDataAtualizacao()));
                    ps.setLong(3, pedido.getCodigoPedido());

                    if (ps.executeUpdate() == 0) {
                        conexao.rollback();
                        throw new PedidoPecasNaoEncontradoException(pedido.getCodigoPedido());
                    }
                }
                try (PreparedStatement ps = conexao.prepareStatement(DELETE_ITENS_DO_PEDIDO)) {
                    ps.setLong(1, pedido.getCodigoPedido());
                    ps.executeUpdate();
                }
                inserirItens(conexao, pedido);
                conexao.commit();
                return pedido;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException(
                    "Erro ao atualizar o pedido de peças " + pedido.getCodigoPedido(), e);
        }
    }

    // ===================== DELETE =====================
    // Os itens caem junto (ON DELETE CASCADE no DDL).
    @Override
    public void deleteById(Long codigoPedido) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(DELETE)) {

            ps.setLong(1, codigoPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao remover o pedido de peças " + codigoPedido, e);
        }
    }

    // ===================== EXISTS =====================
    @Override
    public boolean existsById(Long codigoPedido) {
        try (Connection conexao = conexaoFactory.abrir();
             PreparedStatement ps = conexao.prepareStatement(EXISTE)) {

            ps.setLong(1, codigoPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErroBancoDadosException("Erro ao verificar o pedido de peças " + codigoPedido, e);
        }
    }

    // ===================== AUXILIARES =====================
    private void inserirItens(Connection conexao, PedidoPecas pedido) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(INSERT_ITEM)) {
            for (ItemPedido item : pedido.getItens()) {
                ps.setLong(1, pedido.getCodigoPedido());
                ps.setLong(2, item.getPeca().getCodigoSKU());
                ps.setInt(3, item.getQuantidade());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void carregarItens(Connection conexao, PedidoPecas pedido) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(SELECT_ITENS_DO_PEDIDO)) {
            ps.setLong(1, pedido.getCodigoPedido());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedido.getItens().add(itemMapper.mapear(rs));
                }
            }
        }
    }
}
