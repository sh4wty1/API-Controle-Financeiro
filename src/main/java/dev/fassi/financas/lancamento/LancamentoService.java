package dev.fassi.financas.lancamento;

import dev.fassi.financas.categoria.Categoria;
import dev.fassi.financas.categoria.CategoriaRepository;
import dev.fassi.financas.shared.CategoriaNaoEncontradaException;
import dev.fassi.financas.shared.LancamentoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    //PUT
    public Lancamento update(Long id, String descricao, Long categoriaId, BigDecimal valor, LocalDate data) {
        Lancamento lancamento = findById(id);

        lancamento.atualizar(descricao, valor, resolverCategoria(categoriaId), data);
        return lancamentoRepository.save(lancamento);
    }

    //DELETE
    public Lancamento delete(Long id) {

        Lancamento lancamento = findById(id);
        lancamentoRepository.delete(lancamento);
        return lancamento;
    }
}
