# Roadmap

Onde eu estou no projeto. Atualizar ao fim de cada sessão de estudo.
Conceitos e regras ficam no [GUIDE.md](GUIDE.md), aqui é só progresso e decisões.

> **Retomando com IA:** "Leia `docs/GUIDE.md` e `docs/ROADMAP.md` e me ajude a continuar de onde parei."

**Última atualização:** 19/09/2026, passo 5 fechado: os 500 e o acabamento pendentes foram corrigidos e testados (400 em `valor <= 0`, 409 ao apagar categoria em uso, mensagens em português, `Page` via DTO). Próximo: Passo 6 (relatório) e Passo 7 (testes). Front de verdade adiado (ver Passo 8)

---

## Progresso

### Passo 0: Setup ✅
- [x] Projeto gerado no start.spring.io (Boot 4.1.1, Java 21, Maven)
- [x] Pacote `dev.fassi.financas`
- [x] JDK 21 configurado no IntelliJ
- [x] README
- [x] Primeiro commit do projeto

### Passo 1: Postgres + app vazia rodando ✅
- [x] Instalar Docker Desktop no PC de casa
- [x] Escrever `docker-compose.yml` na raiz (modelo no GUIDE)
- [x] `docker compose up -d`
- [x] Configurar `application.yaml`: `datasource.url/username/password`, `jpa.hibernate.ddl-auto: validate`, `jpa.show-sql: true`
- [x] Rodar `FinancasApplication` e ler o log/erro com calma
- [x] ~~Apagar `src/main/resources/templates/`~~ → decidi manter, quero experimentar front aqui também (ver Decisões)
- [x] Responder: quais 3 anotações `@SpringBootApplication` agrupa? Qual faz o scan dos pacotes?
  → `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan` (essa faz o scan)

### Passo 2: Migration `V1__criar_tabelas.sql` ✅
- [x] Criar em `src/main/resources/db/migration/`
- [x] `categoria`: id identity, nome (unique, not null), tipo com `CHECK (tipo IN ('RECEITA','DESPESA'))`
- [x] `lancamento`: id identity, descricao, valor `NUMERIC(19,2)`, data `DATE`, FK `categoria_id`
- [x] Flyway aplicou na subida da app (`flyway_schema_history` versão 1, success)
- FK sem `ON DELETE` → o próprio banco já barra apagar categoria com lançamentos

### Passo 3: Categoria ponta a ponta ✅
- [x] `Categoria` (@Entity): id identity, nome/tipo, validação centralizada no método `atualizar` (chamado também pelo construtor)
- [x] `CategoriaRepository` (extends JpaRepository<Categoria, Long>)
- [x] `CategoriaService`: create, findById, getAll, update, delete
- [x] `CategoriaController` (@RestController) + DTOs (`dto/CategoriaRequest`, `dto/CategoriaResponse`) — entidade não pode sair do controller
- Lembrar amanhã: `orElseThrow` desembrulha `Optional<T>` pra `T` — o método precisa retornar `T`, não `Optional<T>`
- Padrão adotado: sem setters soltos na entidade — só `atualizar(nome, tipo)`, que valida e é reaproveitado pelo construtor
- Pegadinhas do controller: `.body(x)` é o `T` genérico do `ResponseEntity<T>` — o tipo de retorno declarado no método já fixa esse `T`, então o argumento passado tem que bater (não dá pra devolver `Categoria` num método que promete `ResponseEntity<CategoriaResponse>`)
- Duas rotas `@GetMapping` sem path próprio colidem (`Ambiguous mapping` na subida) — `findById`/`update`/`delete` precisam de `/{id}` no mapping + `@PathVariable Long id` no parâmetro
- `HttpStatus.FOUND` é 302 (redirecionamento), não "encontrado" — GET/PUT/DELETE que devolvem o recurso usam `HttpStatus.OK`
- Campo injetado no controller deveria ser `private final`, não `public final` (encapsulamento)
### Passo 4: Tratamento de erros ✅
- [x] `shared/CategoriaNaoEncontradaException` e `shared/CategoriaNomeDuplicadoException` (RuntimeException)
- [x] `shared/GlobalExceptionHandler` (@RestControllerAdvice): 404 pra não encontrada, 409 pra nome duplicado, 400 pra `MethodArgumentNotValidException`
- [x] `CategoriaRepository.existsByNome` (query derivada do nome do método, sem corpo)
- [x] `CategoriaService.create` checa duplicado e lança `CategoriaNomeDuplicadoException` antes de salvar; `findById` lança `CategoriaNaoEncontradaException` (cobre `update`/`delete` de graça, os dois chamam `findById`)
- [x] Removido `CategoriaRepository.id(Long id)` — método sobrando, nome não batia com convenção de query do Spring Data (quebraria a subida da app)
- [x] Handler de validação trocado de `getFieldError()` (só o primeiro) pra `getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "))` — devolve todas as mensagens de validação de uma vez
- [x] `update` agora checa duplicado com `existsByNomeAndIdNot(nome, id)` — exclui a própria categoria da comparação, senão bloqueava atualizar mantendo o mesmo nome
- `throw new X(...)` não precisa de `return`: interrompe o método e sobe a exceção, não devolve valor
- `.orElseThrow()` é método de `Optional` — não existe em `Categoria`/`List`, só em quem já é `Optional<T>` (ex: `repository.findById(id)`)
### Passo 5: Lançamentos + regras ✅
- [x] `Lancamento` (@Entity): id identity, descricao/valor/data/categoria, validação centralizada no método `atualizar` (chamado também pelo construtor), sem setters soltos — mesmo padrão da `Categoria`
- [x] `LancamentoRepository` (extends JpaRepository<Lancamento, Long>): `findByCategoriaIdAndDataBetween` e `findByDataBetween`, os dois com `Pageable`/`Page<Lancamento>`, ambos chamados pelo `filterByMonth` do service. `existsByDescricaoAndIdNot` removida (dois lançamentos com a mesma descrição são válidos)
- [x] `LancamentoService`: `create`, `findById`, `getAll`, `filterByMonth`, `update`, `delete` — mesmo padrão do `CategoriaService` (`findById` lança `LancamentoNaoEncontradoException` via `orElseThrow`, cobre `update`/`delete` de graça). `resolverCategoria` privado compartilhado por `create` e `update`
- [x] `shared/LancamentoNaoEncontradoException` (RuntimeException) — registrada no `GlobalExceptionHandler` devolvendo `NOT_FOUND`
- [x] DTOs `lancamento/dto/LancamentoRequest` e `lancamento/dto/LancamentoResponse` — `categoriaId` (`Long`), não a entidade `Categoria` inteira (entidade não pode sair do controller, mesma regra da `Categoria`)
- [x] `LancamentoController` — CRUD completo (`create`/`findAll`/`findById`/`update`/`delete`) + `filterByMonth` (`@GetMapping(params = "mes")`: `YearMonth mes` obrigatório, `Long categoriaId` opcional, `Pageable`, retorno `Page<LancamentoResponse>` via `.map(LancamentoResponse::new)`)
- [x] POST devolve `CREATED`; `update` recebe `categoriaId` e o service resolve a `Categoria` (sem `CategoriaService` no controller)
- [x] `@Transactional` no `update` de `LancamentoService` e `CategoriaService`, `save()` removido dos dois
- [x] Limpezas: `findALL` → `findAll`, `/{id}` em todos os mappings
- [x] **Teste manual** contra o banco real (18/09, via `curl` e pela página `static/index.html`): todas as rotas OK — 201 nos POST, filtro de mês com e sem `categoriaId`, `page`/`size`/`sort`, mês sem dados e `categoriaId` inexistente devolvem 200, 404 nos inexistentes, 409 em nome duplicado, `PUT` com o mesmo nome passa. `mes` no formato `yyyy-MM` converteu sem `@DateTimeFormat`
- [x] `valor <= 0` (e qualquer `IllegalArgumentException` da entidade) devolvia 500, agora 400: `@ExceptionHandler(IllegalArgumentException.class)` no `GlobalExceptionHandler`, mais `@Positive` no `LancamentoRequest`
- [x] `DELETE /categorias/{id}` com lançamentos devolvia 500, agora 409: `CategoriaService.delete` faz `findById` (404) e checa `LancamentoRepository.existsByCategoriaId` antes de apagar, lançando `shared/CategoriaEmUsoException` (handler devolve `CONFLICT`)
- [x] Mensagens de validação em português: `message = "..."` nas anotações dos DTOs
- [x] Aviso `Serializing PageImpl instances as-is is not supported` resolvido com `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` na `FinancasApplication`. O JSON do `Page` mudou: `content` + objeto `page` (`size`, `number`, `totalElements`, `totalPages`)
- Exceção sem `@ExceptionHandler` vira 500 com o JSON padrão do Spring (`timestamp`, `status`, `error`, `path`), sem mensagem útil; `mes=abc` cai no mesmo formato, com 400
- Estado do banco da VPS depois do teste: categoria 1 "Salário" e lançamento 1 "Mercado (editado)" (dados de teste)
- Decisão: filtro por categoria na listagem é **opcional**, filtro por mês é **obrigatório** (`YearMonth`, não datas soltas — intervalo sempre válido, sem validar `inicio <= fim`). O service decide qual método do repository chamar dependendo se `categoriaId` veio na requisição
- Decisão: `GET /lancamentos` sem filtro **continua existindo** ao lado do filtrado por mês; o Spring roteia pelo parâmetro `mes` (`params = "mes"`)
- `categoriaId` inexistente no filtro devolve página vazia com 200, não 404 — filtrar por algo que não existe é resposta vazia válida (não chama `resolverCategoria` no filtro)
- `@Transactional` e o `save()` que sobra: sem a anotação, cada chamada de repository abre a própria transação, então `findById` devolve a entidade *detached* e o `save` é quem reconecta. Com a anotação a entidade fica *managed* do início ao fim, o `atualizar` mexe num objeto que o Hibernate já rastreia e o *dirty checking* dispara o `UPDATE` no commit. Vale só pra `update` — `create` (INSERT) e `delete` (`deleteById`) não dependem de dirty check
- `Page` é imutável: converter itens com `page.map(...)` mantém `totalElements`/`totalPages`. `for` + `ArrayList` gera `List` e perde a paginação; `getContent()` também
- Duas rotas `@GetMapping` sem path colidem; `@GetMapping(params = "mes")` desempata pela presença do parâmetro na query
- Autocomplete do IntelliJ injetou `PlaceholderConfigurerSupport` no construtor do `LancamentoController` (import errado, mesma família dos `Page` homônimos): a app quebraria na subida com bean não encontrado. Conferir imports e construtor depois de aceitar sugestão
- Spring Data recente avisa no log ao serializar `PageImpl` direto; se aparecer, `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` ou `PagedModel`
- Decisão: toda transação sempre tem categoria (nunca `null`) — se não tiver uma específica, cadastro uma categoria "Outro"
- `valor`: validação certa é `valor.compareTo(BigDecimal.ZERO) <= 0` pra garantir positivo — a primeira tentativa comparava `valor.toString().isBlank()`, que nunca disparava (um `BigDecimal` nunca gera string vazia, nem sendo zero ou negativo)
- `data` é `LocalDate`, não `LocalDateTime` — a coluna no banco é `DATE` (migration `V1`), tipo incompatível quebraria a subida com `ddl-auto: validate`
- Pegadinha de import: `Page` e `Pageable` têm homônimos em outros pacotes que o autocomplete sugere por engano — `org.hibernate.query.Page` (não tem generics, é só Hibernate puro) e `java.awt.print.Pageable` (impressão AWT, nada a ver com paginação de dados). Os certos são `org.springframework.data.domain.Page` e `org.springframework.data.domain.Pageable`
- Pegadinha de query method: a ordem dos parâmetros do método tem que bater com a ordem das condições no **nome** (`findByXAndY` → 1º parâmetro é X, 2º é Y), não a ordem que parece mais natural de escrever
- `JpaRepository.findById` sempre devolve `Optional<T>`, nunca `T` direto — mesma pegadinha do `orElseThrow` do Passo 3, mas agora no `LancamentoService.create` (precisava desembrulhar o `Optional<Categoria>` antes de passar pro construtor de `Lancamento`, que pede `Categoria`)
- `throw` é **statement**, não **expression**, em Java — não dá pra usar num ternário (`cond ? valor : throw new X()` não compila). É por isso que `Optional.orElseThrow(() -> new X())` existe: o `if/else` que decide lançar ou não fica escondido na implementação do `Optional`, e o lambda só *produz* a exceção, não lança sozinho
- DTO não pode carregar a entidade inteira: primeira versão de `LancamentoRequest`/`LancamentoResponse` guardava `Categoria categoria` em vez de `Long categoriaId` — quebrava a regra "entidade não sai do controller" e não batia com a assinatura de `LancamentoService.create` (que pede `Long categoriaId`)
- Construtor extra de um record (ex: `LancamentoResponse(Lancamento lancamento)`) não dá acesso aos nomes dos componentes como variável — só o construtor canônico (gerado a partir da lista de componentes) tem isso. Dentro do construtor extra, o único parâmetro que existe é o que você declarou nele

### Passo 6: Relatório
- Endpoint `GET /relatorios/mensal?ano=2026&mes=9`: total de receitas, total de despesas, saldo e total por categoria (pacote `relatorio/`)
- Agregação no banco: `@Query` com JPQL e `SUM`/`GROUP BY` no repository (query method derivado não agrega). JPQL usa entidade e campo (`l.categoria.tipo`), não tabela e coluna
- O tipo (receita/despesa) vem da categoria, `Lancamento` não tem `tipo`: dá pra fazer uma query agrupada por categoria e somar no service, ou duas queries. Decisão minha
- Projeção em record: *constructor expression* (`select new ...`, com o nome completo do pacote do record)
- `SUM` devolve `null` quando não há linhas: tratar como `BigDecimal.ZERO`. Saldo = receitas menos despesas com `.subtract(...)`
- Intervalo do mês: `YearMonth.of(ano, mes)` + `atDay(1)`/`atEndOfMonth()`, como no `filterByMonth`. `mes=13` lança `DateTimeException`: validar ou tratar no handler
- Mês sem dados: 200 com zeros e lista vazia, não 404 (mesma lógica do `categoriaId` inexistente no filtro)
- Ordem sugerida: record de resposta, `@Query`, service, controller
### Passo 7: Testes

### Passo 8: Front (adiado)
- `src/main/resources/static/index.html` já existe, mas é só um **console de teste da API**, gerado pela IA por liberação minha (ver `docs/tasks/2026-09-18-static-teste-api.md`), não o front de verdade. Servido pelo Spring em `localhost:8080`, mesma origem, sem CORS
- Front de verdade: projeto separado, Bun + Vite + React + TypeScript. Briefing pra sessão dele em [frontend-briefing.md](frontend-briefing.md) (copiar como `CLAUDE.md` na raiz do projeto novo). Front separado traz o CORS de volta: resolver com proxy do Vite, prefixo `/api`
- Depois: talvez um projeto com web e app juntos. A API é a mesma pra qualquer cliente
- Antes de qualquer front em produção falta autenticação (Spring Security), fora da v1

---

## Decisões tomadas

| Decisão | Motivo |
|---|---|
| IntelliJ com keymap padrão | Aprender os atalhos nativos |
| Autocomplete de IA desligado (Full Line / AI Assistant) | Regra do GUIDE: IA não escreve código. Code completion normal (`Ctrl+Space`) fica ligado |
| Banco via Docker **no PC de casa** | PC do trabalho: licença do Docker Desktop + política de TI |
| Banco de dev na VPS (container `financas-db-db-1`), por túnel SSH | Em 18/09, nesta máquina Linux não há Docker. A porta 5432 da VPS é de outro projeto: o túnel aponta pra 5433. Senha só no compose da VPS, via `SPRING_DATASOURCE_PASSWORD` |
| Front de verdade em projeto separado (Bun + Vite + React + TS), **adiado** | Mercado. `static/index.html` fica só como console de teste da API. O GUIDE segue com "Fora da v1: front" |
| Manter blocos vazios do `pom.xml` (`<licenses/>` etc.) | Evitam herdar licença/devs do parent (ver HELP.md) |

Banco na VPS (adotado em 18/09): container `financas-db-db-1` em `127.0.0.1:5433` da VPS, compose em `/opt/financas-db/docker-compose.yml`, túnel `ssh -N -L 5432:127.0.0.1:5433 fassi-vps` (o exemplo antigo com `5432:localhost:5432` cairia no banco de outro projeto). Outras alternativas: Neon/Supabase, ou H2.

---

## Setup numa máquina nova

1. Instalar JDK 21, IntelliJ e Docker Desktop (WSL2)
2. `git clone` e abrir no IntelliJ: **File → Open → `pom.xml` → Open as Project**
3. `Ctrl+Alt+Shift+S` → Project → SDK **21**, Language level **SDK default** → Apply
4. Esperar o Maven sincronizar (se não tiver nada pra baixar, some rápido sem barra de progresso)

---

## Pegadinhas que já aprendi

- **Editar `static/` e não ver mudança**: o Spring serve de `target/classes`, não de `src/`. `Ctrl+F9` (Build) no IntelliJ ou `./mvnw process-resources`, depois F5 no navegador
- **Subir a app com o banco da VPS**: abrir o túnel (`ssh -N -L 5432:127.0.0.1:5433 fassi-vps`) e passar `SPRING_DATASOURCE_PASSWORD` na run configuration do IntelliJ. Nunca a senha no `application.yaml`, ele é commitado. Se a 8080 estiver ocupada, tem outra instância da app rodando
- **Pacote com hífen quebra tudo**: pasta = pacote, e hífen é inválido. Sintoma: `New → Java Class` some do menu
- **Atalhos não funcionam com foco no terminal**: `Esc` volta pro editor. Settings → Tools → Terminal → desmarcar "Override IDE shortcuts"
- **`Shift Shift`**: busca qualquer ação e mostra o atalho dela
- **`Alt+Enter`**: quick fix de qualquer coisa vermelha/amarela
- **Nome do módulo** no IntelliJ pode ficar desatualizado. É só um rótulo. Pra corrigir: fechar IDE, apagar `.idea/`, reabrir pelo `pom.xml`
- **`contextLoads` falha sem banco rodando**: afeta `mvnw test` e `mvnw package`
- **`POSTGRES_*` no compose só valem na 1ª inicialização**: mudou senha? `docker compose down -v`
- **Git não versiona pasta vazia**: `static/` e `db/migration/` só aparecem no repo quando tiverem arquivo
- **Docker Desktop com erro `HCS_E_HYPERV_NOT_INSTALLED` / "WSL2 is not supported"**: hypervisor desligado no boot. Corrige com `bcdedit /set "{current}" hypervisorlaunchtype auto` (admin) + reiniciar o PC
- **Erro de autenticação `FATAL: password authentication failed` mesmo com usuário/senha certos no `application.yaml`**: pode ter um Postgres nativo instalado no Windows ocupando a porta 5432 e roubando a conexão do container. Checar com `netstat -ano | findstr :5432` e `Get-Service *postgres*`; parar o serviço nativo se for o caso
