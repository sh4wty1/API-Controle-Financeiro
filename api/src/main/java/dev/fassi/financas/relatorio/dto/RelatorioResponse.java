package dev.fassi.financas.relatorio.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioResponse(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldo,
        List<CategoriaTotalResponse> porCategoria
) {
}
