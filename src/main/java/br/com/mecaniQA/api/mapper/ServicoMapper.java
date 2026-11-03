package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ServicoRequestDTO;
import br.com.mecaniQA.api.dto.ServicoResponseDTO;
import br.com.mecaniQA.api.model.Servico;

// DTO <-> modelo de Serviço.
// (A conversão ResultSet -> modelo fica em ServicoResultSetMapper.)
public class ServicoMapper {

    private ServicoMapper() {
        // classe utilitária: não deve ser instanciada
    }

    // ===================== DTO -> MODEL =====================
    public static Servico paraModel(ServicoRequestDTO dto) {
        Servico servico = new Servico();
        servico.setNomeServico(dto.getNomeServico());
        servico.setDescricaoServico(dto.getDescricaoServico());
        servico.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());
        servico.setCustoTabelado(dto.getCustoTabelado());
        return servico;
    }

    // ===================== MODEL -> DTO =====================
    public static ServicoResponseDTO paraResponseDTO(Servico servico) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setCodigoServico(servico.getCodigoServico());
        dto.setNomeServico(servico.getNomeServico());
        dto.setDescricaoServico(servico.getDescricaoServico());
        dto.setTempoEstimadoMinutos(servico.getTempoEstimadoMinutos());
        dto.setCustoTabelado(servico.getCustoTabelado());
        dto.setDataCriacao(servico.getDataCriacao());
        dto.setDataAtualizacao(servico.getDataAtualizacao());
        return dto;
    }
}
