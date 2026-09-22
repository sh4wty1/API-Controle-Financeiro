package dev.fassi.financas.relatorio;

import dev.fassi.financas.categoria.TipoCategoria;
import dev.fassi.financas.lancamento.LancamentoRepository;
import dev.fassi.financas.relatorio.dto.CategoriaTotalProjection;
import dev.fassi.financas.relatorio.dto.CategoriaTotalResponse;
import dev.fassi.financas.relatorio.dto.RelatorioResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioService {

    private final LancamentoRepository lancamentoRepository;

    public RelatorioService(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    private BigDecimal somarPorTipo(List<CategoriaTotalProjection> categorias, TipoCategoria tipo) {
        return categorias.stream()
                .filter(c -> c.getTipo() == tipo)
                .map(CategoriaTotalProjection::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public RelatorioResponse gerarRelatorio(LocalDate initialDate, LocalDate finalDate) {
        if (initialDate.isAfter(finalDate)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }

        List<CategoriaTotalProjection> categorias = lancamentoRepository.sumPorCategoriaBetween(initialDate, finalDate);

        BigDecimal totalReceitas = somarPorTipo(categorias, TipoCategoria.RECEITA);

        BigDecimal totalDespesas = somarPorTipo(categorias, TipoCategoria.DESPESA);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        List<CategoriaTotalResponse> porCategoria = categorias.stream()
                .map(CategoriaTotalResponse::new)
                .toList();

        return new RelatorioResponse(totalReceitas, totalDespesas, saldo, porCategoria);
    }
}
