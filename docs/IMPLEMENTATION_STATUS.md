# Implementation Status

Generated 2026-09-24. This file describes the current contents of this repository against the project implementation plan.

## Implemented in this repository

### Android

- Gradle/Compose application shell with Java domain/data layers and Kotlin Compose UI.
- Hilt dependency injection.
- Domain models including the incident aggregate and validated incident state transitions.
- Separate AI detection results and human annotations.
- Autopilot policy model and safety/escalation rules.
- Room local cache for incidents and nodes.
- Retrofit/OkHttp REST layer with authenticated requests and token refresh support.
- Encrypted access/refresh token storage.
- Authenticated WebSocket client with reconnect backoff and typed event parsing.
- Foreground WebSocket service with Android 14+ foreground-service declarations and Android 15 timeout handling.
- Local incident/system notifications.
- Login, Dashboard, Incident Feed, filters, and Incident Detail verification UI.
- Unit tests for incident state transitions, autopilot policy authorization, and WebSocket message parsing.
- Configurable API base URL through Gradle property or `SIP_API_BASE_URL` environment variable.
- CI workflow that runs unit tests and assembles a debug APK.

## Not yet implemented

- Evidence/media viewer and event timeline UI.
- Node map UI.
- Autopilot policy management UI.
- Event history UI.
- Settings UI.
- FCM-based push delivery for process-killed/background notification delivery.
- Gateway / Spring Boot REST + MQTT bridge.
- Raspberry Pi publisher/subscriber/evidence-uploader components.
- End-to-end gateway ↔ Pi ↔ Android integration tests.
- Production Room migrations.

## Configuration notes

The root `.env.example` is for local/Pi deployment tooling. Its SSH credentials are not consumed by the Android app and must never be committed with real values.

The Android app's API endpoint is supplied at build/runtime configuration time; do not hardcode device or server credentials into the APK.
