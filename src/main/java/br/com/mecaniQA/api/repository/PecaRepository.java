package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PecaRepository {

    // ===================== ATRIBUTO ESTÁTICO (Singleton) =====================
    private static PecaRepository instance;

    // ===================== ATRIBUTOS =====================
    private List<Peca> pecas;
    private final AtomicLong contadorId = new AtomicLong(0);

    // ===================== CONSTRUTOR PRIVADO =====================
    private PecaRepository() {
        pecas = new ArrayList<>();
    }

    // ===================== ACESSO À INSTÂNCIA =====================
    public static PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    // ===================== MÉTODOS CRUD =====================
    // (adicionar, listar, buscar, atualizar, deletar)
    // CRUD - CREATE

    public Peca salvar(Peca peca) {
        peca.setCodigoSKU(contadorId.incrementAndGet());
        peca.setDataCadastro(LocalDateTime.now());
        peca.setDataAtualizacao(LocalDateTime.now());
        pecas.add(peca);
        return peca;
    }


    // CRUD - READ
    // TODAS

    public List<Peca> listarTodas() {

        return pecas;
    }

    // Listar por ID

    public Peca buscarPorId(long codigoSKU) {
        for (Peca peca : pecas) {
            if (peca.getCodigoSKU() == codigoSKU) {
                return peca;
            }

        }
        return null;
    }

    // CRUD - UPDATE

    public Peca atualizar(long codigoSKU, Peca pecaAtualizada) {
        Peca peca = buscarPorId(codigoSKU);
        if (peca == null) {
            return null;
        }
        peca.setNome(pecaAtualizada.getNome());
        peca.setCodigobarras(pecaAtualizada.getCodigobarras());
        peca.setFornecedor(pecaAtualizada.getFornecedor());
        peca.setQuantidade(pecaAtualizada.getQuantidade());
        peca.setPrecoCusto(pecaAtualizada.getPrecoCusto());
        peca.setPrecoVenda(pecaAtualizada.getPrecoVenda());
        peca.setTamanho(pecaAtualizada.getTamanho());
        peca.setCor(pecaAtualizada.getCor());
        peca.setCategoriaPeca(pecaAtualizada.getCategoriaPeca());
        peca.setDataAtualizacao(LocalDateTime.now());
        return peca;
    }

    // CRUD - DELETE

    public boolean deletar(long codigoSKU) {
        Peca peca = buscarPorId(codigoSKU);
        if (peca == null) {
            return false;
        }
        pecas.remove(peca);
        return true;
    }
}

