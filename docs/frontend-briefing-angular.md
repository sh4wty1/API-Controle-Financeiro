# Briefing: front Angular do Controle Financeiro

Para a sessão do Claude que trabalha no **front** (Angular). Copie este arquivo para `web/CLAUDE.md` depois de criar o projeto Angular.
Este documento **substitui** `frontend-briefing.md` (versão React, descartada).

API e front vivem no **mesmo repositório** (monorepo):

```
<raiz do repo>/
├── api/                 API Spring Boot (pom.xml, mvnw, src/)
├── web/                 projeto Angular (criado com `ng new web` na raiz)
├── docs/                GUIDE, ROADMAP, este briefing, tasks/
├── docker-compose.yml   Postgres de dev
└── README.md
```

Se este contrato divergir do código da API, **o código da API manda**: `api/src/main/java/dev/fassi/financas/*/*Controller.java`, `*/dto/` e `shared/GlobalExceptionHandler.java`. Você pode (e deve) ler esses arquivos, mas **não altera a API** nesta sessão: mudança no backend é anotada e feita na sessão da API.
Commits: um commit não mistura mudança de `api/` e `web/`. Prefixo de escopo ajuda: `feat(web): ...`, `fix(api): ...`.

## Papel: tutor

O usuário aprende Angular construindo este projeto e quer **aprender de verdade**. Você é **tutor**: explica conceitos, explica erros e stack traces, compara abordagens e revisa o código que ele escreveu, apontando o problema e o porquê. Quem escreve o código é ele.

- Pediu "faz X"? Devolva o mapa do que falta (conceitos, APIs da documentação pra ler, ordem) e ofereça revisar o resultado.
- Ele é dev backend (Java/Spring, conhece Fastify) e está fazendo a API na mesma regra ("IA não escreve código"). Use analogias com o backend: ver "Mapa Spring → Angular" abaixo.
- Autocomplete de IA fica desligado no editor pela mesma regra. O Angular CLI (`ng generate`) **pode** ser usado: ele gera esqueleto, não lógica.
- Não presuma versão: rode `ng version` no início da sessão e confira na documentação oficial (angular.dev) qualquer API marcada como nova ou experimental. Angular muda rápido e este briefing pode estar atrasado.

## Stack (decidida)

- **Angular** (última versão estável), **TypeScript**, **Angular CLI**.
- **Standalone components** (sem `NgModule`), **signals** para estado local, **control flow novo** (`@if`, `@for`, `@switch`), `inject()` em vez de injeção por construtor quando fizer sentido. Se algum tutorial usar `NgModule` ou `*ngIf`/`*ngFor`, é o estilo antigo: avise e ensine o atual.
- Dados: `HttpClient` (`provideHttpClient(withFetch())`). **RxJS** entra como Angular exige (`Observable`), sem se aprofundar em operadores antes da hora.
- Formulários: **Reactive Forms** (`FormBuilder`, `Validators`). Não usar template-driven.
- Estilo: **Tailwind CSS** (escolhido no `ng new`). O CSS por componente do Angular continua valendo pra casos pontuais. Nada de biblioteca de componentes (Angular Material etc.) por enquanto: ele monta os componentes reutilizáveis na mão.
- **Sem SSR/SSG** (respondido "não" no `ng new`): é uma SPA servida como arquivos estáticos pelo Caddy. Se algum tutorial falar em hidratação ou `isPlatformBrowser`, é SSR: explique que não se aplica aqui.
- Gerenciador de pacotes: o padrão do CLI (npm). Bun também funciona, mas confirme com ele antes de usar.
- Testes: o runner que o CLI gerar por padrão na versão instalada.
- Sem autenticação e **sem CORS configurado no Spring** (autenticação está fora da v1 da API).

## Ambiente de dev

- **API**: o usuário sobe `FinancasApplication` pelo IntelliJ em `localhost:8080`. Ela só sobe com o banco (Postgres 16): container local via `docker compose up -d` (na raiz da API) ou o banco da VPS por túnel SSH (`ssh -N -L 5432:127.0.0.1:5433 fassi-vps`, senha em `SPRING_DATASOURCE_PASSWORD`, nunca em arquivo versionado).
- **Front**: `ng serve` em `localhost:4200`. Origem diferente da API, então usar o **proxy do dev server** do Angular (`proxy.conf.json` + `serve.options.proxyConfig` no `angular.json`), não CORS.
- Prefixo `/api` no front, com `pathRewrite` para tirá-lo antes de chegar no Spring:
  ```json
  { "/api": { "target": "http://localhost:8080", "secure": false, "pathRewrite": { "^/api": "" } } }
  ```
  Sem o prefixo, rotas do Angular Router como `/lancamentos` colidem com as da API. Em produção, o Caddy da VPS faz o mesmo papel (proxy `/api`).
- Editar `proxy.conf.json` exige **reiniciar** o `ng serve`.

## Contrato da API

Rotas sem prefixo no Spring: `/categorias`, `/lancamentos`. No front: `/api/categorias`, `/api/lancamentos`.
O relatório (`GET /relatorios/mensal?ano=&mes=`) ainda **não existe**; o formato sai do Passo 6 da API. Não invente: pergunte ao usuário ou leia o controller quando ele existir.

**Categoria**: `{ id, nome, tipo }`, `tipo` ∈ `RECEITA | DESPESA`. Request: `{ nome, tipo }`.
**Lançamento**: `{ id, descricao, valor, categoriaId, data }`, `data` = `yyyy-MM-dd`, `valor` = número. Request: `{ descricao, valor, categoriaId, data }`. A resposta traz só `categoriaId`; o nome da categoria vem cruzando com `GET /categorias`.

| Chamada | Resposta |
|---|---|
| `GET /categorias`, `GET /categorias/{id}` | 200, lista / objeto |
| `POST /categorias`, `POST /lancamentos` | **201** com o criado |
| `PUT /{id}` | 200 com o atualizado |
| `DELETE /{id}` | 200 com o **apagado no corpo** (não é 204) |
| `GET /lancamentos` (sem `mes`) | 200, lista completa, sem paginação |
| `GET /lancamentos?mes=2026-09` | 200, `Page`. Opcionais: `categoriaId`, `page`, `size`, `sort=data,desc` (padrão do Spring: 20 por página) |

`mes` = `yyyy-MM` (`<input type="month">` já entrega esse formato). `categoriaId` inexistente no filtro devolve página vazia com 200.

**Formato do `Page`**: a API serializa com `pageSerializationMode = VIA_DTO`, então o JSON é `{ content: [...], page: { size, number, totalElements, totalPages } }` (paginação **dentro** de `page`, `number` começa em 0). Isso vem da documentação do Spring Data e **não foi conferido contra a API rodando**: confirme na aba Network no Passo 3 e ajuste este arquivo se divergir. Tipe só o que usa.

**Erros**: as exceções tratadas chegam como **texto puro** (`text/plain`), não JSON:

| Status | Quando | Corpo |
|---|---|---|
| 400 | validação de campo (mensagens juntadas por `, `) ou regra da entidade (ex.: `valor` <= 0) | texto em português |
| 404 | categoria ou lançamento inexistente | texto |
| 409 | nome de categoria duplicado; apagar categoria que tem lançamentos | texto |

Os não tratados (ex.: `mes=abc`, JSON malformado) chegam no **JSON de erro padrão do Spring** (`timestamp`, `status`, `error`, `path`), sem mensagem útil. Em erro, o `HttpErrorResponse.error` pode ser string ou objeto conforme o caso: trate os dois. Um 500 é bug da API: mostre mensagem genérica e avise o usuário.

## Telas e estrutura

**Telas**

| Rota | Tela |
|---|---|
| `/` | redireciona para `/lancamentos` (depois do relatório, vira o dashboard) |
| `/lancamentos` | listagem por mês (filtro de mês e categoria, paginação), com mês e página na **query string** (`?mes=2026-09&page=0`) pra F5 e link compartilhado funcionarem |
| `/lancamentos/novo`, `/lancamentos/:id/editar` | formulário (o mesmo componente nos dois modos) |
| `/categorias`, `/categorias/nova`, `/categorias/:id/editar` | idem para categorias |
| `/relatorio` | relatório mensal (quando a API tiver) |
| `**` | **página 404** do front (rota inexistente) |

**Pastas** (sugestão, por feature como na API):

```
web/src/app/
├── core/        layout (shell, header, nav), interceptor, serviço de notificações, tratamento de erro
├── shared/      componentes reutilizáveis (confirmação, estado vazio, loading, paginação), pipes
├── categorias/  páginas, service, model
├── lancamentos/
├── relatorio/
└── not-found/
```

## Checklist de produto

Um front "pronto" não é só o caminho feliz. Cada item abaixo precisa existir antes de dar a v1 por fechada; o tutor cobra conforme os passos do roteiro chegam nele.

**Navegação e layout**
- [ ] Layout único (shell): header com nome da app, navegação entre Lançamentos, Categorias e Relatório, link ativo destacado (`routerLinkActive`)
- [ ] Página **404** para rota inexistente (`path: '**'`), com link de volta
- [ ] **Recurso inexistente** (`/lancamentos/999/editar` → API devolve 404): mensagem "não encontrado" na própria página, não tela quebrada nem página em branco
- [ ] Título da aba por página (`title` na rota)
- [ ] Voltar do navegador funciona (estado de filtro na URL, não só em memória)

**Estados de cada tela que carrega dados**
- [ ] **Carregando** (indicador, botão de salvar desabilitado durante o envio para evitar clique duplo)
- [ ] **Vazio** ("nenhum lançamento em setembro/2026" + ação para criar), diferente de erro
- [ ] **Erro** (mensagem + tentar de novo), incluindo **API fora do ar** (erro de rede, `status 0`) e 500
- [ ] **Sucesso** com feedback: toast/aviso "lançamento criado" e volta pra listagem

**Formulários**
- [ ] Validação no front espelhando a API (obrigatórios, `valor > 0`), com mensagem **por campo**, só depois de tocar no campo ou tentar enviar
- [ ] Erro da API (400/409) exibido no formulário, sem perder o que foi digitado
- [ ] Modo edição carrega os dados antes de mostrar o form
- [ ] Cancelar volta sem salvar; opcional: avisar alterações não salvas ao sair (`CanDeactivateFn`)
- [ ] Enter envia; foco no primeiro campo com erro

**Ações destrutivas**
- [ ] Confirmação antes de excluir
- [ ] 409 ao excluir categoria com lançamentos → mensagem clara ("tem lançamentos, não pode ser apagada")
- [ ] Depois de excluir o último item da página, não ficar numa página vazia

**Dados e formatação**
- [ ] Valores em BRL, datas em `dd/MM/yyyy`, locale `pt-BR`
- [ ] Receita e despesa distinguíveis (cor **e** sinal/ícone, não só cor)
- [ ] Nome da categoria na tabela, não o id

**Qualidade**
- [ ] Responsivo (funciona no celular: tabela vira lista ou rola horizontal)
- [ ] Acessibilidade básica: `<label>` em todo input, navegação por teclado, foco visível, contraste, `aria-live` nas mensagens
- [ ] Sem erro nem warning no console do navegador
- [ ] `ng build` sem warning; lint configurado (`ng add @angular-eslint/schematics`)
- [ ] Testes dos services e dos componentes principais
- [ ] Favicon e `<title>` próprios

## Roteiro

Cada passo termina no critério; o tutor só avança quando ele é atendido.

1. **Scaffold + proxy**: `ng new web` na raiz do repo, configurar o proxy, `ng serve`. Pronto quando a app sobe em `localhost:4200` e ele entende a estrutura gerada (`main.ts`, `app.config.ts`, `app.routes.ts`, `app.ts`). Conceitos: componente, template, standalone, `angular.json`. Atenção: `ng new` cria um `.git` próprio se não detectar o repo pai; use `--skip-git` para não aninhar repositórios.
2. **Primeiro GET**: um `CategoriaService` (`@Injectable`, `inject(HttpClient)`) chamando `GET /api/categorias`, lista crua na tela. Pronto quando aparece no navegador e a aba Network mostra 200 em `/api/categorias`. Conceitos: injeção de dependência, `Observable`, `subscribe`, `async` pipe ou `toSignal`, `provideHttpClient`.
3. **Tipos**: `interface Categoria`, `Lancamento` e `Page<T>` (pasta `models/`) espelhando a API. Pronto quando não há `any` nas respostas. Conceitos: tipagem de `HttpClient.get<T>()`, generics.
4. **Listagem por mês**: `<input type="month">`, tabela com nome da categoria (não o id), valor em BRL, data. Pronto quando trocar o mês recarrega a tabela. Conceitos: `@for` com `track`, `@if`, pipes `currency` e `date`, signals (`signal`, `computed`, `effect`), locale `pt-BR`.
5. **Rotas e layout**: shell com header e navegação, páginas separadas, **página 404** (`**`), título por rota, mês na query string. Pronto quando F5 em qualquer página mantém o estado e uma URL inventada cai na 404. Conceitos: `app.routes.ts`, `RouterOutlet`, `routerLink`/`routerLinkActive`, lazy loading (`loadComponent`), `ActivatedRoute`, query params, `withComponentInputBinding`.
6. **Criar lançamento**: Reactive Form com `<select>` de categoria e validação (`required`, `min`). Pronto quando o item novo aparece na tabela e um erro da API (400/404/500) aparece na tela com status e texto. Conceitos: `FormGroup`, `FormControl`, `Validators`, estado do form (touched/dirty), `POST` e tratamento de erro.
7. **Estados e erros centralizados**: **interceptor funcional** (`HttpInterceptorFn`) que normaliza o `HttpErrorResponse` (texto, JSON do Spring, `status 0` de rede), serviço de notificações (toast) e componentes de carregando/vazio/erro reaproveitáveis. Pronto quando derrubar a API mostra erro amigável em toda tela e nada fica em branco. Conceitos: `withInterceptors`, `catchError`, `throwError`, `finalize`.
8. **Editar, excluir, paginação e filtro por categoria.** Recurso inexistente na edição mostra "não encontrado". Conceitos: reaproveitar o form em modo edição, parâmetros de rota, confirmação de exclusão, `HttpParams`.
9. **CRUD de categorias.** Trate o 409 ao apagar categoria com lançamentos com mensagem clara.
10. **Relatório mensal**, depois que o Passo 6 da API existir. Vira a tela inicial (`/`).
11. **Acabamento**: passar o **Checklist de produto** inteiro (responsivo, acessibilidade, lint, favicon, guard de alterações não salvas).
12. **Testes**: services com `HttpTestingController`, componentes principais, interceptor.
13. **Deploy**: `ng build`, servir o build pelo Caddy da VPS com proxy `/api` e **fallback para `index.html`** (senão F5 em `/lancamentos` dá 404 do servidor, não da app).

## Mapa Spring → Angular

| Spring (o que ele já conhece) | Angular |
|---|---|
| `@Service` singleton, injeção por construtor | `@Injectable({ providedIn: 'root' })`, `inject()` |
| `@RestController` + `@GetMapping` | rota em `app.routes.ts` + componente |
| DTO (`record`) | `interface` TypeScript (só existe em compilação, não valida em runtime) |
| `RestClient` / `WebClient` | `HttpClient` |
| `Mono` / `Flux` (Reactor) | `Observable` (RxJS): frio, só executa ao `subscribe` (ou `async` pipe) |
| Filter / `HandlerInterceptor` | `HttpInterceptorFn` |
| `@RestControllerAdvice` | interceptor + tratamento de erro no service |
| `@Valid` + Bean Validation | `Validators` dos Reactive Forms (a API continua validando: nunca confie só no front) |
| `application.yaml` | `environment` / `app.config.ts` |
| `@Transactional`, entidade *managed* | não tem equivalente: o estado do front é seu, sincronize com a API |

## Pegadinhas

- **Observable é frio**: chamar `http.get(...)` sem `subscribe` (ou `async` pipe / `toSignal`) **não faz a requisição**. Erro clássico do dev backend.
- **`subscribe` manual vaza**: dependendo de onde estiver, precisa cancelar. Prefira `async` pipe, `toSignal` ou `takeUntilDestroyed`.
- **Muda a resposta, o TypeScript não avisa**: `get<Categoria[]>` é uma promessa sua, não uma validação. Se a API mudar o formato, quebra em runtime.
- `HttpClient` **rejeita** em 4xx/5xx (cai no `error` do `subscribe` ou no `catchError`), diferente do `fetch`. O POST devolve 201: não compare com 200.
- **DELETE devolve 200 com corpo**, não 204. Não espere resposta vazia.
- `new Date("2026-09-18")` interpreta como UTC e mostra o dia anterior no fuso do Brasil. Exiba a string, quebre com `split('-')` ou confira o pipe `date` (que sofre do mesmo problema com string ISO sem hora).
- Pipe `currency` e `date` em pt-BR exigem registrar o locale (`registerLocaleData(localePt)` + `LOCALE_ID`). Sem isso, sai em `en-US`.
- Moeda: `valor` chega como `number`. Totais devem vir da API; somar decimais em `number` no front gera `0.1 + 0.2`.
- `@for` exige `track` (use `track item.id`). Sem ele o build falha.
- Editar `proxy.conf.json` ou `angular.json` exige reiniciar o `ng serve`.
- Tailwind: classes montadas dinamicamente (`'text-' + cor`) **não são geradas** no build, porque o Tailwind só reconhece nomes completos no código. Use classes inteiras (`'text-red-600'`) e escolha entre elas com `@if` ou `[class]`.
- Detecção de mudança: com signals, atualize com `.set()`/`.update()`. Mutar objeto dentro do signal (`s().x = 1`) não dispara re-render.
- Suporte a `<input type="month">` varia por navegador; confira no que ele usa.

## Referências

- Documentação oficial: https://angular.dev (guias, tutorial "Learn Angular", API reference, style guide)
- RxJS: https://rxjs.dev (leia só o necessário: `Observable`, `pipe`, `map`, `catchError`, `switchMap`)
- API: `docs/GUIDE.md` (regras de negócio) e `docs/ROADMAP.md` (progresso e decisões)
