package dev.fassi.financas.relatorio.dto;

import dev.fassi.financas.categoria.TipoCategoria;

import java.math.BigDecimal;

public interface CategoriaTotalProjection {
    Long getCategoriaId();
    String getCategoriaNome();
    TipoCategoria getTipo();
    BigDecimal getTotal();
}
