# Guia: API de Controle Financeiro (Spring Boot)

Objetivo: aprender Java + Spring escrevendo tudo na mão. IA só explica, não escreve.

---

## Escopo da v1

- [ ] **Categorias**: CRUD com `nome` e `tipo` (`RECEITA` | `DESPESA`)
- [ ] **Lançamentos**: CRUD com `descricao`, `valor`, `data`, `categoria`
    - listagem com filtro por mês e categoria + paginação
- [ ] **Relatório mensal**: `GET /relatorios/mensal?ano=2026&mes=9`
    - total receitas, total despesas, saldo, total por categoria

### Regras de negócio
- valor sempre positivo
- categoria precisa existir
- tipo do lançamento deve bater com o tipo da categoria
- categoria com lançamentos não pode ser apagada

Fora da v1: autenticação, Lombok. O front é um projeto de estudo à parte, em Angular, na pasta `web/` deste mesmo repo (ver `ROADMAP.md`, Passo 8, e `frontend-briefing-angular.md`).

---

## Setup

- **start.spring.io**: Maven, Java 21 ou 25, Boot estável
- **Dependências**: Spring Web, Spring Data JPA, PostgreSQL Driver, Flyway Migration, Validation
- **VSCode**: Extension Pack for Java + Spring Boot Extension Pack

```yaml
# docker-compose.yml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: financas
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    ports:
      - "5432:5432"
```

No `application.yml`, além da conexão:
- `spring.jpa.hibernate.ddl-auto: validate` → Flyway cria, Hibernate só confere
- `spring.jpa.show-sql: true` → ver as queries geradas

---

## Estrutura (por feature)

```
com.seunome.financas
├── categoria/    Categoria, CategoriaRepository, CategoriaService, CategoriaController, dto/
├── lancamento/
├── relatorio/
└── shared/       exceções de domínio, tratamento de erros
```

---

## Roteiro

| # | Passo | Conceitos pra entender |
|---|-------|------------------------|
| 1 | Subir Postgres e rodar app vazia | `@SpringBootApplication`, auto-configuration, `application.yml` |
| 2 | Migration `V1__criar_tabelas.sql` | Flyway, versionamento, `NUMERIC(19,2)` |
| 3 | Categoria ponta a ponta | `@Entity`, `JpaRepository`, `@Service`, `@RestController`, `record` como DTO, `@Valid` |
| 4 | Tratamento de erros | `@RestControllerAdvice`, `@ExceptionHandler`, `ProblemDetail` |
| 5 | Lançamentos + regras | `@ManyToOne`, `BigDecimal`, `LocalDate`, `Pageable`, `@Transactional` |
| 6 | Relatório | JPQL, `SUM` / `GROUP BY`, projeção em record |
| 7 | Testes | JUnit 5, Mockito, `@WebMvcTest`, `@DataJpaTest` |

---

## Tradução Fastify → Spring

| Fastify (na mão) | Spring |
|------------------|--------|
| composition root | container de IoC (injeção via construtor) |
| rotas | `@GetMapping`, `@PostMapping`... |
| schema de validação | Bean Validation (`@NotBlank`, `@Positive`...) |
| error handler | `@RestControllerAdvice` |
| hooks / middleware | `Filter` / `HandlerInterceptor` |
| query SQL / query builder | Spring Data JPA / JPQL |
| migrations | Flyway |

---

## Regras que eu sigo

- Injeção **via construtor**, campos `final`, sem `@Autowired` em campo
- **Nunca** `double`/`float` pra dinheiro → `BigDecimal`
- Entidade não sai do controller → sempre DTO
- Regra de negócio fica no **service**, não no controller
- Toda anotação nova: entender o que ela faz por baixo antes de seguir

---

## Como usar a IA

**Pode:**
- explicar um conceito ou anotação
- explicar uma mensagem de erro / stack trace
- comparar abordagens ("lock otimista vs pessimista")
- revisar código que **eu já escrevi**

**Não pode:**
- escrever a feature por mim
- gerar a classe pronta

**Prompts úteis:**
- "Me explique o que `X` faz por baixo, sem escrever código da minha solução."
- "Recebi este erro: [erro]. O que significa e onde devo procurar? Não me dê a correção pronta."
- "Escrevi isto: [código]. Aponte problemas e explique o porquê, sem reescrever."
- "Me faça 3 perguntas pra testar se entendi `@Transactional`."

---

## Depois da v1

- [ ] Spring Security + JWT
- [ ] Testcontainers nos testes de integração
- [ ] Swagger / OpenAPI
- [ ] Evoluir pra carteira Pix (ledger, concorrência, idempotência)