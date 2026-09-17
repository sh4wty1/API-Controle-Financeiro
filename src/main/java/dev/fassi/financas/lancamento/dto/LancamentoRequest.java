package dev.fassi.financas.lancamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(
        @NotBlank
        String descricao,
        @NotNull
        BigDecimal valor,
        @NotNull
        Long categoriaId,
        @NotNull
        LocalDate data

) {}
