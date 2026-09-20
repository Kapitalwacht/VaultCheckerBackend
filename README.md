# VaultChecker Platform — Backend

RESTful API for **VaultChecker**, a multi-store platform that digitalises the neighbourhood
_"cuaderno de fiado"_ (store credit ledger). Small businesses — bodegas, butchers, bakeries,
hairdressers — extend credit to their neighbours; VaultChecker replaces the paper notebook with a
proper running-account system, including the **financial engine** that computes interest, instalments
and late fees.

Built with **Java 26**, **Spring Boot 4** and **PostgreSQL**, following **Domain-Driven Design (DDD)**
with a **hexagonal / ports-and-adapters** layout and a lightweight **CQRS** style (commands vs queries).

---

## Tech stack

| Concern          | Choice                                                        |
|------------------|--------------------------------------------------------------|
| Language / runtime | Java 26                                                    |
| Framework        | Spring Boot 4 (Web MVC, Data JPA, Security, Validation)       |
| Database         | PostgreSQL (H2 in tests)                                      |
| Auth             | JWT bearer tokens (JJWT) + BCrypt password hashing           |
| Docs             | SpringDoc OpenAPI / Swagger UI                                |
| Build            | Maven (wrapper included) — `./mvnw`                          |

---

## Architecture

The code is split into **bounded contexts** (one folder per business capability). Every context is
organised into the same four layers, and dependencies only ever point **inwards** (interfaces →
application → domain; infrastructure implements domain ports):

```
<context>/
├── domain/           # Pure business model — no framework code
│   ├── model/
│   │   ├── aggregates/     # Aggregate roots (Store, Customer, CreditAccount, …)
│   │   ├── entities/       # Entities owned by an aggregate (Role)
│   │   ├── valueobjects/   # Immutable values (Roles, RateType, PaymentAllocation)
│   │   ├── commands/       # Write intents (CreateStoreCommand, RegisterPaymentCommand)
│   │   ├── queries/        # Read intents (GetAllStoresQuery, GetStoreByIdQuery)
│   │   └── events/         # Domain events (UserSignedUpEvent)
│   ├── repositories/       # Repository PORTS (interfaces the domain depends on)
│   └── services/           # Pure domain services (the financial calculators)
├── application/      # Use-case orchestration
│   ├── commandservices/    # Command service contracts
│   ├── queryservices/      # Query service contracts
│   └── internal/           # Their @Service implementations + event handlers + outbound ports
├── infrastructure/   # Framework & I/O adapters
│   ├── persistence/jpa/    # JPA entities, Spring Data repositories, assemblers, port adapters
│   ├── authorization/      # Spring Security config, JWT filter, UserDetails
│   ├── tokens/ hashing/     # JWT + BCrypt implementations of the application ports
│   └── …
└── interfaces/       # Delivery layer
    └── rest/               # Controllers, resources (DTOs), transform assemblers
```

### Why the domain never touches JPA

The domain model (e.g. `Store`) is a plain Java object. Persistence uses a **separate**
`StorePersistenceEntity` annotated with JPA. A **persistence assembler** converts between the two, and
a **repository adapter** implements the domain's `StoreRepository` port using Spring Data. This keeps
business rules independent of the database and makes the domain trivial to unit-test.

### How a request flows (write path)

```
POST /api/v1/stores
  → StoresController                      (interfaces/rest)
  → CreateStoreCommandFromResourceAssembler   maps the JSON DTO → CreateStoreCommand
  → StoreCommandService.handle(command)   (application) validates + orchestrates
  → Store aggregate                       (domain) applies business rules
  → StoreRepository.save(...)             domain PORT
  → StoreRepositoryImpl                   (infrastructure) adapter → Spring Data JPA → PostgreSQL
  ← Result<Store, ApplicationError>       success value OR typed error
  ← ResponseEntityAssembler               turns the Result into a 2xx body or the right error status
```

Every use case returns a **`Result<T, ApplicationError>`** (a sealed type in `shared`) instead of
throwing for expected failures. `ResponseEntityAssembler` maps success to the given HTTP status and
`ApplicationError` to the correct code (`VALIDATION_ERROR` → 400, `*_NOT_FOUND` → 404,
`BUSINESS_RULE_VIOLATION` → 422, `*_CONFLICT` → 409). Messages are localised (en/es) via i18n bundles.

### Bounded contexts

| Context         | Responsibility                                                     | Base path(s) |
|-----------------|-------------------------------------------------------------------|--------------|
| `iam`           | Authentication, users, roles, JWT/BCrypt security                 | `/authentication`, `/users`, `/roles` |
| `stores`        | Store registration / logical deactivation, tenant isolation       | `/stores` |
| `catalog`       | Products & services (cash/list price, payment mode, brand, image) | `/products` |
| `customers`     | Customers and their pactada credit terms (rate, currency, dates)  | `/customers` |
| `credit`        | Credit accounts, purchases, payments + the **financial engine**   | `/credit-accounts`, `/purchases`, `/payments` |
| `subscriptions` | Store plans, subscriptions and invoices                           | `/plans`, `/subscriptions`, `/invoices` |
| `audit`         | Operation traceability, event-driven                              | `/audit-logs` |
| `shared`        | Base aggregate, `Result`/`ApplicationError`, global error handling, JPA/OpenAPI/i18n config | — |

All paths are under `/api/v1`. Interactive docs: **`/swagger-ui.html`**.

### Roles & multi-tenancy

Three roles model the three personas in the brief:

- `ROLE_SYSTEM_ADMIN` — platform admin; registers and deactivates stores.
- `ROLE_STORE_ADMIN` — business owner; manages products, customers and credit.
- `ROLE_CUSTOMER` — neighbour; consults their own debt and payment plan.

Roles are created on demand the first time they are assigned (no startup seeding). Every product,
customer and credit account carries a `storeId`, keeping each store's data isolated.

---

## Financial calculation engine (`credit/domain/services`)

The academic core of the project. All calculators are **pure, dependency-free** classes that honour
the mandatory conventions in `FinanceConstants` — **360-day year, 30-day commercial month, amounts
rounded to 2 decimals, rates carried to ≥7 decimals**:

- **`InterestCalculator`** — converts a pactada rate (nominal ⇄ effective), computes **compensatory
  interest** for the days between purchase and payment, **moratory (late) interest** for the days of
  delay, and **capitalises the grace period** before the first instalment.
- **`FrenchAmortizationCalculator`** — builds the equal-instalment **French method (vencido)** schedule
  `A = P · r / (1 − (1 + r)⁻ⁿ)`, using the effective monthly rate and capitalising grace days first.
- **`PaymentAllocationCalculator`** — applies the payment **prelación**: a payment is imputed to
  moratory interest first, then compensatory interest, then principal.

Being framework-free, each calculator can be unit-tested directly against the assignment's test cases.

---

## Configuration — two environments

Configuration is externalised into **two env files** (same scheme as the frontend). Each is parsed as
a properties file and auto-loaded by the matching Spring profile via `spring.config.import`:

| File               | Profile          |
|--------------------|------------------|
| `.env.development` | `dev` (default)  |
| `.env.production`  | `prod`           |

Each file holds just two variables — real environment variables override them, so nothing
environment-specific is hardcoded in the source:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/vaultchecker?user=postgres&password=change-me
JWT_SECRET=your-strong-random-secret
```

---

## Running locally

Prerequisites: **JDK 26** and a running **PostgreSQL** with a `vaultchecker` database. Set your values
in `.env.development`, then:

```bash
./mvnw spring-boot:run
```

- API: `http://localhost:8092`
- Swagger UI: `http://localhost:8092/swagger-ui.html`

On first start Hibernate creates the tables (`spring.jpa.hibernate.ddl-auto=update`).

### Quick smoke test

```bash
# Register a store admin, then sign in to get a JWT
curl -X POST http://localhost:8092/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@bodega.pe","password":"12345678","role":"ROLE_STORE_ADMIN"}'

curl -X POST http://localhost:8092/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@bodega.pe","password":"12345678"}'
# → { "id":1, "email":"...", "role":"ROLE_STORE_ADMIN", "token":"eyJ..." }
```

Use the returned `token` as `Authorization: Bearer <token>` for the protected endpoints.

## Docker

```bash
docker build -t vaultchecker-backend .
docker run -p 8092:8092 --env-file .env.production vaultchecker-backend
```
