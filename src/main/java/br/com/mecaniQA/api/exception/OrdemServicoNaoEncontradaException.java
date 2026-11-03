package br.com.mecaniQA.api.exception;

public class OrdemServicoNaoEncontradaException extends RecursoNaoEncontradoException {

    public OrdemServicoNaoEncontradaException(long codigoOS) {
        super("Ordem de serviço com código " + codigoOS + " não encontrada");
    }
}
