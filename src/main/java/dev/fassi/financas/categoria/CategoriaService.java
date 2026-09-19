package dev.fassi.financas.categoria;

import dev.fassi.financas.lancamento.LancamentoRepository;
import dev.fassi.financas.shared.CategoriaEmUsoException;
import dev.fassi.financas.shared.CategoriaNaoEncontradaException;
import dev.fassi.financas.shared.CategoriaNomeDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final LancamentoRepository lancamentoRepository;

    public CategoriaService(CategoriaRepository repository, LancamentoRepository lancamentoRepository) {
        this.repository = repository;
        this.lancamentoRepository = lancamentoRepository;
    }

    // POST
    public Categoria create(String nome, TipoCategoria tipo) {
        if (repository.existsByNome(nome)) {
            throw new CategoriaNomeDuplicadoException("Ja existe uma categoria com esse nome");
        }

        Categoria categoria = new Categoria(nome, tipo);
        return repository.save(categoria);
    }

    //GET
    public Categoria findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada"));
    }

    public List<Categoria> getAll() {
        return repository.findAll();
    }

    // PUT
    @Transactional
    public Categoria update(Long id, String nome, TipoCategoria tipo) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new CategoriaNomeDuplicadoException("Já existe uma categoria com esse nome");
        }

        Categoria categoria = findById(id);
        categoria.atualizar(nome, tipo);
        return categoria;
    }

    // DELETE
    public Categoria delete(Long id) {
        if (lancamentoRepository.existsByCategoriaId(id)) {
            throw new CategoriaEmUsoException("Categoria possui lançamentos e não pode ser apagada");
        } else {
            Categoria categoria = findById(id);
            repository.deleteById(id);
            return categoria;
        }

    }
}
