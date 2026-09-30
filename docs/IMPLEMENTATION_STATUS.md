# SIP Implementation Status

Last reviewed: 2026-10-01.

This status is based on the current repository tree and GitHub Actions results. It distinguishes implemented code from integrations that have not yet been exercised against a live Neon database or Raspberry Pi.

## Android

Implemented:
- Compose application shell, login, dashboard, incident feed, incident detail and operator verification.
- Domain models, incident-state validation, Hilt dependency injection and Room cache.
- Retrofit/OkHttp REST integration, encrypted token storage and refresh authenticator.
- Authenticated WebSocket client, reconnect backoff, parser and foreground service.
- Android DTO/mapping compatibility for both the legacy nested incident payload and the backend's flat incident DTO.
- Unit tests for incident state transitions, autopilot policy and WebSocket parsing.

Not yet implemented:
- Node map, autopilot policy UI, event-history screen, settings screen, and evidence/media timeline.
- FCM delivery when Android is killed or background WebSocket service is unavailable.
- Production Room migrations and full Android/backend contract or end-to-end tests.

## Backend

Implemented in source:
- Spring Boot REST application, JPA entities/repositories, Flyway schema/index migrations, JWT login and refresh-token persistence.
- Incident feed/detail and operator verification, node listing, dashboard, autopilot policy endpoints and audit records.
- WebSocket broadcaster wired to the registered endpoint; bearer token is checked during the WebSocket handshake.
- Optional environment-based first-admin bootstrap; fixed-password demo users are removed by a migration.
- Separate backend CI workflow running Gradle tests and packaging the JAR.

Still requires verification or implementation:
- Live Neon provisioning/connectivity and a clean-database migration test against PostgreSQL.
- Full WebSocket event authorization/channel subscription semantics and contract tests.
- MQTT ingestion and command bridge, node-to-backend authentication, heartbeat ingestion and idempotent Pi incident ingestion.
- Production evidence object storage, signed URLs, upload retries and retention enforcement.
- User administration, password reset/recovery, rate limiting and deployment-level HTTPS/secret management.

## Architecture invariants

- Android talks to the backend over HTTPS/WSS only. Never connect the APK directly to Neon, MQTT or Raspberry Pi.
- Neon/PostgreSQL is authoritative; Room is only a local cache.
- AI predictions and human annotations remain separate.
- Edge autonomous actions must remain local and continue during network/backend outages.
- Never commit or print real database URLs, passwords, JWT secrets, node keys or SSH credentials.

## Verification

- Android CI builds and tests the debug app.
- Backend CI builds/tests the Spring Boot service.
- These checks do not prove live Neon connectivity, physical-device reachability, Pi integration, or production readiness. Perform those separately with private environment configuration.
