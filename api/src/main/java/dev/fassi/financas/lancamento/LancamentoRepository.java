package dev.fassi.financas.lancamento;

import dev.fassi.financas.relatorio.dto.CategoriaTotalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    Page<Lancamento> findByCategoriaIdAndDataBetween(Long categoriaId, LocalDate initialData, LocalDate finalData, Pageable pageable);

    Page<Lancamento> findByDataBetween(LocalDate initialData, LocalDate finalData, Pageable pageable);

    boolean existsByCategoriaId(Long id);

    @Query("""
        SELECT l.categoria.id AS categoriaId, l.categoria.nome AS categoriaNome,
               l.categoria.tipo AS tipo, SUM(l.valor) AS total
        FROM Lancamento l
        WHERE l.data BETWEEN :inicio AND :fim
        GROUP BY l.categoria.id, l.categoria.nome, l.categoria.tipo
        ORDER BY l.categoria.nome
""")
    List<CategoriaTotalProjection> sumPorCategoriaBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
