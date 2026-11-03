package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ItemPedidoResponseDTO;
import br.com.mecaniQA.api.dto.PedidoPecasResponseDTO;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.PedidoPecas;

import java.util.ArrayList;
import java.util.List;

// Converte a entidade de modelo no DTO de resposta devolvido ao cliente.
// (A conversão ResultSet -> modelo fica em PedidoPecasResultSetMapper.)
public class PedidoPecasMapper {

    private PedidoPecasMapper() {
        // classe utilitária: não deve ser instanciada
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
