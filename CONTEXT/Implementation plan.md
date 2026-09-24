# AI-Powered Street Safety Device Network — Android Application
## Agentic AI Implementation-Planning Prompt

You are an **agentic software architect and senior Android engineer** working on my academic SIP/ASIP project:

**Project:** AI-Powered Street Safety Device Network  
**Project identifiers:** `ASIP_127` / `24UTAI13`  
**Institution:** Atria Institute of Technology, Bengaluru  
**Project context:** An AI-powered street safety device network using Raspberry Pi 5 edge nodes, multimodal sensing, AI/ML threat detection, autonomous deterrence, emergency escalation, and fault-tolerant networking.

Your first task is to produce a **complete implementation plan and technical architecture for the Android application** described below.

Do NOT immediately start generating the entire application. First inspect the environment, inspect the referenced repositories, inspect the Raspberry Pi if useful, understand the architecture, and then produce a structured implementation plan.

---

# 1. EXISTING PROJECT ARCHITECTURE

The overall SIP system already has the following conceptual architecture:

```text
Camera
Thermal Sensor
PIR Sensor
Microphone
      │
      ▼
Raspberry Pi 5 Edge Compute Core
      │
      ├── Visual Detection
      ├── Audio Detection
      ├── Sensor Fusion
      ├── Threat Classification
      └── Severity / Deterrability Classification
              │
              ├── Deterrable Threat
              │       ↓
              │   Autonomous Deterrence
              │
              └── Severe / Non-deterrable Threat
                      ↓
                 Emergency Escalation
```

The Raspberry Pi 5 remains the **edge inference and response device**.

The Android application is primarily the **operator, monitoring, verification, configuration and control interface**.

Do NOT move the primary AI inference workload into the Android application unless there is a specific architectural reason to do so.

---

# 2. PRIMARY ANDROID APP REQUIREMENTS

The Android application must support:

## A. Real-time threat incidents

When the Raspberry Pi detects a potential threat, it should create an incident and transmit it to the backend/application.

An incident should contain, where available:

- Incident ID
- Threat type
- Threat category
- AI confidence score
- Timestamp
- Raspberry Pi node ID
- Node status
- GPS coordinates
- Human-readable location
- Detection metadata
- Image/visual evidence
- Video evidence
- Audio evidence
- Sensor information
- AI model/version information
- Detection status
- Current response state
- Whether autopilot was active
- Whether deterrence was triggered
- Result of deterrence
- Human verification result, if applicable

The Android application should receive incidents in real time rather than relying exclusively on periodic polling.

Evaluate REST + WebSocket/SSE architecture and select the appropriate mechanism, explaining the decision.

---

# 3. EVIDENCE AND HUMAN VERIFICATION

This is a particularly important part of the system.

When a threat is detected:

```text
Threat Detection
      ↓
Evidence Capture
      ├── Image
      ├── Video
      └── Audio
      ↓
Incident Creation
      ↓
Android Application
      ↓
Human Verification
```

The operator must be able to inspect the evidence before making a decision.

The incident-detail screen should allow the operator to:

- View the captured image
- Play the captured video
- Play the captured audio
- View the location
- View the threat classification
- View AI confidence
- View timestamp
- View node information
- View relevant detection metadata
- Verify the incident
- Reject the incident
- See what action the system has already taken
- See the incident's complete event history

The system must maintain an auditable state machine such as:

```text
DETECTED
   ↓
EVIDENCE_CAPTURED
   ↓
PENDING_VERIFICATION
   ├── VERIFIED
   │      ↓
   │  DETERRENCE / ESCALATION
   │
   └── REJECTED
```

Do not blindly copy this state machine if a better design exists; explain any changes.

---

# 4. HUMAN VERIFICATION IS ALSO TRAINING DATA

This requirement is critical.

The human verification process is not merely an operational feature.

It creates **labelled data for future model training and evaluation**.

When an operator verifies or rejects an incident, retain:

### Original AI output

- Original predicted class
- Original confidence
- Model version
- Timestamp
- Node
- Detection metadata

### Evidence

- Image
- Video
- Audio
- Relevant sensor data

### Human label

For example:

```text
AI prediction: WEAPON
AI confidence: 0.91
Human label: FALSE_POSITIVE
```

or:

```text
AI prediction: LOITERING
AI confidence: 0.87
Human label: TRUE_POSITIVE
```

The architecture should support a proper labelled-data lifecycle:

```text
Raw Sensor Data
      ↓
AI Prediction
      ↓
Evidence Package
      ↓
Human Verification
      ↓
Ground-Truth / Human Label
      ↓
Labelled Dataset
      ↓
Model Evaluation / Retraining
```

Design suitable data models and storage structures for this.

The system should preserve the original AI prediction even after human correction.

Do NOT overwrite the AI prediction with the human label.

Instead, store them as separate fields.

Also consider:

- annotation versioning
- annotator/operator ID
- annotation timestamp
- disagreement handling
- multiple annotations
- confidence/quality of human labels
- dataset export
- anonymization/privacy
- evidence retention policies
- dataset split management
- model version tracking

The architecture should make it possible to eventually export verified incidents into a dataset suitable for further ML training.

---

# 5. AUTOPILOT MODE

The system must have a completely optional autonomous mode.

There must be a clear Android UI switch for:

```text
AUTOPILOT
[ OFF / ON ]
```

However, understand this carefully:

**Autopilot must NOT depend on the Android application being online.**

The Raspberry Pi is the edge device.

Therefore:

### Manual mode

```text
Pi detects threat
      ↓
Evidence captured
      ↓
Incident sent to backend
      ↓
Android receives incident
      ↓
Human verifies
      ↓
Deterrence command
      ↓
Pi performs deterrence
```

### Autopilot mode

```text
Pi detects threat
      ↓
Evidence captured
      ↓
Threat verification/classification
      ↓
Autopilot policy evaluation
      ↓
Automatic deterrence
      ↓
Incident + action log sent to backend
      ↓
Android displays what happened
```

If connectivity is temporarily unavailable, the Pi should still be capable of executing an authorized autonomous policy.

The Android app should therefore configure the **autonomy policy**, while the Raspberry Pi executes that policy locally.

Do NOT implement:

```text
Autopilot ON
→ Android must approve every action
```

That would not actually be autonomous.

---

# 6. AUTOPILOT POLICY

Do not implement autopilot as a single scattered boolean.

Design a proper policy/configuration abstraction.

For example:

```text
AutopilotPolicy
├── enabled
├── allowedThreatTypes
├── confidenceThreshold
├── deterrenceTimeout
├── escalationRules
└── safetyConstraints
```

The UI should eventually support configuration such as:

```text
AUTOPILOT
────────────────────

Status
[ ON ]

Allowed autonomous responses

☑ Wildlife
☑ Loitering
☐ Weapon
☐ Assault
☐ Robbery
☐ Unknown threat

Confidence threshold
85%

Deterrence timeout
5 seconds
```

The exact policy model should be determined after analysing the existing SIP architecture.

The system must distinguish:

- detection
- classification
- verification
- deterrence
- escalation
- human approval
- autonomous authorization

These are not the same thing.

---

# 7. ANDROID TECHNOLOGY REQUIREMENT

The application should use:

## Java as the primary application architecture language

I specifically want this project to align with my Java-oriented career/resume roadmap.

Therefore:

**Prefer Java substantially over Kotlin for the application/business architecture.**

Use Java for areas such as:

- Domain models
- Business logic
- Repository layer
- API integration
- WebSocket handling
- Authentication
- Incident management
- Autopilot policy
- Data management
- Services
- Use cases
- Application architecture

## UI: Option B

Use:

**Jetpack Compose + Material 3**

for the modern UI.

This necessarily introduces Kotlin because Compose is Kotlin-based.

Keep Kotlin concentrated around the UI/Compose boundary wherever reasonably practical.

Do NOT rewrite the entire application architecture in Kotlin simply because Compose is being used.

The target architecture should therefore be conceptually:

```text
Java
 ├── Domain
 ├── Data
 ├── Repositories
 ├── Use Cases
 ├── Services
 ├── Business Logic
 └── Application Architecture

Kotlin
 └── Jetpack Compose / Material 3 UI
```

Use clean Java/Kotlin interoperability rather than forcing an artificial separation when it would make the project worse.

---

# 8. OOP REQUIREMENT

This is an academic project and the implementation must be **easy to understand, explain, maintain and defend in a viva**.

Do not produce a giant collection of procedural functions.

Use proper Object-Oriented Programming principles.

Explicitly demonstrate:

- Encapsulation
- Abstraction
- Inheritance where genuinely appropriate
- Polymorphism
- Composition
- Interfaces
- Dependency inversion where useful
- Single responsibility
- Separation of concerns

However:

**Do not create unnecessary classes merely to claim that OOP was used.**

Every class should have a clear responsibility.

Prefer understandable structures such as:

```text
Incident
├── Threat
├── Location
├── EvidenceBundle
└── DetectionMetadata
```

and abstractions such as:

```text
IncidentRepository
     ↑
RemoteIncidentRepository
LocalIncidentRepository
```

or:

```text
DeterrenceStrategy
     ├── AudioDeterrenceStrategy
     ├── VisualDeterrenceStrategy
     └── CombinedDeterrenceStrategy
```

where such abstractions genuinely benefit the system.

The implementation plan must explicitly explain the purpose and relationship of major classes.

---

# 9. ARCHITECTURE

Prefer a layered/clean architecture that remains understandable to a student developer.

Evaluate something along the lines of:

```text
presentation/
domain/
data/
network/
services/
models/
utils/
```

or a better alternative if justified.

The implementation plan must provide:

- Complete package structure
- Responsibilities of every major package
- Responsibilities of major classes
- Dependency direction
- Data flow
- UI → ViewModel → Use Case → Repository → API flow
- Real-time event flow
- Evidence retrieval flow
- Autopilot configuration flow
- Human verification flow
- Training-label flow

Avoid unnecessary enterprise architecture.

This should be sophisticated enough for a strong engineering project but understandable enough for an undergraduate student to maintain.

---

# 10. UI / DESIGN

Use:

**Jetpack Compose + Material 3**

as the primary design system.

Take visual inspiration from modern MIUI/Xiaomi-style interfaces where appropriate, particularly:

- clean information density
- rounded cards
- strong status indicators
- polished switches
- modern dark mode
- dashboard layouts
- media-focused incident views

But do NOT attempt to clone MIUI.

Material 3 should remain the underlying design system.

The UI should look like a serious security/operations application rather than a generic CRUD application.

---

# 11. INITIAL SCREEN STRUCTURE

Evaluate and refine the following screen architecture.

## Dashboard

Show:

- System status
- Connected node count
- Online/offline/degraded nodes
- Active threats
- Recent incidents
- Autopilot status
- System health

## Incident Feed

Chronological incident list with:

- Severity
- Threat type
- Confidence
- Node
- Time
- Verification status
- Response status

## Incident Details

Show:

- Image
- Video
- Audio
- Threat classification
- Confidence
- GPS/location
- Timestamp
- Node
- Sensor information
- AI prediction
- Human verification
- Response history
- Event timeline

Actions:

```text
VERIFY
REJECT
```

when applicable.

## Node Map

Show Raspberry Pi nodes and their status:

```text
GREEN  = Online
ORANGE = Degraded
RED    = Offline
```

Consider displaying:

- node location
- node ID
- last heartbeat
- current threat state
- connectivity
- battery/power status if available

This should integrate conceptually with the SIP's fault-tolerant mesh architecture.

## Autopilot

Dedicated screen for:

- master enable/disable
- autonomous threat classes
- thresholds
- response configuration
- safety constraints
- current node policy
- policy synchronization status

## Event History

Provide a chronological audit log such as:

```text
02:14:37  Threat detected
02:14:37  Evidence captured
02:14:38  Incident created
02:14:41  Autopilot policy evaluated
02:14:41  Deterrence activated
02:14:46  Threat cleared
```

## Settings

Potentially include:

- account
- notification preferences
- backend configuration
- node management
- evidence settings
- dataset/export settings
- security settings

Determine which belong in the first implementation.

---

# 12. BACKEND COMMUNICATION

Do not make the Android app communicate directly with every sensor.

Prefer:

```text
Android
   ↓
Backend / Gateway
   ↓
Raspberry Pi Nodes
```

Potential technologies:

- REST API
- WebSocket
- MQTT
- HTTPS
- object/file storage

Evaluate the existing SIP architecture and determine the appropriate combination.

For large evidence files, do NOT put entire videos/audio files inside ordinary JSON API responses.

Prefer an architecture such as:

```text
Incident JSON
 ├── metadata
 ├── location
 ├── threat
 └── evidence references

Evidence Storage
 ├── image
 ├── video
 └── audio
```

The Android app retrieves evidence when required.

---

# 13. RASPBERRY PI ACCESS

The Raspberry Pi 5 is currently powered on and connected to the same Wi-Fi network as the development machine.

The project has Raspberry Pi connection information available in the project's `.env` file.

The environment variables are:

```text
IP_ADDRESS
USERNAME
PASSWORD
```

Do NOT hardcode these credentials into source code.

Do NOT print the password.

Do NOT commit `.env`.

Do NOT place the credentials in documentation, README files, logs, Git commits, or generated source code.

If you need to inspect or interact with the Raspberry Pi, read the values from the local `.env` file.

Use SSH/SFTP or another appropriate secure mechanism.

Before modifying anything on the Raspberry Pi:

1. Inspect the current environment.
2. Identify the OS.
3. Identify the existing project files.
4. Identify running services/processes.
5. Identify network configuration.
6. Determine whether existing SIP prototype software is present.
7. Avoid destroying or overwriting existing work.
8. Explain any proposed changes before making destructive changes.

The Pi should be treated as an existing development/test node.

If direct access is unavailable, continue with a local implementation plan and explicitly identify what must later be tested on the Pi.

---

# 14. REPOSITORIES TO INSPECT

Clone and inspect these repositories before designing the implementation:

```text
https://github.com/bappaditya-paul/AI-Surveillance-System
https://github.com/Amaldaskm7736/ai-video-intelligence-platform
https://github.com/Fizza-Awan/ai-security-surveillance
https://github.com/K-saif/Weapon-Detection-and-Alarm-System
https://github.com/MahmoudMTaha/YoloV4-Weapons-Detection
https://github.com/kgdash116/Camera-Surveillance-Dashboard--Cloud-Technologies
https://github.com/Te-Stack/Security-Dashboard-
```

These repositories are **reference/reuse candidates**, not mandatory dependencies.

For every repository:

1. Clone it.
2. Inspect its README.
3. Inspect its directory structure.
4. Inspect its implementation.
5. Identify reusable components.
6. Identify useful algorithms.
7. Identify useful UI patterns.
8. Identify useful API/data models.
9. Inspect its license.
10. Determine whether code can legally/practically be reused.
11. Determine whether adaptation is required.

Produce a table:

```text
Repository
Relevant functionality
Technology
License
Potential reuse
Potential problems
Recommended action
```

Do not blindly copy code.

Prefer reuse/adaptation where it reduces unnecessary reimplementation, but prioritize:

1. Correctness
2. Maintainability
3. Security
4. License compatibility
5. Architectural consistency
6. Understandability
7. Code reuse

Do not optimize for minimum line count.

Avoid reinventing the wheel where a suitable, compatible implementation already exists.

---

# 15. DO NOT DUPLICATE THE EXISTING AI SYSTEM

The Android application is not intended to replace the Raspberry Pi inference pipeline.

The Pi remains responsible for things such as:

- camera acquisition
- thermal acquisition
- PIR sensing
- microphone acquisition
- AI inference
- multimodal fusion
- threat classification
- severity classification
- deterrence execution
- local autonomy

The Android application should primarily handle:

- monitoring
- visualization
- incident management
- human verification
- operator control
- configuration
- node management
- evidence inspection
- labelled-data management
- audit history

If you believe functionality should move between components, explain why before proposing it.

---

# 16. SECURITY

Design security from the beginning.

Evaluate:

- HTTPS/TLS
- authentication
- authorization
- secure token storage
- API authentication
- WebSocket authentication
- role-based access if useful
- secure evidence URLs
- credential management
- Android secure storage
- SSH key authentication for development
- replay protection
- command authorization
- audit logging

Do not store Raspberry Pi credentials inside the Android APK.

Do not embed backend secrets in the application.

Do not expose unrestricted deterrence APIs.

Commands such as:

```text
ENABLE_AUTOPILOT
DISABLE_AUTOPILOT
TRIGGER_DETERRENCE
UPDATE_POLICY
```

must be authenticated and authorized.

---

# 17. TRAINING-DATA ARCHITECTURE

Design the data model so that the following are separately represented:

```text
AI Prediction
Human Annotation
Final Dataset Label
Model Version
Evidence
Incident
Response Action
```

For example:

```text
Incident
├── Detection
│   ├── modelVersion
│   ├── predictedClass
│   └── confidence
│
├── Evidence
│   ├── image
│   ├── video
│   └── audio
│
├── HumanAnnotation
│   ├── label
│   ├── annotator
│   ├── timestamp
│   └── notes
│
└── Response
    ├── mode
    ├── action
    └── result
```

The architecture should support future ML workflows without requiring the Android application to become the ML training environment.

---

# 18. DATA MODELS

Design proper domain models.

At minimum evaluate:

```text
Incident
Threat
Evidence
EvidenceBundle
Location
DetectionResult
HumanAnnotation
Node
NodeStatus
DeterrenceAction
AutopilotPolicy
ResponseEvent
AuditEvent
ModelVersion
User
```

Do not blindly use this exact list.

Refine it according to the architecture.

For each major model, explain:

- fields
- relationships
- responsibility
- lifecycle
- serialization requirements

---

# 19. STATE MANAGEMENT

Define explicit states wherever appropriate.

For incidents:

```text
DETECTED
EVIDENCE_CAPTURED
PENDING_VERIFICATION
VERIFIED
REJECTED
DETERRENCE_REQUESTED
DETERRENCE_ACTIVE
DETERRENCE_COMPLETED
ESCALATED
RESOLVED
```

For nodes:

```text
ONLINE
DEGRADED
OFFLINE
UNKNOWN
```

For autopilot:

```text
DISABLED
ENABLED
SYNC_PENDING
ACTIVE
ERROR
```

Refine these states if necessary.

Avoid scattered boolean flags where a proper state machine is more appropriate.

---

# 20. DEVELOPMENT METHODOLOGY

The SIP uses an **Incremental Development Model**.

Therefore structure the implementation into incremental deliverables.

For example:

### Increment 1
Android shell + navigation + Material 3 UI

### Increment 2
Backend communication + authentication

### Increment 3
Real-time incident delivery

### Increment 4
Evidence viewing

### Increment 5
Human verification

### Increment 6
Training-data/annotation pipeline

### Increment 7
Node management

### Increment 8
Autopilot configuration

### Increment 9
Pi integration

### Increment 10
End-to-end testing

Do not assume these exact increments are correct. Produce an optimized implementation sequence.

Every increment should leave behind a working system.

---

# 21. TESTING

Include:

- Unit tests
- Repository tests
- API tests
- WebSocket tests
- UI tests
- State-machine tests
- Evidence playback tests
- Authentication tests
- Autopilot policy tests
- Pi integration tests
- Failure/reconnection tests
- Offline behavior tests
- Evidence integrity tests

Especially test:

```text
What happens if Wi-Fi disappears?
What happens if the Pi disappears?
What happens if Android disappears?
What happens if backend disappears?
What happens if evidence upload fails?
What happens if an incident is duplicated?
What happens if the same incident arrives twice?
What happens if autopilot policy synchronization fails?
```

The system must not silently enter an unsafe state.

---

# 22. OUTPUT REQUIRED FROM YOU

Before implementing the application, produce a comprehensive implementation plan containing:

## 1. Executive architecture summary

## 2. System architecture diagram

## 3. Android architecture diagram

## 4. Backend architecture

## 5. Raspberry Pi ↔ Backend ↔ Android data flow

## 6. Manual verification sequence

## 7. Autopilot sequence

## 8. Training-data lifecycle

## 9. Complete Android package structure

## 10. Major Java classes

For every major class:

- responsibility
- attributes
- methods
- relationships
- OOP principles demonstrated

## 11. Kotlin/Compose classes

Clearly identify which parts require Kotlin because of Compose.

## 12. Domain model / ER-style diagram

## 13. API specification

Include endpoints, request bodies, response bodies and authentication.

## 14. WebSocket/event specification

## 15. MQTT integration plan

## 16. Evidence-storage architecture

## 17. Database schema

## 18. Autopilot policy model

## 19. Incident state machine

## 20. Node state machine

## 21. UI screen map

## 22. Material 3 design system

## 23. Repository reuse analysis

## 24. License analysis

## 25. Security architecture

## 26. Testing strategy

## 27. Incremental implementation roadmap

## 28. Deployment architecture

## 29. Raspberry Pi integration plan

## 30. Risks, trade-offs and unresolved questions

---

# 23. IMPORTANT AGENT BEHAVIOUR

You are acting as an **agentic engineering architect**, not a code-completion chatbot.

Therefore:

- Inspect before designing.
- Search the local project before inventing files.
- Inspect the supplied GitHub repositories.
- Inspect the Raspberry Pi where useful.
- Reuse suitable existing implementations.
- Do not blindly copy code.
- Respect software licenses.
- Do not hardcode credentials.
- Do not destroy existing work.
- Do not create unnecessary abstractions.
- Prefer readable OOP.
- Explain architectural decisions.
- Keep Java as the primary application architecture language.
- Use Kotlin primarily where required for Compose.
- Use Material 3.
- Keep AI inference primarily on the Raspberry Pi.
- Treat human verification as a source of labelled training data.
- Preserve original AI predictions separately from human labels.
- Make autopilot genuinely autonomous at the edge.
- Make autopilot optional and policy-controlled.
- Maintain a complete audit trail.
- Design for intermittent connectivity.
- Design for future multi-node deployment.
- Design for future model retraining.
- Do not optimize merely for fewer lines of code.

Most importantly:

**Do not generate a superficial generic Android architecture.**

This application is part of a larger multimodal AI/IoT safety system. The Android architecture must be designed around that actual system and its future evolution.

First produce the complete implementation plan.

Only after the architecture has been reviewed and approved should implementation begin.