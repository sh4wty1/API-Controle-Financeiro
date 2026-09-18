package dev.fassi.financas.lancamento;

import dev.fassi.financas.lancamento.dto.LancamentoRequest;
import dev.fassi.financas.lancamento.dto.LancamentoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("lancamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    // ROTA POST
    @PostMapping
    public ResponseEntity<LancamentoResponse> create(@RequestBody @Valid LancamentoRequest lancamentoRequest) {
        Lancamento response = lancamentoService.create(
                lancamentoRequest.descricao(),
                lancamentoRequest.valor(),
                lancamentoRequest.data(),
                lancamentoRequest.categoriaId()
        );

        LancamentoResponse dtoResponse = new LancamentoResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoResponse);
    }

    // ROTA GET
    @GetMapping
    public ResponseEntity<List<LancamentoResponse>> findAll() {
        List<Lancamento> response = lancamentoService.getAll();
        List<LancamentoResponse> responseList = new ArrayList<>();
        for (Lancamento lancamento : response) {
            LancamentoResponse dtoResponse = new LancamentoResponse(lancamento);
            responseList.add(dtoResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponse> findById(@PathVariable Long id) {
        Lancamento response = lancamentoService.findById(id);
        LancamentoResponse dtoResponse = new LancamentoResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }

    // GET com filtro de mês
    @GetMapping(params = "mes")
    public ResponseEntity<Page<LancamentoResponse>> filterByMonth(
            @RequestParam YearMonth mes,
            @RequestParam(required = false) Long categoriaId,
            Pageable pageable
    ) {
        Page<LancamentoResponse> response = lancamentoService.filterByMonth(mes, categoriaId, pageable).map(LancamentoResponse::new);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ROTA PUT

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponse> update(@PathVariable Long id, @RequestBody @Valid LancamentoRequest lancamentoRequest) {
        Lancamento response = lancamentoService.update(id, lancamentoRequest.descricao(), lancamentoRequest.categoriaId(), lancamentoRequest.valor(), lancamentoRequest.data());
        LancamentoResponse dtoResponse = new LancamentoResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }

    // ROTA DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<LancamentoResponse> delete(@PathVariable Long id) {
        Lancamento response = lancamentoService.delete(id);
        LancamentoResponse dtoResponse = new LancamentoResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(dtoResponse);
    }
}
