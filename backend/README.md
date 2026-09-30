# SIP Backend

Spring Boot backend for the **AI-Powered Street Safety Device Network** (24UTAI13).

Acts as the single system-of-record and control plane:
- **Raspberry Pi** sends AI detections via MQTT
- **Android** operators interact via REST / WebSocket
- **Neon PostgreSQL** is the canonical data store

## Architecture

`
Neon PostgreSQL  ?  Spring Boot Backend  ?  Android App
                          ?
                     MQTT Broker
                          ?
                  Raspberry Pi 5 (AI nodes)
`

## Quick Start

### Prerequisites

- Java 17+
- Gradle 8.9+ (wrapper included)
- Neon PostgreSQL branch or local PostgreSQL

### 1. Configure environment

`ash
cp .env.example .env
# Edit .env and set:
#   DATABASE_URL=jdbc:postgresql://<host>/<db>?sslmode=require
#   JWT_SECRET=<at-least-32-char-random-string>
`

### 2. Run migrations

Migrations run automatically on startup via Flyway.

### 3. Build and run

`ash
./gradlew bootRun
`

Or build a JAR:

`ash
./gradlew bootJar
java -jar build/libs/sip-backend-0.1.0.jar
`

### 4. Verify

`ash
curl http://localhost:8080/actuator/health
`

## Mock Login Credentials

> For development only. Requires running backend.

| Username   | Password     | Role     |
|------------|-------------|----------|
| dmin    | ChangeMe123! | Admin  |
| operator | ChangeMe123! | Operator |

## API Overview

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /api/v1/auth/login | No | Login, returns JWT |
| POST | /api/v1/auth/refresh | No | Refresh access token |
| POST | /api/v1/auth/logout | Yes | Revoke refresh tokens |
| GET | /api/v1/incidents | Yes | Paginated incident feed |
| GET | /api/v1/incidents/{id} | Yes | Single incident with detail |
| POST | /api/v1/incidents/{id}/verify | Yes | Verify/reject incident |
| GET | /api/v1/incidents/{id}/events | Yes | Audit log for incident |
| GET | /api/v1/nodes | Yes | All edge nodes |
| GET | /api/v1/nodes/{id} | Yes | Single node |
| GET | /api/v1/dashboard | Yes | Dashboard summary |
| GET | /api/v1/autopilot/policy | Yes | Get autopilot policy |
| PUT | /api/v1/autopilot/policy | Admin | Update autopilot policy |
| GET | /api/v1/evidence/{id}/image | Yes | Evidence image |
| GET | /api/v1/evidence/{id}/video | Yes | Evidence video |
| WS | /ws/events | No* | Realtime events stream |

*WebSocket authentication is handled via the Authorization header on the first HTTP handshake
or the /ws/events?token=<access-token> query parameter.

## Testing

`ash
./gradlew test
`

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| DATABASE_URL | Yes | — | PostgreSQL JDBC URL |
| DATABASE_USERNAME | No | sipuser | DB username |
| DATABASE_PASSWORD | No | — | DB password |
| JWT_SECRET | Yes | — | JWT signing key (min 32 chars) |
| JWT_ACCESS_EXPIRY | No | 900 | Access token lifetime (seconds) |
| JWT_REFRESH_EXPIRY | No | 604800 | Refresh token lifetime (seconds) |
| SERVER_PORT | No | 8080 | HTTP server port |

## Deployment

See docs/DEPLOYMENT.md for full deployment instructions including Render and production setups.