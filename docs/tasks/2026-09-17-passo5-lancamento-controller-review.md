# Revisão do LancamentoController (Passo 5)

**Por quê:** usuário escreveu o `LancamentoController` sozinho (Passo 5 do roteiro) e pediu revisão para saber se o passo estava fechado, seguindo a regra do projeto de que a IA só explica/revisa, não escreve código (`docs/GUIDE.md`).

**O quê:** revisão apontou, em ordem de gravidade: (1) dois `@GetMapping` sem path próprio (`findALL`/`findById`) causando `Ambiguous mapping` — a app não subia; (2) `LancamentoNaoEncontradoException` não registrada no `GlobalExceptionHandler`, devolvendo 500 em vez de 404; (3) POST devolvendo `HttpStatus.OK` em vez de `CREATED`; (4) `update` resolvendo a `Categoria` no controller via `CategoriaService`, vazando a entidade pra camada web e divergindo do `create` (que passa `categoriaId` e deixa o service resolver); (5) listagem com filtro de mês/categoria e `Pageable` ainda inexistente — os dois métodos `Page<Lancamento>` do repository não são chamados por ninguém; (6) `existsByDescricaoAndIdNot` morta no repository. O usuário corrigiu o item (1) (`@GetMapping("{id}")` + remoção do import morto de `CategoriaResponse`) e commitou em `6c7da06`. Passo 5 continua **em andamento**.

**Como:** leitura dos arquivos do pacote `lancamento` (entidade, repository, service, DTOs), do `GlobalExceptionHandler` e do `CategoriaController`/`CategoriaService` como referência de padrão; `docs/ROADMAP.md` lido para confrontar o entregue com o que o passo pedia. Nenhum arquivo de código alterado pela IA — só explicação.

**Verificação:** `./mvnw -o compile` passou (exit 0) antes do commit, confirmando que os problemas eram de runtime/semântica, não de compilação. O mapeamento ambíguo foi confirmado por leitura (é o mesmo caso já registrado no Passo 3), não por subida da app — banco não estava rodando nesta sessão.

**Pendências:** itens (2) a (6) acima. Para fechar o Passo 5 faltam o handler da `LancamentoNaoEncontradoException`, a listagem filtrada por mês (obrigatório) e categoria (opcional) com `Pageable`, e `@Transactional` nos métodos de escrita do service. `docs/ROADMAP.md` não foi atualizado — o passo não fechou.
