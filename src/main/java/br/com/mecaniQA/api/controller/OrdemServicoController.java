package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.AtualizarStatusOSDTO;
import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.exception.ServicoNaoEncontradoException;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// O Controller só recebe a requisição (DTO), chama o Service e devolve o
// status HTTP. Os erros são tratados aqui, manualmente, com try/catch.
@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    // ===================== CREATE (US01) =====================
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody OrdemServicoRequestDTO dto) {
        try {
            OrdemServico ordemSalva = service.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(OrdemServicoMapper.paraResponseDTO(ordemSalva));
        } catch (ServicoNaoEncontradoException | DadosInvalidosException e) {
            // serviço informado no corpo da requisição não existe, ou dado inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - TODAS =====================
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            List<OrdemServicoResponseDTO> resposta = new ArrayList<>();
            for (OrdemServico ordemServico : service.listarTodas()) {
                resposta.add(OrdemServicoMapper.paraResponseDTO(ordemServico));
            }
            return ResponseEntity.ok(resposta);
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - POR ID =====================
    @GetMapping("/{codigoOS}")
    public ResponseEntity<?> buscarPorId(@PathVariable long codigoOS) {
        try {
            OrdemServico ordemServico = service.buscarPorId(codigoOS);
            return ResponseEntity.ok(OrdemServicoMapper.paraResponseDTO(ordemServico));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== UPDATE - STATUS (US02) =====================
    @PatchMapping("/{codigoOS}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable long codigoOS,
            @Valid @RequestBody AtualizarStatusOSDTO dto) {
        try {
            OrdemServico ordemAtualizada = service.atualizarStatus(codigoOS, dto.getStatus());
            return ResponseEntity.ok(OrdemServicoMapper.paraResponseDTO(ordemAtualizada));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoOS}")
    public ResponseEntity<?> deletar(@PathVariable long codigoOS) {
        try {
            service.deletar(codigoOS);
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
