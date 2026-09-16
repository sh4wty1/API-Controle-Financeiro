package dev.fassi.financas.categoria;

import dev.fassi.financas.categoria.dto.CategoriaRequest;
import dev.fassi.financas.categoria.dto.CategoriaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // ROTA POST
    @PostMapping
    public ResponseEntity<CategoriaResponse> create(@RequestBody @Valid CategoriaRequest categoriaRequest) {
        Categoria response = categoriaService.create(categoriaRequest.nome(), categoriaRequest.tipo());
        CategoriaResponse dtoResponse = new CategoriaResponse(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoResponse);
    }
    // ROTA GET
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> findAll() {
        List<Categoria> response = categoriaService.getAll();
        List<CategoriaResponse> responseList = new ArrayList<>();
        for (Categoria categoria : response) {
            CategoriaResponse dtoResponse = new CategoriaResponse(categoria);
            responseList.add(dtoResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> findById(@PathVariable Long id) {
        Categoria response = categoriaService.findById(id);
        CategoriaResponse dtoResponse = new CategoriaResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> update(@PathVariable Long id, @RequestBody @Valid CategoriaRequest categoriaRequest) {
        Categoria response = categoriaService.update(id, categoriaRequest.nome(), categoriaRequest.tipo());
        CategoriaResponse dtoResponse = new CategoriaResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaResponse> delete(@PathVariable Long id) {
        Categoria response = categoriaService.delete(id);
        CategoriaResponse dtoResponse = new CategoriaResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }
}
