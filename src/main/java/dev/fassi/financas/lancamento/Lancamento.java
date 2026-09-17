package dev.fassi.financas.lancamento;

import dev.fassi.financas.categoria.Categoria;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    protected Lancamento() {}

    public Lancamento(String descricao, BigDecimal valor, Categoria categoria, LocalDate data) {
        atualizar(descricao, valor, categoria, data);
    }

    public void atualizar(String descricao, BigDecimal valor, Categoria categoria, LocalDate data) {
        if (descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo");
        }

        if (data == null) {
            throw new IllegalArgumentException("A data não pode ser vazia");
        }

        if (categoria == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula");
        }

        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
