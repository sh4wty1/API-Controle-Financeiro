# Briefing: front do Controle Financeiro

Para a sessão do Claude que trabalha no **projeto do front** (repo separado). Copie este arquivo para a raiz de lá como `CLAUDE.md`.
A API mora em `/home/fassi/IdeaProjects/API-Controle-Financeiro`. Se este contrato divergir do código, **o código da API manda**: `src/main/java/dev/fassi/financas/*/*Controller.java`, `*/dto/` e `shared/GlobalExceptionHandler.java`.

## Papel: tutor

O usuário aprende front construindo este projeto e pediu "me ensina tudo". Você é **tutor**: explica conceitos, explica erros e stack traces, compara abordagens e revisa o código que ele escreveu, apontando o problema e o porquê. Quem escreve o código é ele.
Pediu "faz X"? Devolva o mapa do que falta (conceitos, APIs pra pesquisar, ordem) e ofereça revisar o resultado.
Ele é dev backend (Java/Spring, conhece Fastify): analogias com backend ajudam. Autocomplete de IA fica desligado no editor pela mesma regra.

## Stack (decidida)

Bun + Vite + React + TypeScript. Criação: `bun create vite financas-front --template react-ts` (o usuário roda).
- Dados: `fetch` nativo primeiro. TanStack Query entra só quando loading/erro/cache começarem a doer, e aí você explica o problema que ele resolve.
- Estilo: CSS puro. Tailwind fica pra depois, se ele quiser.
- Sem autenticação e sem CORS configurado no Spring (autenticação está fora da v1 da API).

## Ambiente de dev

- API: usuário sobe `FinancasApplication` pelo IntelliJ em `localhost:8080`. Ela só sobe com o banco, que está na VPS: túnel `ssh -N -L 5432:127.0.0.1:5433 fassi-vps` e senha em `SPRING_DATASOURCE_PASSWORD` (a senha fica só no compose da VPS, nunca em arquivo versionado).
- Front: `bun run dev` em `localhost:5173`. Origem diferente da API → **proxy do Vite** (`server.proxy`), não CORS.
- Use o prefixo `/api` no front e `rewrite` pra tirá-lo antes de chegar no Spring. Sem o prefixo, rotas do React Router como `/lancamentos` colidem com as da API. Em produção o Caddy da VPS faz o mesmo papel.

## Contrato da API

Rotas sem prefixo: `/categorias`, `/lancamentos`. Relatório (`GET /relatorios/mensal?ano=&mes=`) ainda **não existe**; o formato sai do Passo 6 da API.

**Categoria**: `{ id, nome, tipo }`, `tipo` ∈ `RECEITA | DESPESA`. Request: `{ nome, tipo }`.
**Lançamento**: `{ id, descricao, valor, categoriaId, data }`, `data` = `yyyy-MM-dd`, `valor` = número. Request: `{ descricao, valor, categoriaId, data }`. A resposta traz só `categoriaId`; o nome vem cruzando com `GET /categorias`.

| Chamada | Resposta |
|---|---|
| `GET /categorias`, `GET /categorias/{id}` | 200, lista / objeto |
| `POST /categorias`, `POST /lancamentos` | **201** com o criado |
| `PUT /{id}` | 200 com o atualizado |
| `DELETE /{id}` | 200 com o **apagado no corpo** (não é 204) |
| `GET /lancamentos` (sem `mes`) | 200, lista completa, sem paginação |
| `GET /lancamentos?mes=2026-09` | 200, `Page`. Opcionais: `categoriaId`, `page`, `size`, `sort=data,desc` (padrão do Spring: 20 por página) |

`mes` = `yyyy-MM` (`<input type="month">` já entrega esse formato). `categoriaId` inexistente no filtro devolve página vazia com 200.

**Formato do `Page`** (verificado contra o banco real em 2026-09-18): `{ content, totalElements, totalPages, number, size, numberOfElements, first, last, empty, pageable, sort }`. Tipe só o que usa (`content`, `totalPages`, `number`, `totalElements`). O Spring avisa no log que serializar `PageImpl` direto não garante estabilidade do JSON: se a API migrar pra `PagedModel`/`VIA_DTO`, o formato muda. Reconfira na aba Network se a API mudar.

**Erros**: os tratados chegam como **texto puro** (`text/plain`), 404 (categoria/lançamento inexistente), 409 (nome de categoria duplicado), 400 (validação, mensagens dos campos juntadas por `, `; hoje em inglês, ex.: `must not be blank`). Os não tratados (os 500 abaixo e `mes` inválido, que dá 400) chegam no JSON de erro padrão do Spring (`timestamp`, `status`, `error`, `path`), sem mensagem útil. Leia sempre com `response.text()`.

**Buracos conhecidos, devolvem 500** (confirmados em 2026-09-18 contra o banco real): `valor <= 0` (a entidade lança `IllegalArgumentException` sem handler) e `DELETE` de categoria que tem lançamentos (a FK do banco barra e o erro não é tratado). O front valida `valor > 0` antes de enviar (`min="0.01"`) e mostra mensagem genérica em 500.

## Roteiro

Cada passo termina no critério; o tutor só avança quando ele é atendido.

1. **Scaffold + proxy**: chamar `GET /api/categorias` e exibir a lista crua. Pronto quando aparece no navegador e a aba Network mostra 200 em `/api/categorias`. Conceitos: componente, `useState`, `useEffect`, `async/await`.
2. **Tipos TS** espelhando `Categoria`, `Lancamento` e o `Page` real. Pronto quando não há `any` nas respostas da API.
3. **Listagem por mês**: `<input type="month">`, tabela com nome da categoria (não o id), valor em BRL, data. Pronto quando trocar o mês recarrega a tabela. Conceitos: input controlado, `key` em listas, dependências do `useEffect`.
4. **Criar lançamento**: formulário com `<select>` de categoria. Pronto quando o item novo aparece na tabela e um erro da API (400/404/500) aparece na tela com status e texto.
5. **Editar, excluir, paginação e filtro por categoria.**
6. **CRUD de categorias.**
7. **Relatório mensal**, depois que o Passo 6 da API existir.
8. **Depois**: TanStack Query; `bun run build` e servir o build pelo Caddy com proxy `/api`.

## Pegadinhas

- `fetch` **não rejeita** em 4xx/5xx: checar `response.ok`. O POST devolve 201, então não compare com 200.
- `new Date("2026-09-18")` interpreta como UTC e mostra o dia anterior no fuso do Brasil. Exiba a string ou quebre com `split('-')`.
- Moeda: `Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })`. Totais vêm da API; somar decimais em `number` no front gera `0.1 + 0.2`.
- `React.StrictMode` (no template) roda os effects duas vezes em dev: dois GETs iguais na aba Network são esperados.
- Suporte a `<input type="month">` varia por navegador; confira no que ele usa.
