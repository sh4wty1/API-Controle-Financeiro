package dev.fassi.financas.lancamento;

import dev.fassi.financas.categoria.Categoria;
import dev.fassi.financas.categoria.CategoriaRepository;
import dev.fassi.financas.shared.CategoriaNaoEncontradaException;
import dev.fassi.financas.shared.LancamentoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository, CategoriaRepository categoriaRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private Categoria resolverCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada"));
    }

    //POST
    public Lancamento create(String descricao, BigDecimal valor, LocalDate data, Long categoriaId) {

        Lancamento lancamento = new Lancamento(
                descricao,
                valor,
                resolverCategoria(categoriaId),
                data
        );
        return lancamentoRepository.save(lancamento);
    }

    //GET
    public Lancamento findById(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new LancamentoNaoEncontradoException("Lançamento não encontrado"));
    }

    public List<Lancamento> getAll() {
        return lancamentoRepository.findAll();
    }

    // GET filtrado por mês
    public Page<Lancamento> filterByMonth(YearMonth mes, Long categoriaId, Pageable pageable) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();
        if (categoriaId != null) {
            return lancamentoRepository.findByCategoriaIdAndDataBetween(categoriaId, inicio, fim, pageable);
        } else {
            return lancamentoRepository.findByDataBetween(inicio, fim, pageable);
        }
    }

    //PUT
    @Transactional
    public Lancamento update(Long id, String descricao, Long categoriaId, BigDecimal valor, LocalDate data) {
        Lancamento lancamento = findById(id);

        lancamento.atualizar(descricao, valor, resolverCategoria(categoriaId), data);
        return lancamento;
    }

    //DELETE
    public Lancamento delete(Long id) {
        Lancamento lancamento = findById(id);
        if (lancamento == null) {
        throw new LancamentoNaoEncontradoException("Lançamento não encontrado");
        }
        lancamentoRepository.delete(lancamento);
        return lancamento;
    }
}
