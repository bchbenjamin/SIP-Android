# SIP Guardian — Implementation

Implementation of `implementation_plan.md`: AI-Powered Street Safety Device Network.

## Layout

```
sip-guardian/
├── android/   # Android app — Java domain/data, Kotlin Compose UI
├── gateway/   # Spring Boot gateway (MQTT ↔ REST/WebSocket bridge)
├── pi/        # Raspberry Pi MQTT + evidence upload integration
└── docs/      # Status and integration notes
```

## Increment Coverage (plan §28)

| Increment | Status | Location |
|---|---|---|
| 1 Shell + navigation | Implemented | `android/` (theme, nav graph, screens, Login) |
| 2 Domain models + Room | Implemented | `android/.../domain`, `data/local` |
| 3 Gateway + REST API | Implemented | `gateway/` (controllers, services, JWT, schema.sql) |
| 4 Network layer + repos | Implemented | `android/.../data` (Retrofit, WS client, repo impls) |
| 5 Dashboard + Feed | Implemented | `android/.../ui/screen/dashboard`, `incidents` |
| 6 Incident detail + evidence | Implemented | `IncidentDetailScreen`, `EvidenceViewer` |
| 7 Verification + training data | Implemented | `VerifyIncidentUseCase`, annotation flow, gateway verify endpoint |
| 8 WebSocket real-time | Implemented | `SipWebSocketClient`, `WebSocketService`, broadcaster |
| 9 Autopilot + nodes | Implemented | `AutopilotScreen`, `AutopilotPolicy.isAuthorizedFor` |
| 10 Pi integration | Code ready — requires live Pi | `pi/` (publisher, subscriber, uploader, systemd) |

## Credential handling (.env)

`IP_ADDRESS`, `USERNAME`, `PASSWORD` are used **only** by dev tooling (`pi/deploy.sh`)
via `set -a; source .env`. They are never read by the Android app, never committed,
never printed. See `pi/README.md`.
