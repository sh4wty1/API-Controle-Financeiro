package dev.fassi.financas.shared;

public class LancamentoNaoEncontradoException extends RuntimeException {
    public LancamentoNaoEncontradoException(String message) {
        super(message);
    }
}
