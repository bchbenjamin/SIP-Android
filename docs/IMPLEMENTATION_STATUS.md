# Implementation Status

Generated 2026-09-24. What exists in this archive vs. the full plan.

## Fully implemented

**Android (`android/`)**
- Gradle project shell: Compose BOM, Room + RxJava3, Retrofit/OkHttp, Hilt, Coil, Media3, EncryptedSharedPreferences (Apache-2.0 stack per plan §24)
- Complete domain layer (plan §10): all enums, `Incident` aggregate with enforced
  state machine (`Incident.transitionTo` validates plan §19 transitions),
  immutable `DetectionResult`, separate `HumanAnnotation` with disagreement
  tracking, `AutopilotPolicy.isAuthorizedFor()` mirroring the Pi-side check,
  `Node` heartbeat-staleness logic (plan §20)
- Repository interfaces (Java) + implementations: network-first with Room
  offline fallback (plan §27 edge cases), DAO `Flowable` observation
- `VerifyIncidentUseCase` with auth + state validation; `LoginUseCase`,
  `GetIncidentsUseCase`, autopilot use cases
- Data layer: full DTO set, `SipApiService` matching plan §13 endpoints,
  `AuthInterceptor` + `TokenRefreshAuthenticator`, `SecureTokenStore`
  (AES-256-SIV/GCM, never logs tokens)
- `SipWebSocketClient`: header-based auth (token NOT in URL, plan §14),
  exponential backoff 1s→30s, typed events, SUBSCRIBE handshake
- `WebSocketService` (foreground) + `NotificationService` (provider
  abstraction for later FCM, plan §27) + `EvidenceCacheService`
- UI: theme (plan §22 palette/shapes), nav destinations, `ThreatBadge`,
  `ConfidenceMeter`, `IncidentCard`, Login, Dashboard, Incident Feed with
  filter chips

## Not yet implemented (next increments)

- `IncidentDetailScreen` + ViewModel (VERIFY/REJECT UI), `EvidenceViewer`
  (Coil + Media3), `EventTimeline`, `NodeMapScreen`, `AutopilotScreen`,
  `EventHistoryScreen`, `SettingsScreen`, `SipNavGraph`, `MainActivity`
- Gateway (`gateway/`) and Pi integration (`pi/`) — Increments 3 and 10
- Unit tests (plan §27)

## Known notes

- `fallbackToDestructiveMigration()` on Room is flagged for prototype use only
- `.env` (`IP_ADDRESS`, `USERNAME`, `PASSWORD`) is for dev tooling only;
  nothing in the app reads it (plan §25/§26)
