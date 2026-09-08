package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.dto.ServicoResumoDTO;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;

import java.util.ArrayList;
import java.util.List;

// Responsável por converter o mundo "de fora" (DTOs, JSON) no mundo
// "de dentro" (entidades de modelo) e vice-versa.
public class OrdemServicoMapper {

    private OrdemServicoMapper() {
        // classe utilitária: não deve ser instanciada
    }

    // ===================== DTO -> MODEL =====================
    public static OrdemServico paraModel(OrdemServicoRequestDTO dto) {
        OrdemServico.Builder builder = OrdemServico.builder()
                .comCliente(dto.getClienteNome())
                .comVeiculo(dto.getVeiculo())
                .comDescricaoProblema(dto.getDescricaoProblema());

        ServicoRepository servicoRepository = ServicoRepository.getInstance();

        for (Long codigoServico : dto.getCodigosServicos()) {
            Servico servico = servicoRepository.buscarPorID(codigoServico);
            if (servico == null) {
                throw new IllegalArgumentException(
                        "Serviço com código " + codigoServico + " não encontrado");
            }
            builder.adicionarServico(servico);
        }

        return builder.build();
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
