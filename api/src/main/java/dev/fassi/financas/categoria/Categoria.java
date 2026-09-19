package dev.fassi.financas.categoria;
import jakarta.persistence.*;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;

    protected Categoria() {
    }

    public Categoria(String nome, TipoCategoria tipo) {
        atualizar(nome, tipo);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }
    public void atualizar(String nome, TipoCategoria tipo) {
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        String strTipo = tipo.toString();
        if (strTipo.isBlank()) {
            throw new IllegalArgumentException("Tipo não pode ser vazio");
        }

        this.nome = nome;
        this.tipo = tipo;
    }


}
