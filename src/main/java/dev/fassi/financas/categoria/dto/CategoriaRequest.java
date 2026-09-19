package dev.fassi.financas.categoria.dto;

import dev.fassi.financas.categoria.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(

        @NotBlank(message = "A categoria deve ter um nome")
        String nome,

        @NotNull(message = "O tipo não pode ser nulo")
        TipoCategoria tipo
) {
}
