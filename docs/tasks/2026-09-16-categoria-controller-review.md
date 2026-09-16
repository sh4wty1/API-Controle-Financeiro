# Revisão do CategoriaController e atualização do roadmap

**Por quê:** usuário escreveu o `CategoriaController` (Passo 3 do roteiro de estudo) e pediu revisão + explicação dos erros de compilação, seguindo a regra do projeto de que a IA só explica/revisa, não escreve código (`docs/GUIDE.md`).

**O quê:** guiado por explicações (sem escrever código), o usuário corrigiu, na ordem: erro de sintaxe/tipo no `create` (argumentos com tipo literal e retorno `Categoria` vs `CategoriaResponse`), ausência de `@PathVariable`/path `/{id}` em `findById`/`update`/`delete` (mapeamento ambíguo entre `findAll` e `findById`), uso incorreto de `HttpStatus.FOUND` (302) em vez de `OK` (200), e `update` recebendo campos soltos em vez de `@RequestBody CategoriaRequest`. `CategoriaController` ficou com os 5 endpoints do CRUD, todos devolvendo DTO. `docs/ROADMAP.md` atualizado: Passo 3 marcado como concluído, com as pegadinhas aprendidas registradas.

**Como:** revisão manual do arquivo (`Read`) a cada iteração; erro de compilação real capturado com `./mvnw -o compile` numa etapa para confirmar a mensagem exata do compilador; edição do `docs/ROADMAP.md` (`Edit`) atualizando o checklist e a seção de progresso do Passo 3.

**Verificação:** revisão visual final do `CategoriaController.java` — sem problemas pendentes da checklist anterior (mapeamento, status code, path variable, DTO no update). Não executei build/testes após a última rodada de ajustes (não pedido).

**Pendências:** nenhuma no Passo 3. Próximo: Passo 4 — Tratamento de erros (`@RestControllerAdvice`, `@ExceptionHandler`, `ProblemDetail`).
