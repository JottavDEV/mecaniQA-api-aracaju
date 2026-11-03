package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.service.PecaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// O Controller só recebe a requisição, chama o Service e devolve o
// status HTTP. Os erros são tratados aqui, manualmente, com try/catch.
@RestController
@RequestMapping("/api/pecas")
public class PecaController {

    private final PecaService service;

    public PecaController(PecaService service) {
        this.service = service;
    }

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody PecaRequestDTO dto) {
        try {
            Peca pecaSalva = service.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(PecaMapper.paraResponseDTO(pecaSalva));
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ =====================
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            List<PecaResponseDTO> resposta = new ArrayList<>();
            for (Peca peca : service.listarTodas()) {
                resposta.add(PecaMapper.paraResponseDTO(peca));
            }
            return ResponseEntity.ok(resposta);
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ por ID =====================
    @GetMapping("/{codigoSKU}")
    public ResponseEntity<?> buscarPorId(@PathVariable long codigoSKU) {
        try {
            return ResponseEntity.ok(PecaMapper.paraResponseDTO(service.buscarPorId(codigoSKU)));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== UPDATE =====================
    @PutMapping("/{codigoSKU}")
    public ResponseEntity<?> atualizar(
            @PathVariable long codigoSKU,
            @Valid @RequestBody PecaRequestDTO dto) {
        try {
            return ResponseEntity.ok(PecaMapper.paraResponseDTO(service.atualizar(codigoSKU, dto)));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoSKU}")
    public ResponseEntity<?> deletar(@PathVariable long codigoSKU) {
        try {
            service.deletar(codigoSKU);
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
