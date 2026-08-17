package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pecas")
public class PecaController {

    private final PecaRepository repository = PecaRepository.getInstance();

    @PostMapping
    public Peca salvar(@RequestBody Peca peca) {
        return repository.salvar(peca);
    }

    @GetMapping
    public List<Peca> listarTodas() {
        return repository.listarTodas();
    }

    @GetMapping("/{codigoSKU}")
    public Peca buscarPorId(@PathVariable long codigoSKU) {
        return repository.buscarPorId(codigoSKU);
    }

    @PutMapping("/{codigoSKU}")
    public Peca atualizar(
            @PathVariable long codigoSKU,
            @RequestBody Peca peca) {

        return repository.atualizar(codigoSKU, peca);
    }

    @DeleteMapping("/{codigoSKU}")
    public boolean deletar(@PathVariable long codigoSKU) {
        return repository.deletar(codigoSKU);
    }
}