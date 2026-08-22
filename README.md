# agoravaiapp — Core Service (Quarkus)

Serviço principal do **Agora Vai** (lançamentos, assinaturas, dashboard,
categorias, quick actions). Implementado em **Java 21 + Quarkus 3 + PostgreSQL**.

## Stack

- REST: Quarkus REST (RESTEasy Reactive) + Jackson
- Persistência: Hibernate ORM + Panache + PostgreSQL 16
- Migrações: Flyway (schema `core`)
- Observabilidade: `/q/health`, `/q/metrics`, `/q/openapi`
- Redis: **semi-implementado** (desabilitado por padrão via `agoravai.redis.enabled`)
- Kafka: dependência presente (eventos de domínio — Fase 5, ainda não emitidos)

## Como rodar (dev)

1. Suba o Postgres:

   ```shell
   docker compose up -d
   ```

   Banco `agoravai_core` na porta `5434`.

2. Rode o app em dev mode:

   ```shell
   ./mvnw quarkus:dev
   ```

   O app sobe em `http://localhost:8080` e aplica as migrations automaticamente.

## Identidade do usuário

O gateway valida o JWT (Firebase) e injeta os headers `X-User-Id` e
`X-User-Admin`. Em dev, sem gateway, é usado o usuário fixo `dev-user`
(administrador), configurável em `application.properties`.

> TODO (produção): validar o JWT Firebase no próprio core via `quarkus-oidc`.

## Endpoints (prefixo `/api/v1`)

| Método | Rota | Status |
|---|---|---|
| GET | `/categories` | ✅ |
| GET/POST/DELETE | `/transactions` | ✅ |
| GET/POST/PATCH/DELETE | `/subscriptions` | ✅ |
| GET/POST/DELETE | `/quick-actions` | ✅ |
| GET | `/dashboard` | ✅ |
| GET | `/admin/metrics` | ✅ (admin) |
| POST | `/transactions/statement/upload` | 🚧 501 |
| POST | `/transactions/statement/{id}/confirm` | 🚧 501 |
| POST | `/ai/insights`, `/ai/nlp/parse-transaction` | 🚧 501 |
| GET | `/gamification/*` | 🚧 501 |

Itens marcados com 🚧 são serviços ainda não criados e respondem `501` com
"Em desenvolvimento".

## Empacotar

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```
