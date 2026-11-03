package br.com.mecaniQA.api.exception;

// Base das exceções "não encontrado". Os Controllers capturam esta
// classe para devolver 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
