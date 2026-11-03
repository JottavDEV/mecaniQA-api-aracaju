package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.enums.StatusOS;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.OrdemServicoNaoEncontradaException;
import br.com.mecaniQA.api.exception.ServicoNaoEncontradoException;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final ServicoRepository servicoRepository;

    public OrdemServicoService(OrdemServicoRepository repository, ServicoRepository servicoRepository) {
        this.repository = repository;
        this.servicoRepository = servicoRepository;
    }

    // ===================== CREATE (US01) =====================
    public OrdemServico criar(OrdemServicoRequestDTO dto) {
        if (dto == null || dto.getCodigosServicos() == null || dto.getCodigosServicos().isEmpty()) {
            throw new DadosInvalidosException("A ordem de serviço precisa ter ao menos um serviço");
        }

        OrdemServico.Builder builder = OrdemServico.builder()
                .comCliente(dto.getClienteNome())
                .comVeiculo(dto.getVeiculo())
                .comDescricaoProblema(dto.getDescricaoProblema());

        for (Long codigoServico : dto.getCodigosServicos()) {
            if (codigoServico == null) {
                throw new DadosInvalidosException("O código de um dos serviços não foi informado");
            }
            Servico servico = servicoRepository.findById(codigoServico)
                    .orElseThrow(() -> new ServicoNaoEncontradoException(codigoServico));
            builder.adicionarServico(servico);
        }

        return repository.save(builder.build());
    }

    // ===================== READ =====================
    public List<OrdemServico> listarTodas() {
        return repository.findAll();
    }

    public OrdemServico buscarPorId(long codigoOS) {
        return repository.findById(codigoOS)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(codigoOS));
    }

    // ===================== UPDATE - STATUS (US02) =====================
    public OrdemServico atualizarStatus(long codigoOS, StatusOS novoStatus) {
        if (novoStatus == null) {
            throw new DadosInvalidosException("O novo status é obrigatório");
        }
        OrdemServico os = buscarPorId(codigoOS);
        os.setStatus(novoStatus);
        return repository.update(os);
    }

    // ===================== DELETE =====================
    public void deletar(long codigoOS) {
        if (!repository.existsById(codigoOS)) {
            throw new OrdemServicoNaoEncontradaException(codigoOS);
        }
        repository.deleteById(codigoOS);
    }
}
