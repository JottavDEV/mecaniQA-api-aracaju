package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ItemPedidoRequestDTO;
import br.com.mecaniQA.api.dto.ItemPedidoResponseDTO;
import br.com.mecaniQA.api.dto.PedidoPecasRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasResponseDTO;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.repository.PecaRepository;

import java.util.ArrayList;
import java.util.List;

public class PedidoPecasMapper {

    private PedidoPecasMapper() {
        // classe utilitária: não deve ser instanciada
    }

    // ===================== DTO -> MODEL (item avulso) =====================
    public static ItemPedido paraModel(ItemPedidoRequestDTO dto) {
        PecaRepository pecaRepository = PecaRepository.getInstance();
        Peca peca = pecaRepository.buscarPorId(dto.getCodigoSKU());
        if (peca == null) {
            throw new IllegalArgumentException(
                    "Peça com código SKU " + dto.getCodigoSKU() + " não encontrada");
        }
        return new ItemPedido(peca, dto.getQuantidade());
    }

    // ===================== DTO -> MODEL (pedido completo) =====================
    public static PedidoPecas paraModel(PedidoPecasRequestDTO dto) {
        PedidoPecas pedido = new PedidoPecas();

        for (ItemPedidoRequestDTO itemDTO : dto.getItens()) {
            pedido.adicionarItem(paraModel(itemDTO));
        }

        return pedido;
    }

    // ===================== MODEL -> DTO =====================
    public static PedidoPecasResponseDTO paraResponseDTO(PedidoPecas pedido) {
        PedidoPecasResponseDTO dto = new PedidoPecasResponseDTO();
        dto.setCodigoPedido(pedido.getCodigoPedido());
        dto.setStatus(pedido.getStatus());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setDataAtualizacao(pedido.getDataAtualizacao());

        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();
        for (ItemPedido item : pedido.getItens()) {
            ItemPedidoResponseDTO itemDto = new ItemPedidoResponseDTO();
            itemDto.setCodigoSKU(item.getPeca().getCodigoSKU());
            itemDto.setNomePeca(item.getPeca().getNome());
            itemDto.setQuantidade(item.getQuantidade());
            itemDto.setPrecoUnitario(item.getPeca().getPrecoVenda());
            itemDto.setSubtotal(item.getSubtotal());
            itensResponse.add(itemDto);
        }
        dto.setItens(itensResponse);

        return dto;
    }
}
