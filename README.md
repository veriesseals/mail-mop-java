# Mail Mop

Email unsubscribe and mailbox cleanup application by **Base 256 Software LLC**.

Mail Mop keeps a person's inbox junk-free: it scans a connected mailbox, groups
mail by sender and sending domain, and lets the user safely bulk-unsubscribe —
confirming **each sender individually** so wanted mail that landed in spam is
never unsubscribed by mistake — then optionally blocks the sender's domain and
moves the offending mail to Trash. Nothing is ever permanently deleted by the
app.

Status: **pre-release, in active development.** Not yet published.

---

## Technology Stack

**Backend**
- Java 17
- Spring Boot 3.5.16
- Spring Security + JWT (dual-mode: httpOnly cookie for web, `Authorization: Bearer` for mobile)
- Spring Data JPA / Hibernate 6
- PostgreSQL 16 (dev and prod) / H2 (fast tests) / Testcontainers-Postgres (integration tests)
- Flyway (versioned migrations)
- AWS SDK v2 — KMS (wraps the OAuth-token encryption key), SES (transactional email)
- Maven (wrapper committed — no system Maven required)
- Lombok, springdoc / Swagger UI

**Frontend** (later step)
- React + Vite, React Router v7, Axios — `mail-mop-react/`

**Mobile** (planned, after web)
- React Native — Android + iOS, App Store / Play Store targeted. The web
  frontend's `api/` and state layers are kept framework-agnostic so they port.

**Mail providers**
- Gmail (Gmail API) · Microsoft (Graph API) · Apple iCloud (IMAP)
- One `MailProvider` interface with capability flags. Apple/IMAP has no
  unsubscribe or server-side block API — scan + Trash + client-side filter only.

---

## Project Structure

```
mail-mop-java/
├── src/
│   ├── main/
│   │   ├── java/com/base256/mailmop/
│   │   │   ├── apps/          # feature modules (accounts, mailboxes, scans, senders, actions, rules, audit)
│   │   │   ├── shared/        # config, security, email, exception handling
│   │   │   └── MailMopApplication.java
│   │   └── resources/
│   │       ├── application.yml            # base (activates dev profile, loads .env)
│   │       ├── application-dev.yml        # local Postgres
│   │       ├── application-prod.yml       # Postgres over SSL, env-only secrets
│   │       └── db/migration/              # Flyway V1__…, V2__…
│   └── test/
├── mail-mop-react/            # React + Vite frontend (later step)
├── .env.example              # copy to .env for local dev (.env is gitignored)
├── mvnw / mvnw.cmd           # Maven wrapper
└── pom.xml
```

Each feature module under `apps/` follows the same shape: `<Entity>.java`,
`<Feature>Controller.java` (`/api/<feature>`, `@PreAuthorize`),
`<Feature>Service.java` (`@Transactional`, audit calls), `<Feature>Repository.java`,
`dto/<Feature>Request.java` (validation), `dto/<Feature>Response.java`.

---

## Roadmap

Built in small, individually verifiable steps.

**Where we left off:** Step 3 complete (`d80387f`). The backend boots, connects
to local Postgres, applies Flyway V1, and serves `/api/health`,
`/api/system/status`, `/v3/api-docs`, and Swagger UI. **Next: Step 4** — the
`accounts` module (ported from CivicID, dual-mode cookie + bearer auth),
planned as 4a (core: users, roles, JWT, login) then 4b (MFA + refresh-token
rotation).

- [x] **1. Build foundation** — `pom.xml`: Boot 3.5.16 / Java 17 stack, Postgres, Flyway, Security, JWT, springdoc, AWS KMS/SES, Testcontainers. No GPL dependencies in the shipped artifact.
- [x] **2. Application skeleton** — `MailMopApplication`, `application.yml` / `-dev` / `-prod` profiles, `.env` loading, Maven wrapper, `.gitignore`. Boots and connects to local Postgres.
- [x] **3. Shared layer + first migration** — `SecurityConfig`, `HealthController`, `SystemStatusController`, `GlobalExceptionHandler` / `ErrorResponse`, `OpenApiConfig`, and `V1__baseline.sql`. `/api/health`, `/api/system/status`, `/v3/api-docs`, and Swagger UI all return 200; every other route is 401/403.
- [ ] **4. Accounts module** — `User` / `Role`, JWT filter + util (dual-mode), `AuthController` / `UserController`, `CustomUserDetailsService`, bootstrap admin, `EmailService` (SES), refresh-token rotation, MFA. Ported from CivicID.
- [ ] **5. Mailboxes module** — `ConnectedMailbox` entity, encrypted OAuth token storage (AES-256-GCM + KMS-wrapped key), `MailProvider` interface + capability flags, connect / disconnect endpoints.
- [ ] **6. Gmail adapter** — Google API deps, OAuth consent flow, token store, `scan()` (sender/domain aggregation, `List-Unsubscribe` / RFC 8058 one-click detection).
- [ ] **7. Scans module** — async scan job (`@Async` + status row), start-scan and poll-results endpoints.
- [ ] **8. Senders module** — grouped sender/domain aggregates: address, display name, message count, unsubscribe method, first/last seen, keep/block state.
- [ ] **9. Actions module** — per-sender action queue (`UNSUBSCRIBE` / `BLOCK_DOMAIN` / `TRASH`), explicit per-sender confirmation, execution via the provider adapter, every outcome audited. The safety core.
- [ ] **10. Rules module** — keep-list / block-list, enforced everywhere actions run.
- [ ] **11. Microsoft Graph adapter** — against the same `MailProvider` interface.
- [ ] **12. Apple IMAP adapter** — scan + Trash + client-side filter (no unsubscribe/block API).
- [ ] **13. Web frontend** — scaffold `mail-mop-react/`, then: connect mailbox → run scan → review senders table with checkboxes → confirm-per-sender → results.
- [ ] **14. CI / Docker / compose** — GitHub Actions, multi-stage `Dockerfile`, `docker-compose.yml`, `nginx.conf`.

**Before public launch:** Google OAuth verification + CASA security assessment
(for the `gmail.modify` restricted scope), Microsoft Graph app registration,
published privacy policy, `THIRD-PARTY-NOTICES` file, proprietary `LICENSE`,
USPTO knockout search on the "Mail Mop" name.

---

## Getting Started

### Prerequisites

- Java 17 (`brew install --cask microsoft-openjdk@17`)
- PostgreSQL 16 (`brew install postgresql@16 && brew services start postgresql@16`)
- Node.js 20+ / npm (for the frontend, later)

Maven is **not** required — the repo ships `./mvnw`.

### 1. Create the database

```sql
-- psql postgres
CREATE ROLE mailmop_user WITH LOGIN;
\password mailmop_user
CREATE DATABASE mailmop OWNER mailmop_user;
\c mailmop
GRANT ALL ON SCHEMA public TO mailmop_user;
```

### 2. Configure `.env`

```bash
cp .env.example .env
```

Set `DB_USERNAME` / `DB_PASSWORD` to the role you just created. `.env` is
gitignored and is loaded automatically at startup.

### 3. Run

```bash
./mvnw spring-boot:run
```

Backend starts on `http://localhost:8080`. Swagger UI at
`http://localhost:8080/swagger-ui.html` (once Step 3 lands).

Run with the prod profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Tests

```bash
./mvnw test
```

---

## License

Proprietary. Copyright © Base 256 Software LLC. All rights reserved.
