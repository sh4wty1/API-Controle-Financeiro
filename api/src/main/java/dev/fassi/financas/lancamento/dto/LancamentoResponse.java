package dev.fassi.financas.lancamento.dto;

import dev.fassi.financas.lancamento.Lancamento;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        Long categoriaId,
        LocalDate data
) {
    public LancamentoResponse(Lancamento lancamento) {
        this(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getCategoria().getId(),
                lancamento.getData()
        );
    }
}
