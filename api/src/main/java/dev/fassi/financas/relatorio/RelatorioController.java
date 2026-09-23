package dev.fassi.financas.relatorio;

import dev.fassi.financas.relatorio.dto.RelatorioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("relatorios")
public class RelatorioController {
    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/mensal")
    public ResponseEntity<RelatorioResponse> obterRelatorioMensal(
            @RequestParam(value = "ano") Integer ano,
            @RequestParam(value = "mes") Integer mes
    ) {
        RelatorioResponse response = relatorioService.gerarRelatorio(
                java.time.LocalDate.of(ano, mes, 1),
                java.time.LocalDate.of(ano, mes, java.time.YearMonth.of(ano, mes).lengthOfMonth())
        );
        return ResponseEntity.ok(response);
    }
}
