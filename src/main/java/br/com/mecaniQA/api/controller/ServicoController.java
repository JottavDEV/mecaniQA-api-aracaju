package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    // ===================== REPOSITORY =====================

    private final ServicoRepository repository;

    // ===================== CONSTRUTOR =====================

    public ServicoController() {
        this.repository = ServicoRepository.getInstance();
    }

    // ===================== CREATE =====================

    @PostMapping
    public Servico criar(@RequestBody Servico servico) {

        return repository.salvar(servico);
    }

    // ===================== READ - TODOS =====================

    @GetMapping
    public List<Servico> listarTodos() {

        return repository.getServicos();
    }

    // ===================== READ - POR ID =====================

    @GetMapping("/{codigoServico}")
    public Servico buscarPorId(
            @PathVariable long codigoServico) {

        return repository.buscarPorID(codigoServico);
    }

    // ===================== UPDATE =====================

    @PutMapping("/{codigoServico}")
    public Servico atualizar(
            @PathVariable long codigoServico,
            @RequestBody Servico servico) {

        return repository.atualizar(
                codigoServico,
                servico
        );
    }

    // ===================== DELETE =====================

    @DeleteMapping("/{codigoServico}")
    public boolean deletar(
            @PathVariable long codigoServico) {

        return repository.deletar(codigoServico);
    }
}