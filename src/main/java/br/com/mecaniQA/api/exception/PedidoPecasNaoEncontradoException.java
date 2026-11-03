package br.com.mecaniQA.api.exception;

public class PedidoPecasNaoEncontradoException extends RecursoNaoEncontradoException {

    public PedidoPecasNaoEncontradoException(long codigoPedido) {
        super("Pedido de peças com código " + codigoPedido + " não encontrado");
    }
}
