# SIP Guardian — Android

Android control and monitoring client for the **AI-Powered Street Safety Device Network** (`ASIP_127` / `24UTAI13`).

## Layout

```
sip-guardian/
├── android/   # Android app — Java domain/data, Kotlin Compose UI
├── CONTEXT/   # Project context and implementation plan
└── docs/      # Status and integration notes
```

The gateway and Raspberry Pi components are part of the overall project plan but are not contained in this repository yet.

## Current Android coverage

| Area | Status |
|---|---|
| Compose shell + navigation | Implemented |
| Login + encrypted token storage | Implemented |
| Domain models + incident state machine | Implemented |
| Room local cache | Implemented |
| Retrofit/OkHttp + authenticated WebSocket client | Implemented |
| Dashboard + incident feed + filters | Implemented |
| Incident detail + operator verification UI | Implemented |
| Foreground WebSocket service + local notifications | Implemented |
| Unit tests for core state/policy/WebSocket parsing | Implemented |
| Evidence viewer / media timeline | Not yet implemented |
| Node map | Not yet implemented |
| Autopilot policy UI | Not yet implemented |
| Event history | Not yet implemented |
| Settings | Not yet implemented |
| FCM delivery | Not yet implemented |
| Gateway / Pi integration | Not yet implemented in this repository |

## Configuration

The Android build gets its API base URL from the Gradle property or environment variable:

```bash
./gradlew assembleDebug -PSIP_API_BASE_URL=http://<gateway-host>:<port>/
```

For local development, `.env.example` documents the variables used by Pi/deployment tooling. The Android app does **not** read Pi SSH credentials from `.env`.

## Build

CI uses JDK 17 and Gradle 8.9:

```bash
cd android
gradle clean testDebugUnitTest assembleDebug
```

See `docs/IMPLEMENTATION_STATUS.md` for the detailed state against the implementation plan.
