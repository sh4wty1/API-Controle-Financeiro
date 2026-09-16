# API de Controle Financeiro

Projeto de estudo pra aprender **Java + Spring Boot** na prática.

A ideia não é construir a API de finanças mais completa do mundo. É um CRUD de categorias e lançamentos com um relatório mensal, simples de propósito, pra servir de desculpa pra relembrar a linguagem, o framework e o ecossistema (JPA, Flyway, validação, testes...).

Tudo é escrito na mão. IA só entra pra explicar conceitos e revisar código, nunca pra escrever a solução.

## Stack

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL 16 (via Docker)
- Flyway (migrations)
- Maven (via wrapper, não precisa instalar)
- Front simples em HTML + JS, servido pelo próprio Spring

## Funcionalidades (v1)

- **Categorias**: CRUD com nome e tipo (`RECEITA` ou `DESPESA`)
- **Lançamentos**: CRUD com descrição, valor, data e categoria, com filtro por mês/categoria e paginação
- **Relatório mensal**: total de receitas, despesas, saldo e total por categoria

## Como rodar

Pré-requisitos: JDK 21 e Docker.

```bash
# sobe o banco
docker compose up -d

# sobe a aplicação (Windows: .\mvnw.cmd spring-boot:run)
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Documentação

O roteiro de estudo, as regras de negócio e as convenções que sigo estão em [`docs/GUIDE.md`](docs/GUIDE.md).
