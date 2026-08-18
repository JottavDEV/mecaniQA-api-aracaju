package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.time.LocalDateTime;
import br.com.mecaniQA.api.model.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ServicoRepository {

    // ===================== ATRIBUTO ESTÁTICO (Singleton) =====================
    private static ServicoRepository instance;

    // ===================== ATRIBUTOS =====================
    private List<Servico> servicos;
    private final AtomicLong contadorId = new AtomicLong(0);

    // ===================== CONSTRUTOR PRIVADO =====================
    private ServicoRepository() {
        servicos = new ArrayList<>();
    }

    // ===================== ACESSO À INSTÂNCIA =====================
    public static ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    // ===================== MÉTODOS CRUD =====================
    // (adicionar, listar, buscar, atualizar, deletar)
    // CRUD - CREATE

    public Servico salvar(Servico servico) {
        servico.setCodigoServico(contadorId.incrementAndGet());
        servico.setDataCriacao(LocalDateTime.now());
        servico.setDataAtualizacao(LocalDateTime.now());
        servicos.add(servico);
        return servico;
    }

    // CRUD - READ
    // TODOS

    public List<Servico> getServicos() {
        return servicos;
    }

    // Listar por ID

    public Servico buscarPorID(long codigoServico) {
        for (Servico servico : servicos) {
            if (servico.getCodigoServico() == codigoServico) {
                return servico;
            }
        }
        return null;
    }

    // CRUD - UPDATE

    public Servico atualizar(long codigoServico, Servico servicoAtualizado) {
        Servico servico = buscarPorID(codigoServico);
        if (servico == null) {
            return null;
        }
        servico.setNomeServico(servicoAtualizado.getNomeServico());
        servico.setCustoTabelado(servicoAtualizado.getCustoTabelado());
        servico.setDataAtualizacao(LocalDateTime.now());
        servico.setDescricaoServico(servicoAtualizado.getDescricaoServico());
        servico.setTempoEstimadoMinutos(servicoAtualizado.getTempoEstimadoMinutos());
        return servico;
    }

    // CRUD - DELETE

    public boolean deletar(long codigoServico) {
        Servico servico = buscarPorID(codigoServico);
        if (servico == null) {
            return false;
        }
        servicos.remove(servico);
        return true;
    }

}