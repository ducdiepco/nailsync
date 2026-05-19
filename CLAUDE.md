# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.example.nailsync.ExampleUnitTest"

# Run instrumented (on-device) tests
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Clean build
./gradlew clean assembleDebug
```

## UI Design

Purple→magenta diagonal gradient fills every screen. Cards are white and float on the gradient. All text on gradient is white; text on cards is dark purple (`Color(0xFF1A1030)`).

- **Queue screen** — two-pane on screens ≥ 600 dp: staff grid (left, 260 dp fixed) + ticket queue (right). Narrow screens show only the ticket queue.
- **Ticket detail screen** — two-pane on ≥ 600 dp: ticket items + totals/actions (left, 300 dp fixed) + service catalog with category tabs (right). The catalog is embedded directly — no separate AddService navigation needed on wide screens.
- **Category tabs** — pill-style, white-on-gradient (unselected) / white-background-purple-text (selected). Categories are seeded from `DataSeeder` (NAILS, WAXING, FACIAL, MASSAGE).
- Brand colors live in `ui/theme/Color.kt`; reusable `GradientBackground` composable in `core/ui/GradientBackground.kt`.

## Architecture

Feature-based Clean Architecture with MVVM. Single `app/` module.

```
app/src/main/java/com/example/nailsync/
├── core/navigation/       — NavHost + Routes constants
├── data/
│   ├── local/             — Room database, DAOs, entities, mappers
│   ├── repository/        — Repository implementations
│   └── seeder/            — DataSeeder (pre-populates technicians + service catalog on first launch)
├── di/                    — Hilt modules (AppModule: DB/DAOs; RepositoryModule: @Binds interfaces)
├── domain/
│   ├── model/             — Ticket, TicketService, TicketStatus, Technician, Service
│   ├── repository/        — Repository interfaces
│   └── usecase/           — One use case per operation
├── features/
│   ├── queue/             — Queue screen (grouped by status, create-ticket dialog)
│   ├── ticket/            — TicketDetail + AddService screens
│   ├── technician/        — TechnicianAssignment screen
│   └── checkout/          — Checkout screen with tip presets
└── NailSyncApplication.kt — @HiltAndroidApp, runs DataSeeder on startup
```

### Data flow

```
Compose Screen → ViewModel (StateFlow) → UseCase → Repository interface → RepositoryImpl → Room DAO
```

### Key design decisions

- `Ticket.subtotal / tax / total` are computed properties derived from services; `tip` is stored in Room
- `TicketStatus` enum carries display name, color, `nextStatus`, and `actionLabel` properties
- Navigation uses typed Int arguments (`ticketId`, `serviceId`) — `SavedStateHandle` injects them into ViewModels automatically
- `DataSeeder` is idempotent (checks `count() == 0` before inserting), called from Application scope on every start
- Repository module uses `@Binds` (abstract class) to bind interfaces to implementations

### Dependency versions (gradle/libs.versions.toml)

- AGP 9.2.1, Kotlin 2.2.10, Compose BOM 2026.02.01
- Hilt **2.59.2**, Room 2.7.0, Navigation Compose 2.9.0
- KSP **2.2.10-2.0.2** (format is `{kotlin_version}-{ksp_api_version}`, not `{kotlin_version}-1.0.x`)
- `android.disallowKotlinSourceSets=false` is required in `gradle.properties` — AGP 9.x built-in Kotlin mode conflicts with how KSP registers generated sources
- Do NOT add `kotlin.android` plugin explicitly — AGP 9.x registers the `kotlin` extension automatically; adding the plugin again causes a conflict
- `kotlinOptions {}` block is unavailable without `kotlin.android`; `compileOptions` handles the JVM target

### Ticket workflow

```
WAITING → ASSIGNED → IN_PROGRESS → READY_FOR_PAYMENT → PAID (via Checkout)
                                                      → CANCELLED (any stage)
```

## Rails API Backend

A companion Rails 8.1 API-only app lives at `~/RubyProjects/nailsync-api/`. It mirrors this app's full domain over HTTP so the Android app can eventually swap Room for remote calls.

**Start the backend locally:**
```bash
# Start PostgreSQL (once per boot, using Postgres.app)
/Applications/Postgres.app/Contents/Versions/15/bin/pg_ctl \
  -D ~/Library/Application\ Support/Postgres/var-15 \
  -l /tmp/postgres.log start

cd ~/RubyProjects/nailsync-api
/opt/homebrew/Cellar/ruby/3.3.6/bin/bundle exec rails s -p 3000
```

### Keeping both apps in sync

The Android app and Rails API share the same domain model. **Any feature that touches data must be applied to both.** Use this checklist:

| Change in Android | Required change in Rails API |
|---|---|
| New field on a domain model | Migration + update serializer |
| New domain model / entity | New migration, model, serializer, controller, routes |
| New use case / operation | New controller action + route |
| Status enum value added | Update `Ticket::STATUSES` constant |
| Seed data changed (`DataSeeder.kt`) | Update `db/seeds.rb` |
| Business rule change (tax rate, combine logic, etc.) | Mirror in the Rails model or controller |

**When building a new feature, always:**
1. Implement the feature in this Android app (domain model → Room entity → use case → ViewModel → Screen)
2. Apply the equivalent change in `~/RubyProjects/nailsync-api/` (migration → model → serializer → controller → route)
3. Run `rails db:migrate` and verify the endpoint with curl or the Rails console

### Rails API structure

```
~/RubyProjects/nailsync-api/
├── app/
│   ├── controllers/api/v1/   — auth, tickets, ticket_services, customers, catalog_services, technicians
│   ├── models/               — Owner, Customer, Technician, CatalogService, Ticket, TicketService
│   ├── serializers/          — Plain Ruby serializers (no gem); include computed fields
│   └── services/             — JsonWebToken (encode/decode JWT)
├── config/routes.rb          — All routes namespaced under /api/v1/
└── db/
    ├── migrate/              — One migration per table (owners, customers, technicians, catalog_services, tickets, ticket_services)
    └── seeds.rb              — Owner account + 8 technicians + 31 services (mirrors DataSeeder.kt exactly)
```

### Schema mapping (Android → Rails)

| Android (Room) | Rails |
|---|---|
| `TicketEntity` | `tickets` table, `Ticket` model |
| `TicketServiceEntity` | `ticket_services` table, `TicketService` model |
| `CustomerEntity` | `customers` table, `Customer` model |
| `TechnicianEntity` | `technicians` table, `Technician` model |
| `ServiceEntity` | `catalog_services` table, `CatalogService` model |
| `TicketStatus` enum (String) | `Ticket::STATUSES` constant + string column |
| `Ticket.subtotal/tax/total` (computed) | `Ticket#subtotal/tax/total` methods (not stored) |
| `DataSeeder.kt` | `db/seeds.rb` |
