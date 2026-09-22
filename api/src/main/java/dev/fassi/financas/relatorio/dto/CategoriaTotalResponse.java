package dev.fassi.financas.relatorio.dto;

import dev.fassi.financas.categoria.TipoCategoria;

import java.math.BigDecimal;

public record CategoriaTotalResponse(
        Long categoriaId,
        String categoriaNome,
        TipoCategoria tipo,
        BigDecimal total
) {
    public CategoriaTotalResponse(CategoriaTotalProjection projection) {
        this(
                projection.getCategoriaId(),
                projection.getCategoriaNome(),
                projection.getTipo(),
                projection.getTotal()
        );
    }
}
