package dev.fassi.financas.categoria.dto;

import dev.fassi.financas.categoria.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(

        @NotBlank
        String nome,

        @NotNull
        TipoCategoria tipo
) {
}
