package br.com.mecaniQA.api.exception;

public class PecaNaoEncontradaException extends RecursoNaoEncontradoException {

    public PecaNaoEncontradaException(long codigoSKU) {
        super("Peça com código SKU " + codigoSKU + " não encontrada");
    }
}
