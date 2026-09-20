package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.enums.StatusOS;
import br.com.mecaniQA.api.model.OrdemServico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrdemServicoRepository {

    // ===================== ATRIBUTO ESTÁTICO (Singleton) =====================
    private static OrdemServicoRepository instance;

    // ===================== ATRIBUTOS =====================
    private List<OrdemServico> ordensServico;
    private final AtomicLong contadorId = new AtomicLong(0);

    // ===================== CONSTRUTOR PRIVADO =====================
    private OrdemServicoRepository() {
        ordensServico = new ArrayList<>();
    }

    // ===================== ACESSO À INSTÂNCIA =====================
    public static OrdemServicoRepository getInstance() {
        if (instance == null) {
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    // ===================== CRUD - CREATE =====================
    public OrdemServico salvar(OrdemServico ordemServico) {
        ordemServico.setCodigoOS(contadorId.incrementAndGet());
        ordensServico.add(ordemServico);
        return ordemServico;
    }

    // ===================== CRUD - READ =====================
    public List<OrdemServico> listarTodas() {
        return ordensServico;
    }

    public OrdemServico buscarPorId(long codigoOS) {
        for (OrdemServico ordemServico : ordensServico) {
            if (ordemServico.getCodigoOS() == codigoOS) {
                return ordemServico;
            }
        }
        return null;
    }

    // ===================== CRUD - UPDATE (status) =====================
    public OrdemServico atualizarStatus(long codigoOS, StatusOS novoStatus) {
        OrdemServico ordemServico = buscarPorId(codigoOS);
        if (ordemServico == null) {
            return null;
        }
        ordemServico.setStatus(novoStatus);
        ordemServico.setDataAtualizacao(LocalDateTime.now());
        return ordemServico;
    }

    // ===================== CRUD - DELETE =====================
    public boolean deletar(long codigoOS) {
        OrdemServico ordemServico = buscarPorId(codigoOS);
        if (ordemServico == null) {
            return false;
        }
        ordensServico.remove(ordemServico);
        return true;
    }
}
