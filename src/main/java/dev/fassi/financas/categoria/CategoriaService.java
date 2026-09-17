package dev.fassi.financas.categoria;

import dev.fassi.financas.shared.CategoriaNaoEncontradaException;
import dev.fassi.financas.shared.CategoriaNomeDuplicadoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
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
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria nao encontrada"));
    }

    public List<Categoria> getAll() {
        return repository.findAll();
    }

    // PUT
    public Categoria update(Long id, String nome, TipoCategoria tipo) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new CategoriaNomeDuplicadoException("Ja existe uma categoria com esse nome");
        }

        Categoria categoria = findById(id);
        categoria.atualizar(nome, tipo);
        return repository.save(categoria);
    }

    // DELETE
    public Categoria delete(Long id) {
        Categoria categoria = findById(id);
        repository.deleteById(id);
        return categoria;
    }
}
