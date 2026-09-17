package dev.fassi.financas.lancamento;

import dev.fassi.financas.categoria.Categoria;
import dev.fassi.financas.categoria.CategoriaRepository;
import dev.fassi.financas.shared.CategoriaNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository, CategoriaRepository categoriaRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    //POST
    public Lancamento create(String descricao, BigDecimal valor, LocalDate data, Long categoriaId) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaNaoEncontradaException("Nao existe uma categoria com esse nome");
        }

        Categoria categoria = categoriaRepository.findById(categoriaId);

        Lancamento lancamento = new Lancamento(descricao, valor, categoria, data);
        return lancamentoRepository.save(lancamento);
    }
}
