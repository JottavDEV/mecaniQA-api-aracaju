package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.AtualizarStatusPedidoDTO;
import br.com.mecaniQA.api.dto.ItemPedidoRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasResponseDTO;
import br.com.mecaniQA.api.mapper.PedidoPecasMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos-pecas")
public class PedidoPecasController {

    private final PedidoPecasRepository repository = PedidoPecasRepository.getInstance();

    // ===================== CREATE (US03) =====================
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoPecasRequestDTO dto) {
        PedidoPecas pedido;
        try {
            pedido = PedidoPecasMapper.paraModel(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        PedidoPecas pedidoSalvo = repository.salvar(pedido);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PedidoPecasMapper.paraResponseDTO(pedidoSalvo));
    }

    // ===================== READ - TODOS =====================
    @GetMapping
    public List<PedidoPecasResponseDTO> listarTodos() {
        List<PedidoPecasResponseDTO> resposta = new ArrayList<>();
        for (PedidoPecas pedido : repository.listarTodos()) {
            resposta.add(PedidoPecasMapper.paraResponseDTO(pedido));
        }
        return resposta;
    }

    // ===================== READ - POR ID =====================
    @GetMapping("/{codigoPedido}")
    public ResponseEntity<PedidoPecasResponseDTO> buscarPorId(@PathVariable long codigoPedido) {
        PedidoPecas pedido = repository.buscarPorId(codigoPedido);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedido));
    }

    // ===================== UPDATE - ADICIONAR ITEM (US04) =====================
    @PostMapping("/{codigoPedido}/itens")
    public ResponseEntity<?> adicionarItem(
            @PathVariable long codigoPedido,
            @Valid @RequestBody ItemPedidoRequestDTO dto) {

        ItemPedido item;
        try {
            item = PedidoPecasMapper.paraModel(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        PedidoPecas pedidoAtualizado = repository.adicionarItem(codigoPedido, item);
        if (pedidoAtualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedidoAtualizado));
    }

    // ===================== UPDATE - STATUS (US05) =====================
    @PatchMapping("/{codigoPedido}/status")
    public ResponseEntity<PedidoPecasResponseDTO> atualizarStatus(
            @PathVariable long codigoPedido,
            @Valid @RequestBody AtualizarStatusPedidoDTO dto) {

        PedidoPecas pedidoAtualizado = repository.atualizarStatus(codigoPedido, dto.getStatus());
        if (pedidoAtualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedidoAtualizado));
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoPedido}")
    public ResponseEntity<Void> deletar(@PathVariable long codigoPedido) {
        boolean removido = repository.deletar(codigoPedido);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
