package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.dto.ServicoResumoDTO;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;

import java.util.ArrayList;
import java.util.List;

// Converte a entidade de modelo no DTO de resposta devolvido ao cliente.
// (A conversão ResultSet -> modelo fica em OrdemServicoResultSetMapper.)
public class OrdemServicoMapper {

    private OrdemServicoMapper() {
        // classe utilitária: não deve ser instanciada
    }

    // ===================== MODEL -> DTO =====================
    public static OrdemServicoResponseDTO paraResponseDTO(OrdemServico ordemServico) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.setCodigoOS(ordemServico.getCodigoOS());
        dto.setClienteNome(ordemServico.getClienteNome());
        dto.setVeiculo(ordemServico.getVeiculo());
        dto.setDescricaoProblema(ordemServico.getDescricaoProblema());
        dto.setStatus(ordemServico.getStatus());
        dto.setValorTotal(ordemServico.getValorTotal());
        dto.setDataAbertura(ordemServico.getDataAbertura());
        dto.setDataAtualizacao(ordemServico.getDataAtualizacao());

        List<ServicoResumoDTO> servicosResumo = new ArrayList<>();
        for (Servico servico : ordemServico.getServicos()) {
            ServicoResumoDTO resumo = new ServicoResumoDTO();
            resumo.setCodigoServico(servico.getCodigoServico());
            resumo.setNomeServico(servico.getNomeServico());
            resumo.setCustoTabelado(servico.getCustoTabelado());
            servicosResumo.add(resumo);
        }
        dto.setServicos(servicosResumo);

        return dto;
    }
}
