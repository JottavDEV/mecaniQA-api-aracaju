package br.com.mecaniQA.api.exception;

// Violação de regra de negócio / dado inválido. Os Controllers
// capturam esta classe para devolver 400.
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
