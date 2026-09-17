# Passo 4 do roteiro: tratamento de erros da Categoria

**Por quê:** `CategoriaService` lançava `IllegalArgumentException` genérica pra "não encontrada" e não tinha checagem de nome duplicado nem tratamento de validação — qualquer um desses casos virava um 500 cru pro cliente. Passo 4 do roteiro de estudo (`docs/ROADMAP.md`) pede `@RestControllerAdvice`/`@ExceptionHandler`/`ProblemDetail` pra resolver isso.

**O quê:** usuário escreveu, guiado por explicações (regra do projeto: IA só explica/revisa, não escreve código — `docs/GUIDE.md`): exceções de domínio `CategoriaNaoEncontradaException` e `CategoriaNomeDuplicadoException` (`shared/`), `GlobalExceptionHandler` (@RestControllerAdvice) com handlers pra 404/409/400 (validação via `MethodArgumentNotValidException`), método `CategoriaRepository.existsByNome` (query derivada, sem `ProblemDetail` — optou por manter `ResponseEntity<String>` igual aos outros retornos), e ligou tudo no `CategoriaService` (`findById` centraliza a exceção de não-encontrada pra `update`/`delete` também; `create` checa duplicado antes de salvar). Também removido um método quebrado (`CategoriaRepository.id(Long id)`, nome fora da convenção do Spring Data, ia quebrar a subida da app) que não tinha relação com a tarefa mas foi achado na revisão.

**Como:** revisão manual (`Read`) a cada iteração de código escrita pelo usuário; erro de sintaxe/tipo explicado conceitualmente (sem reescrever) — `Optional.orElseThrow()` não existe em `Categoria`, `throw` não precisa de `return`. Atualização de `docs/ROADMAP.md` (Passo 4 marcado ✅, pegadinhas e pendências registradas).

**Verificação:** revisão visual do `CategoriaService.java` e `CategoriaRepository.java` finais — sem problemas bloqueantes. Não executei build/testes (não pedido nesta sessão).

**Pendências:** nenhuma. Handler de validação trocado de `getFieldError()` pra `getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "))` (17/09/2026) — devolve todas as mensagens de validação, não só a primeira. Revisão também apontou e removeu um `Objects.requireNonNull(...)` desnecessário ao redor do resultado de `joining` (nunca retorna null).

**Resolvido depois do registro inicial:** import não usado removido; `update` agora checa duplicado com `existsByNomeAndIdNot(nome, id)` (excluindo a própria categoria da comparação — a primeira tentativa com `existsByNome` bloqueava manter o mesmo nome no update).
