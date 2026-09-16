package dev.fassi.financas.categoria;

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

        Categoria categoria = new Categoria(nome, tipo);
        return repository.save(categoria);
    }

    //GET
    public Categoria findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
    }

    public List<Categoria> getAll() {
        return repository.findAll();
    }

    // PUT
    public Categoria update(Long id, String nome, TipoCategoria tipo) {
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
