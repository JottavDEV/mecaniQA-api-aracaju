package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    // ==================== Rotas CRUD ==================
    // ===================== READ =====================
@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoRepository repository;

    public ServicoController() {
        this.repository = ServicoRepository.getInstance();
    }


    // ===================== CREATE =====================

    @PostMapping
    public ResponseEntity<Servico> criar(@Valid @RequestBody Servico servico) {
        Servico servicoSalvo = repository.salvar(servico);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoSalvo);
    }

    // ===================== READ - TODOS =====================

    @GetMapping("/api/listarServicos")
    public List<Servico> listarTodos() {
        return repository.getServicos();
    }

    // ===================== READ - POR ID =====================

    @GetMapping("/api/{codigoServico}")
    public ResponseEntity<Servico> buscarPorId(
            @PathVariable long codigoServico) {

        Servico servico = repository.buscarPorID(codigoServico);
        if (servico == null) {
            throw new RecursoNaoEncontradoException("Serviço com código " + codigoServico + " não encontrado");
        }
        return ResponseEntity.ok(servico);
    }

    // ===================== UPDATE =====================

    @PutMapping("/api/{codigoServico}")
    public ResponseEntity<Servico> atualizar(
            @PathVariable long codigoServico,
            @Valid @RequestBody Servico servico) {

        Servico servicoAtualizado = repository.atualizar(codigoServico, servico);
        if (servicoAtualizado == null) {
            throw new RecursoNaoEncontradoException("Serviço com código " + codigoServico + " não encontrado");
        }
        return ResponseEntity.ok(servicoAtualizado);
    }

    // ===================== DELETE =====================

    @DeleteMapping("/api/{codigoServico}")
    public ResponseEntity<Void> deletar(
            @PathVariable long codigoServico) {

        boolean removido = repository.deletar(codigoServico);
        if (!removido) {
            throw new RecursoNaoEncontradoException("Serviço com código " + codigoServico + " não encontrado");
        }
        return ResponseEntity.noContent().build();
    }
}