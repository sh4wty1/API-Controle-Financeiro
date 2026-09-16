# Roadmap

Onde eu estou no projeto. Atualizar ao fim de cada sessão de estudo.
Conceitos e regras ficam no [GUIDE.md](GUIDE.md), aqui é só progresso e decisões.

> **Retomando com IA:** "Leia `docs/GUIDE.md` e `docs/ROADMAP.md` e me ajude a continuar de onde parei."

**Última atualização:** 15/09/2026, passos 1 e 2 concluídos, começando passo 3

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

### Passo 3: Categoria ponta a ponta ⏳
### Passo 4: Tratamento de erros
### Passo 5: Lançamentos + regras
### Passo 6: Relatório
### Passo 7: Testes

### Passo 8: Front básico
- HTML + JS puro (`fetch`) em `src/main/resources/static/`, servido pelo próprio Spring em `localhost:8080`
- Mesma origem da API, então não tem CORS
- Pode começar antes, assim que o CRUD de categoria existir, pra visualizar a API

---

## Decisões tomadas

| Decisão | Motivo |
|---|---|
| IntelliJ com keymap padrão | Aprender os atalhos nativos |
| Autocomplete de IA desligado (Full Line / AI Assistant) | Regra do GUIDE: IA não escreve código. Code completion normal (`Ctrl+Space`) fica ligado |
| Banco via Docker **no PC de casa** | PC do trabalho: licença do Docker Desktop + política de TI |
| Front estático em `static/` | Sem Node/build, sem CORS. Isso muda o "Fora da v1: front" do GUIDE |
| Manter blocos vazios do `pom.xml` (`<licenses/>` etc.) | Evitam herdar licença/devs do parent (ver HELP.md) |

Alternativas pro banco se não der Docker: VPS com Postgres acessado por túnel SSH (`ssh -N -L 5432:localhost:5432 user@vps`, porta presa em `127.0.0.1` na VPS), Neon/Supabase, ou H2.

---

## Setup numa máquina nova

1. Instalar JDK 21, IntelliJ e Docker Desktop (WSL2)
2. `git clone` e abrir no IntelliJ: **File → Open → `pom.xml` → Open as Project**
3. `Ctrl+Alt+Shift+S` → Project → SDK **21**, Language level **SDK default** → Apply
4. Esperar o Maven sincronizar (se não tiver nada pra baixar, some rápido sem barra de progresso)

---

## Pegadinhas que já aprendi

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
