package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PecaService {

    private final PecaRepository repository;

    public PecaService(PecaRepository repository) {
        this.repository = repository;
    }

    public Peca salvar(PecaRequestDTO dto) {
        Peca peca = PecaMapper.paraModel(validarDTO(dto));
        validar(peca);
        return repository.save(peca);
    }

    public List<Peca> listarTodas() {
        return repository.findAll();
    }

    public Peca buscarPorId(long codigoSKU) {
        return repository.findById(codigoSKU)
                .orElseThrow(() -> new PecaNaoEncontradaException(codigoSKU));
    }

    public Peca atualizar(long codigoSKU, PecaRequestDTO dto) {
        Peca dados = PecaMapper.paraModel(validarDTO(dto));
        validar(dados);
        Peca peca = buscarPorId(codigoSKU);

        peca.setNome(dados.getNome());
        peca.setCodigobarras(dados.getCodigobarras());
        peca.setFornecedor(dados.getFornecedor());
        peca.setQuantidade(dados.getQuantidade());
        peca.setPrecoCusto(dados.getPrecoCusto());
        peca.setPrecoVenda(dados.getPrecoVenda());
        peca.setTamanho(dados.getTamanho());
        peca.setCor(dados.getCor());
        peca.setCategoriaPeca(dados.getCategoriaPeca());

        return repository.update(peca);
    }

    public void deletar(long codigoSKU) {
        if (!repository.existsById(codigoSKU)) {
            throw new PecaNaoEncontradaException(codigoSKU);
        }
        repository.deleteById(codigoSKU);
    }

    // ===================== VALIDAÇÃO =====================
    private PecaRequestDTO validarDTO(PecaRequestDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Os dados da peça são obrigatórios");
        }
        return dto;
    }

    private void validar(Peca peca) {
        if (peca == null) {
            throw new DadosInvalidosException("Os dados da peça são obrigatórios");
        }
        if (peca.getNome() == null || peca.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da peça é obrigatório");
        }
        if (peca.getFornecedor() == null || peca.getFornecedor().isBlank()) {
            throw new DadosInvalidosException("O fornecedor é obrigatório");
        }
        if (peca.getCategoriaPeca() == null) {
            throw new DadosInvalidosException("A categoria da peça é obrigatória");
        }
    }
}
