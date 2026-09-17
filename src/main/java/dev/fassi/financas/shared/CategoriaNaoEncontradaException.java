package dev.fassi.financas.shared;

public class CategoriaNaoEncontradaException extends RuntimeException {
    public CategoriaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
