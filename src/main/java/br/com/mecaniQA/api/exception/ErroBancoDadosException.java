package br.com.mecaniQA.api.exception;

// Erro de acesso ao banco de dados: embrulha qualquer SQLException lançada
// pelo JDBC nos repositórios. Os Controllers capturam e devolvem 500.
public class ErroBancoDadosException extends RuntimeException {

    public ErroBancoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
