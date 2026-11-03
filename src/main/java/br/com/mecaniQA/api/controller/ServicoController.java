package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ErroBancoDadosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.dto.ServicoRequestDTO;
import br.com.mecaniQA.api.dto.ServicoResponseDTO;
import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// O Controller só recebe a requisição, chama o Service e devolve o
// status HTTP. Os erros são tratados aqui, manualmente, com try/catch.
@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ServicoRequestDTO dto) {
        try {
            Servico servicoSalvo = service.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ServicoMapper.paraResponseDTO(servicoSalvo));
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - TODOS =====================
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<ServicoResponseDTO> resposta = new ArrayList<>();
            for (Servico servico : service.listarTodos()) {
                resposta.add(ServicoMapper.paraResponseDTO(servico));
            }
            return ResponseEntity.ok(resposta);
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== READ - POR ID =====================
    @GetMapping("/{codigoServico}")
    public ResponseEntity<?> buscarPorId(@PathVariable long codigoServico) {
        try {
            return ResponseEntity.ok(ServicoMapper.paraResponseDTO(service.buscarPorId(codigoServico)));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== UPDATE =====================
    @PutMapping("/{codigoServico}")
    public ResponseEntity<?> atualizar(
            @PathVariable long codigoServico,
            @Valid @RequestBody ServicoRequestDTO dto) {
        try {
            return ResponseEntity.ok(ServicoMapper.paraResponseDTO(service.atualizar(codigoServico, dto)));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ErroBancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoServico}")
    public ResponseEntity<?> deletar(@PathVariable long codigoServico) {
        try {
            service.deletar(codigoServico);
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
