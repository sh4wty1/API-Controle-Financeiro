# Controle Financeiro

Projeto de estudo fullstack: API em **Java + Spring Boot** e front em **Angular**, no mesmo repositório.

A ideia não é construir a API de finanças mais completa do mundo. É um CRUD de categorias e lançamentos com um relatório mensal, simples de propósito, pra servir de desculpa pra relembrar a linguagem, o framework e o ecossistema (JPA, Flyway, validação, testes...).

Tudo é escrito na mão. IA só entra pra explicar conceitos e revisar código, nunca pra escrever a solução.

## Stack

**API**
- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL 16 (via Docker)
- Flyway (migrations)
- Maven (via wrapper, não precisa instalar)

**Front**
- Front: Angular + TypeScript (em `web/`)

## Estrutura

```
api/                 API Spring Boot (pom.xml, mvnw, src/)
web/                 front Angular
docs/                guia, roadmap, briefing do front
docker-compose.yml   Postgres de dev
```

## Funcionalidades (v1)

- **Categorias**: CRUD com nome e tipo (`RECEITA` ou `DESPESA`)
- **Lançamentos**: CRUD com descrição, valor, data e categoria, com filtro por mês/categoria e paginação
- **Relatório mensal**: total de receitas, despesas, saldo e total por categoria

## Como rodar

Pré-requisitos: JDK 21, Docker e Node (pro front).

```bash
# sobe o banco
docker compose up -d

# sobe a aplicação (Windows: .\mvnw.cmd spring-boot:run)
cd api
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

```bash
# sobe o front (em outro terminal)
cd web
npm install
npx ng serve
```

O front sobe em `http://localhost:4200` e chega na API pelo proxy `/api`.

## Documentação

O roteiro de estudo, as regras de negócio e as convenções que sigo estão em [`docs/GUIDE.md`](docs/GUIDE.md). Progresso em [`docs/ROADMAP.md`](docs/ROADMAP.md). O briefing das sessões do front está em [`docs/frontend-briefing-angular.md`](docs/frontend-briefing-angular.md).
