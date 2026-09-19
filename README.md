# VaultChecker Platform — Backend

RESTful API for **VaultChecker**, a multi-store platform that digitalises the neighbourhood
"cuaderno de fiado" (store credit ledger). Built with **Domain-Driven Design (DDD)** on **Java 26**,
**Spring Boot 4** and **PostgreSQL**, following the same bounded-context / hexagonal structure as the
[reference services repo](https://github.com/Kauflink/daop-entreprenly-web-services) and serving the
[VaultChecker frontend](https://github.com/Kapitalwacht/VaultCheckerFrontend).

## Architecture

Each bounded context is organised into four layers:

```
domain/          model (aggregates, entities, value objects, commands, queries, events), repositories, services
application/     command/query service contracts + internal implementations, event handlers, outbound ports
infrastructure/  JPA persistence (entities, repositories, assemblers, adapters), security, tokens, hashing
interfaces/      REST controllers, resources (DTOs) and transform assemblers
```

### Bounded contexts (aligned with the frontend)

| Context     | Responsibility                                                        | Base path |
|-------------|----------------------------------------------------------------------|-----------|
| `iam`       | Authentication, users, roles, JWT/BCrypt security                    | `/api/v1/authentication`, `/api/v1/users`, `/api/v1/roles` |
| `stores`    | Platform store registration / logical deactivation, tenant isolation | `/api/v1/stores` |
| `catalog`   | Store products/services                                              | `/api/v1/products` |
| `customers` | Store customers and their credit limit                              | `/api/v1/customers` |
| `credit`    | Credit accounts, payments and the **financial calculation engine**  | `/api/v1/credit-accounts`, `/api/v1/payments` |
| `audit`     | Operation traceability (event-driven)                               | `/api/v1/audit-logs` |
| `shared`    | Base aggregate, Result/ApplicationError, error handling, JPA/OpenAPI/i18n config | — |

### Roles

`ROLE_SYSTEM_ADMIN` (platform admin — manages stores), `ROLE_STORE_ADMIN` (business owner — manages
products, customers, credit) and `ROLE_CUSTOMER` (neighbour — checks own debt). Roles are created
on demand the first time they are assigned (no startup seeding).

## Financial calculation engine (`credit/domain/services`)

The course core. All calculations honour the mandatory conventions in `FinanceConstants`
(360-day base, 30-day commercial month, amounts to 2 decimals, rates to ≥7 decimals):

- `InterestCalculator` — nominal↔effective conversion, compensatory interest by days,
  moratory interest and grace-period capitalisation.
- `FrenchAmortizationCalculator` — equal-installment French method (vencido) schedule with grace
  capitalisation.
- `PaymentAllocationCalculator` — payment prelación: moratory → compensatory → principal.

## Configuration — two environments

Configuration is externalised into **two env files** (same scheme as the frontend), each parsed as a
properties file and auto-loaded by the matching Spring profile:

- `.env.development` → `dev` profile (default)
- `.env.production` → `prod` profile

Each file holds just two variables:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/vaultchecker?user=postgres&password=password
JWT_SECRET=change-me
```

Real environment variables (e.g. from the hosting provider or `docker run --env-file`) override the
file values, so nothing environment-specific is hardcoded in the source.

## Running locally

Prerequisites: **JDK 26**, a running **PostgreSQL** with a `vaultchecker` database, and the values in
`.env.development`.

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8092`. Swagger UI: `http://localhost:8092/swagger-ui.html`.

## Docker

```bash
docker build -t vaultchecker-backend .
docker run -p 8092:8092 --env-file .env.production vaultchecker-backend
```
