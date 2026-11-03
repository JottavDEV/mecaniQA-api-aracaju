package br.com.mecaniQA.api.exception;

public class ServicoNaoEncontradoException extends RecursoNaoEncontradoException {

    public ServicoNaoEncontradoException(long codigoServico) {
        super("Serviço com código " + codigoServico + " não encontrado");
    }
}
