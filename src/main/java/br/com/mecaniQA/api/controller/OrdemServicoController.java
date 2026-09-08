package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.AtualizarStatusOSDTO;
import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoRepository repository = OrdemServicoRepository.getInstance();

    // ===================== CREATE (US01) =====================
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody OrdemServicoRequestDTO dto) {
        OrdemServico ordemServico;
        try {
            ordemServico = OrdemServicoMapper.paraModel(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        OrdemServico ordemSalva = repository.salvar(ordemServico);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrdemServicoMapper.paraResponseDTO(ordemSalva));
    }

    // ===================== READ - TODAS =====================
    @GetMapping
    public List<OrdemServicoResponseDTO> listarTodas() {
        List<OrdemServicoResponseDTO> resposta = new ArrayList<>();
        for (OrdemServico ordemServico : repository.listarTodas()) {
            resposta.add(OrdemServicoMapper.paraResponseDTO(ordemServico));
        }
        return resposta;
    }

    // ===================== READ - POR ID =====================
    @GetMapping("/{codigoOS}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable long codigoOS) {
        OrdemServico ordemServico = repository.buscarPorId(codigoOS);
        if (ordemServico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OrdemServicoMapper.paraResponseDTO(ordemServico));
    }

    // ===================== UPDATE - STATUS (US02) =====================
    @PatchMapping("/{codigoOS}/status")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarStatus(
            @PathVariable long codigoOS,
            @Valid @RequestBody AtualizarStatusOSDTO dto) {

        OrdemServico ordemAtualizada = repository.atualizarStatus(codigoOS, dto.getStatus());
        if (ordemAtualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OrdemServicoMapper.paraResponseDTO(ordemAtualizada));
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{codigoOS}")
    public ResponseEntity<Void> deletar(@PathVariable long codigoOS) {
        boolean removida = repository.deletar(codigoOS);
        if (!removida) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
