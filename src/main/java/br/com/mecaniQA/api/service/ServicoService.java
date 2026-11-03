package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ServicoRequestDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ServicoNaoEncontradoException;
import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public Servico salvar(ServicoRequestDTO dto) {
        Servico servico = ServicoMapper.paraModel(validarDTO(dto));
        validar(servico);
        return repository.save(servico);
    }

    public List<Servico> listarTodos() {
        return repository.findAll();
    }

    public Servico buscarPorId(long codigoServico) {
        return repository.findById(codigoServico)
                .orElseThrow(() -> new ServicoNaoEncontradoException(codigoServico));
    }

    public Servico atualizar(long codigoServico, ServicoRequestDTO dto) {
        Servico dados = ServicoMapper.paraModel(validarDTO(dto));
        validar(dados);
        Servico servico = buscarPorId(codigoServico);

        servico.setNomeServico(dados.getNomeServico());
        servico.setDescricaoServico(dados.getDescricaoServico());
        servico.setTempoEstimadoMinutos(dados.getTempoEstimadoMinutos());
        servico.setCustoTabelado(dados.getCustoTabelado());

        return repository.update(servico);
    }

    public void deletar(long codigoServico) {
        if (!repository.existsById(codigoServico)) {
            throw new ServicoNaoEncontradoException(codigoServico);
        }
        repository.deleteById(codigoServico);
    }

    // ===================== VALIDAÇÃO =====================
    private ServicoRequestDTO validarDTO(ServicoRequestDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Os dados do serviço são obrigatórios");
        }
        return dto;
    }

    private void validar(Servico servico) {
        if (servico == null) {
            throw new DadosInvalidosException("Os dados do serviço são obrigatórios");
        }
        if (servico.getNomeServico() == null || servico.getNomeServico().isBlank()) {
            throw new DadosInvalidosException("O nome do serviço é obrigatório");
        }
        if (servico.getDescricaoServico() == null || servico.getDescricaoServico().isBlank()) {
            throw new DadosInvalidosException("A descrição do serviço é obrigatória");
        }
    }
}
