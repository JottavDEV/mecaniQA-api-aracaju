package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ItemPedidoRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasRequestDTO;
import br.com.mecaniQA.api.enums.StatusPedido;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.exception.PedidoPecasNaoEncontradoException;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoPecasService {

    private final PedidoPecasRepository repository;
    private final PecaRepository pecaRepository;

    public PedidoPecasService(PedidoPecasRepository repository, PecaRepository pecaRepository) {
        this.repository = repository;
        this.pecaRepository = pecaRepository;
    }

    // ===================== CREATE (US03) =====================
    public PedidoPecas criar(PedidoPecasRequestDTO dto) {
        if (dto == null || dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new DadosInvalidosException("O pedido precisa ter ao menos um item");
        }

        PedidoPecas pedido = new PedidoPecas();
        for (ItemPedidoRequestDTO itemDTO : dto.getItens()) {
            pedido.adicionarItem(montarItem(itemDTO));
        }

        return repository.save(pedido);
    }

    // ===================== READ =====================
    public List<PedidoPecas> listarTodos() {
        return repository.findAll();
    }

    public PedidoPecas buscarPorId(long codigoPedido) {
        return repository.findById(codigoPedido)
                .orElseThrow(() -> new PedidoPecasNaoEncontradoException(codigoPedido));
    }

    // ===================== UPDATE - ADICIONAR ITEM (US04) =====================
    public PedidoPecas adicionarItem(long codigoPedido, ItemPedidoRequestDTO dto) {
        PedidoPecas pedido = buscarPorId(codigoPedido);
        pedido.adicionarItem(montarItem(dto));
        return repository.update(pedido);
    }

    // ===================== UPDATE - STATUS (US05) =====================
    public PedidoPecas atualizarStatus(long codigoPedido, StatusPedido novoStatus) {
        if (novoStatus == null) {
            throw new DadosInvalidosException("O novo status é obrigatório");
        }
        PedidoPecas pedido = buscarPorId(codigoPedido);
        pedido.setStatus(novoStatus);
        return repository.update(pedido);
    }

    // ===================== DELETE =====================
    public void deletar(long codigoPedido) {
        if (!repository.existsById(codigoPedido)) {
            throw new PedidoPecasNaoEncontradoException(codigoPedido);
        }
        repository.deleteById(codigoPedido);
    }

    // ===================== AUXILIAR =====================
    private ItemPedido montarItem(ItemPedidoRequestDTO dto) {
        if (dto == null || dto.getCodigoSKU() == null) {
            throw new DadosInvalidosException("O código SKU da peça é obrigatório");
        }
        if (dto.getQuantidade() <= 0) {
            throw new DadosInvalidosException("A quantidade deve ser maior que zero");
        }
        Long codigoSKU = dto.getCodigoSKU();
        Peca peca = pecaRepository.findById(codigoSKU)
                .orElseThrow(() -> new PecaNaoEncontradaException(codigoSKU));
        return new ItemPedido(peca, dto.getQuantidade());
    }
}
