package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ==================== Rotas CRUD ==================
// ===================== CREATE =====================

@RestController
@RequestMapping("/api/criarPeca")
public class PecaController {

    private final PecaRepository repository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<Peca> salvar(@Valid @RequestBody Peca peca) {
        Peca pecaSalva = repository.salvar(peca);
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaSalva);
    }

    // ===================== READ =====================

    @GetMapping("/api/listarPecas")
    public List<Peca> listarTodas() {
        return repository.listarTodas();
    }

    // ===================== READ por ID =====================

    @GetMapping("/api/{codigoSKU}")
    public ResponseEntity<Peca> buscarPorId(@PathVariable long codigoSKU) {
        Peca peca = repository.buscarPorId(codigoSKU);
        if (peca == null) {
            throw new RecursoNaoEncontradoException("Peça com código SKU " + codigoSKU + " não encontrada");
        }
        return ResponseEntity.ok(peca);
    }

    // ===================== UPDATE =====================

    @PutMapping("/api/{codigoSKU}")
    public ResponseEntity<Peca> atualizar(
            @PathVariable long codigoSKU,
            @Valid @RequestBody Peca peca) {

        Peca pecaAtualizada = repository.atualizar(codigoSKU, peca);
        if (pecaAtualizada == null) {
            throw new RecursoNaoEncontradoException("Peça com código SKU " + codigoSKU + " não encontrada");
        }
        return ResponseEntity.ok(pecaAtualizada);
    }

    // ===================== DELETE =====================

    @DeleteMapping("/api/{codigoSKU}")
    public ResponseEntity<Void> deletar(@PathVariable long codigoSKU) {
        boolean removida = repository.deletar(codigoSKU);
        if (!removida) {
            throw new RecursoNaoEncontradoException("Peça com código SKU " + codigoSKU + " não encontrada");
        }
        return ResponseEntity.noContent().build();
    }
}