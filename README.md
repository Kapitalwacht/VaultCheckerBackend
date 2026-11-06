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
| Database         | PostgreSQL (in-memory H2 through the `h2` profile)            |
| Auth             | JWT access tokens (JJWT), rotating refresh tokens, BCrypt, Google sign-in |
| Email            | EmailJS REST API (verification link, password recovery code)  |
| Payments         | Stripe test-mode Payment Links                                |
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
`ApplicationError` to the correct code (`VALIDATION_ERROR` → 400, `FORBIDDEN` → 403, `*_NOT_FOUND` → 404,
`*_CONFLICT` → 409, `BUSINESS_RULE_VIOLATION` → 422, `SERVICE_UNAVAILABLE` → 503). Messages and error
details are localised (en/es) from the request's `Accept-Language` header.

### Bounded contexts

| Context         | Responsibility                                                     | Base path(s) |
|-----------------|-------------------------------------------------------------------|--------------|
| `iam`           | Sign-up/sign-in, sessions, email verification, password recovery, users and roles | `/authentication`, `/users`, `/roles` |
| `stores`        | Store registration / logical deactivation, tenant isolation       | `/stores` |
| `catalog`       | Products & services (cash/list price, payment mode, brand, image) | `/products` |
| `customers`     | Customers and their pactada credit terms (rate, currency, dates)  | `/customers` |
| `credit`        | Credit accounts, statements, purchases, payments + the **financial engine** | `/credit-accounts`, `/purchases`, `/payments`, `/installment-payments` |
| `subscriptions` | Store plans, subscriptions and invoices                           | `/plans`, `/subscriptions`, `/invoices` |
| `audit`         | Operation traceability, event-driven                              | `/audit-logs` |
| `shared`        | Base aggregate, `Result`/`ApplicationError`, global error handling, JPA/OpenAPI/i18n config | — |

All paths are under `/api/v1`. Interactive docs: **`/swagger-ui.html`**.

### Roles & multi-tenancy

Three roles model the three personas in the brief:

- `ROLE_SYSTEM_ADMIN` — platform admin; registers and deactivates stores. Only one can exist.
- `ROLE_STORE_ADMIN` — business owner; manages products, customers, credit and the store's plan.
- `ROLE_CUSTOMER` — neighbour; consults their own debt and payment plan.

The API accepts and returns the short names `system-admin`, `store-admin` and `customer`. Roles are
created on demand the first time they are assigned (no startup seeding).

Every product, customer and credit account carries a `storeId`. `CurrentUserScope` reads the store from
the token, so lists only return the caller's data and another store's records answer `403` or `404`.
A customer signs up with the **DNI** registered by the store, which links the user to their debts.

### Sessions

`sign-in` returns a **15-minute access token** and a **7-day refresh token**. The refresh token is
stored as a SHA-256 hash and rotates on every `/authentication/refresh`; `/authentication/sign-out`
revokes it. Sign-in requires a verified email unless `EMAIL_VERIFICATION_REQUIRED=false`.

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
- **`CreditCycle`** — derives the cutoff, grace end and payment dates of each billing cycle.

Each purchase freezes the terms agreed when it was made (`CreditTerms`), so later changes to the
customer's rate do not alter existing credits. Each credit keeps the customer's currency (PEN or USD).
Amounts owed are always computed on the server.

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

Variables for account emails (verification link and password recovery code), sent through the
EmailJS REST API. While they are empty, sign-up still works but no email is sent, and password recovery
and email changes answer `503 SERVICE_UNAVAILABLE`. Set `FRONTEND_BASE_URL` in production; without it,
verification links point to `http://localhost:5173`:

```properties
FRONTEND_BASE_URL=https://vaultchecker-frontend.web.app
EMAILJS_SERVICE_ID=
EMAILJS_PUBLIC_KEY=
EMAILJS_PRIVATE_KEY=
EMAILJS_VERIFICATION_TEMPLATE_ID=
EMAILJS_RECOVERY_TEMPLATE_ID=
```

EmailJS only accepts server-side calls when **Account → Security → "Allow EmailJS API for
non-browser applications"** is enabled. The verification template receives `{{to_email}}`,
`{{name}}` and `{{link}}`; the recovery template receives `{{to_email}}`, `{{name}}` and `{{code}}`.
Both templates must use `{{to_email}}` in their **To Email** field.

Other optional variables (the defaults are in `application.properties`):

| Variable                      | Default          | Purpose                                 |
|-------------------------------|------------------|-----------------------------------------|
| `PORT`                        | `8092`           | Port the API listens on                 |
| `JWT_EXPIRATION_MINUTES`      | `15`             | Access token lifetime                   |
| `REFRESH_EXPIRATION_DAYS`     | `7`              | Refresh token lifetime                  |
| `EMAIL_VERIFICATION_REQUIRED` | `true`           | Whether sign-in needs a verified email  |
| `APP_TIME_ZONE`               | `America/Lima`   | Business day for due and overdue dates  |
| `GOOGLE_CLIENT_ID`            | Project client   | Audience accepted for Google sign-in    |
| `STRIPE_PAYMENT_LINK_NEGOCIO` | Test link        | Payment Link of the Negocio plan        |
| `STRIPE_PAYMENT_LINK_CADENA`  | Test link        | Payment Link of the Cadena plan         |

Each Stripe Payment Link must redirect to `<frontend>/billing?checkout=success` after payment. That
redirect is what activates the plan.

---

## Running locally

Prerequisites: **JDK 26** and a running **PostgreSQL** with a `vaultchecker` database
(`docker compose up -d` starts one). Set your values in `.env.development`, then:

```bash
./mvnw spring-boot:run
```

To run without PostgreSQL, use the in-memory database. Its data is lost on restart:

```bash
SPRING_PROFILES_ACTIVE=h2 EMAIL_VERIFICATION_REQUIRED=false ./mvnw spring-boot:run
```

- API: `http://localhost:8092`
- Swagger UI: `http://localhost:8092/swagger-ui.html`

On first start Hibernate creates the tables (`spring.jpa.hibernate.ddl-auto=update`).

### Quick smoke test

With `EMAIL_VERIFICATION_REQUIRED=false`, as in the `h2` command above:

```bash
# Register a store admin with their store, then sign in to get a JWT
curl -X POST http://localhost:8092/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@bodega.pe","password":"12345678","name":"Owner","role":"store-admin","storeId":"ST-001","businessName":"Bodega Don Pepe"}'

curl -X POST http://localhost:8092/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{"email":"owner@bodega.pe","password":"12345678"}'
# → { "id":1, "email":"...", "role":"store-admin", "storeId":"ST-001", "token":"eyJ...", "refreshToken":"..." }
```

Use the returned `token` as `Authorization: Bearer <token>` for the protected endpoints.

## Docker

```bash
docker build -t vaultchecker-backend .
docker run -p 8092:8092 --env-file .env.production vaultchecker-backend
```

## Deployment

Production runs on **Render** from this `Dockerfile` with the `prod` profile. Every push to `main`
deploys automatically. Set `DATABASE_URL`, `JWT_SECRET`, `FRONTEND_BASE_URL` and the `EMAILJS_*`
variables in the service's **Environment** tab.
