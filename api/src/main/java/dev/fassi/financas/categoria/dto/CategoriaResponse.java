package dev.fassi.financas.categoria.dto;

import dev.fassi.financas.categoria.Categoria;
import dev.fassi.financas.categoria.TipoCategoria;

public record CategoriaResponse(
        Long id,
        String nome,
        TipoCategoria tipo
) {
    public CategoriaResponse(Categoria categoria) {
        this(categoria.getId(), categoria.getNome(), categoria.getTipo());
    }
}
