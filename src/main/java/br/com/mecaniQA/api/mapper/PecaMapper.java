package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.model.Peca;

// DTO <-> modelo de Peça.
// (A conversão ResultSet -> modelo fica em PecaResultSetMapper.)
public class PecaMapper {

    private PecaMapper() {
        // classe utilitária: não deve ser instanciada
    }

    // ===================== DTO -> MODEL =====================
    public static Peca paraModel(PecaRequestDTO dto) {
        Peca peca = new Peca();
        peca.setNome(dto.getNome());
        peca.setCodigobarras(dto.getCodigobarras());
        peca.setFornecedor(dto.getFornecedor());
        peca.setQuantidade(dto.getQuantidade());
        peca.setPrecoCusto(dto.getPrecoCusto());
        peca.setPrecoVenda(dto.getPrecoVenda());
        peca.setTamanho(dto.getTamanho());
        peca.setCor(dto.getCor());
        peca.setCategoriaPeca(dto.getCategoriaPeca());
        return peca;
    }

    // ===================== MODEL -> DTO =====================
    public static PecaResponseDTO paraResponseDTO(Peca peca) {
        PecaResponseDTO dto = new PecaResponseDTO();
        dto.setCodigoSKU(peca.getCodigoSKU());
        dto.setNome(peca.getNome());
        dto.setCodigobarras(peca.getCodigobarras());
        dto.setFornecedor(peca.getFornecedor());
        dto.setQuantidade(peca.getQuantidade());
        dto.setPrecoCusto(peca.getPrecoCusto());
        dto.setPrecoVenda(peca.getPrecoVenda());
        dto.setTamanho(peca.getTamanho());
        dto.setCor(peca.getCor());
        dto.setCategoriaPeca(peca.getCategoriaPeca());
        dto.setDataCadastro(peca.getDataCadastro());
        dto.setDataAtualizacao(peca.getDataAtualizacao());
        return dto;
    }
}
