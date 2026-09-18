package dev.fassi.financas.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    Page<Lancamento> findByCategoriaIdAndDataBetween(Long categoriaId, LocalDate initialData, LocalDate finalData, Pageable pageable);

    Page<Lancamento> findByDataBetween(LocalDate initialData, LocalDate finalData, Pageable pageable);
}
