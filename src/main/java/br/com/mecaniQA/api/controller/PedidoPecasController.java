package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.AtualizarStatusPedidoDTO;
import br.com.mecaniQA.api.dto.ItemPedidoRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecasResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.mapper.PedidoPecasMapper;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.service.PedidoPecasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// O Controller só recebe a requisição (DTO), chama o Service e devolve o
// status HTTP. Os erros são tratados aqui, manualmente, com try/catch.
@RestController
@RequestMapping("/api/pedidos-pecas")
public class PedidoPecasController {

    private final PedidoPecasService service;

    public PedidoPecasController(PedidoPecasService service) {
        this.service = service;
    }

    // ===================== CREATE (US03) =====================
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoPecasRequestDTO dto) {
        try {
            PedidoPecas pedidoSalvo = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PedidoPecasMapper.paraResponseDTO(pedidoSalvo));
        } catch (PecaNaoEncontradaException | DadosInvalidosException e) {
            // peça informada no corpo da requisição não existe, ou dado inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - TODOS =====================
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<PedidoPecasResponseDTO> resposta = new ArrayList<>();
            for (PedidoPecas pedido : service.listarTodos()) {
                resposta.add(PedidoPecasMapper.paraResponseDTO(pedido));
            }
            return ResponseEntity.ok(resposta);
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - POR ID =====================
    @GetMapping("/{codigoPedido}")
    public ResponseEntity<?> buscarPorId(@PathVariable long codigoPedido) {
        try {
            PedidoPecas pedido = service.buscarPorId(codigoPedido);
            return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedido));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== UPDATE - ADICIONAR ITEM (US04) =====================
    @PostMapping("/{codigoPedido}/itens")
    public ResponseEntity<?> adicionarItem(
            @PathVariable long codigoPedido,
            @Valid @RequestBody ItemPedidoRequestDTO dto) {
        try {
            PedidoPecas pedidoAtualizado = service.adicionarItem(codigoPedido, dto);
            return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedidoAtualizado));
        } catch (PecaNaoEncontradaException | DadosInvalidosException e) {
            // peça informada no corpo da requisição não existe, ou dado inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            // o pedido da URL não existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== UPDATE - STATUS (US05) =====================
    @PatchMapping("/{codigoPedido}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable long codigoPedido,
            @Valid @RequestBody AtualizarStatusPedidoDTO dto) {
        try {
            PedidoPecas pedidoAtualizado = service.atualizarStatus(codigoPedido, dto.getStatus());
            return ResponseEntity.ok(PedidoPecasMapper.paraResponseDTO(pedidoAtualizado));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoPedido}")
    public ResponseEntity<?> deletar(@PathVariable long codigoPedido) {
        try {
            service.deletar(codigoPedido);
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
