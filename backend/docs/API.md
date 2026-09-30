# SIP Backend API Documentation

## Base URL

`
Development: http://localhost:8080/
Production:  https://api.<domain>/
`

## Authentication

All endpoints except /auth/* and /actuator/health require a Bearer token:

`
Authorization: Bearer <access-token>
`

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| AUTH_INVALID_CREDENTIALS | 401 | Wrong username or password |
| AUTH_SESSION_EXPIRED | 401 | Token expired or revoked |
| AUTH_FORBIDDEN | 403 | Insufficient role permissions |
| VALIDATION_ERROR | 400 | Request body/query invalid |
| INCIDENT_NOT_FOUND | 404 | Incident does not exist |
| NODE_NOT_FOUND | 404 | Node does not exist |
| INVALID_STATE_TRANSITION | 409 | Illegal incident state change |
| NOT_FOUND | 404 | Generic resource not found |
| INTERNAL_ERROR | 500 | Unexpected server error |

All errors return this shape:

`json
{
  "timestamp": "2026-09-30T17:30:00Z",
  "status": 401,
  "code": "AUTH_INVALID_CREDENTIALS",
  "message": "Invalid username or password",
  "path": "/api/v1/auth/login",
  "requestId": "abc-123"
}
`

## Endpoints

### POST /api/v1/auth/login

**Request:**
`json
{ "username": "admin", "password": "ChangeMe123!" }
`

**Response 200:**
`json
{
  "token": "eyJhbGc...",
  "refreshToken": "uuid-refresh-token-id",
  "expiresIn": 900,
  "userId": "user-admin-001",
  "username": "admin",
  "role": "ADMIN"
}
`

### POST /api/v1/auth/refresh

**Request:**
`json
{ "refreshToken": "<refresh-token-id>" }
`

**Response 200:** Same shape as login response with new tokens.

### POST /api/v1/auth/logout

**Request:** No body. Requires valid JWT.

**Response:** 204 No Content

### GET /api/v1/incidents

| Param | Type | Description |
|-------|------|-------------|
| page | int | Zero-based page index (default: 0) |
| size | int | Page size (default: 50) |
| state | string | Filter by state |
| 	hreatType | string | Filter by threat type |
| 
odeId | string | Filter by node |
| rom | ISO datetime | Created after |
| 	o | ISO datetime | Created before |

**Response 200:**
`json
{
  "content": [
    {
      "id": "inc-001",
      "state": "DETECTED",
      "threatType": "WEAPON",
      "threatSeverity": "HIGH",
      "threatDescription": "...",
      "latitude": 12.9716,
      "longitude": 77.5946,
      "nodeId": "node-01",
      "nodeName": "Demo Edge Node 1",
      "autopilotHandled": false,
      "createdAt": "2026-09-30T17:00:00Z",
      "updatedAt": "2026-09-30T17:00:00Z"
    }
  ],
  "page": 0,
  "size": 50,
  "totalElements": 1,
  "totalPages": 1
}
`

### GET /api/v1/incidents/{id}

Returns full incident with detection, nnotations, esponseEvents, and evidence embedded.

### POST /api/v1/incidents/{id}/verify

**Request:**
`json
{
  "label": "TRUE_POSITIVE",
  "notes": "Confirmed weapon in frame",
  "confidence": 0.95
}
`

Valid labels: TRUE_POSITIVE, FALSE_POSITIVE, UNCERTAIN

**Response 200:** Updated IncidentDto

### GET /api/v1/incidents/{id}/events

Returns audit log entries for the incident.

### GET /api/v1/nodes

Returns all edge nodes.

### GET /api/v1/nodes/{id}

Returns single node.

### GET /api/v1/dashboard

**Response 200:**
`json
{
  "totalNodes": 1,
  "onlineNodes": 0,
  "activeThreats": 0,
  "recentIncidents": [],
  "systemHealth": "YELLOW"
}
`

### GET /api/v1/autopilot/policy?nodeId=node-01

Returns autopilot policy for the specified node.

### PUT /api/v1/autopilot/policy?nodeId=node-01

**Request:**
`json
{
  "enabled": true,
  "confidenceThreshold": 0.90,
  "deterrenceTimeoutSeconds": 5,
  "maxDeterrenceAttempts": 1,
  "allowedThreatTypes": ["WEAPON"],
  "neverAutonomousTypes": ["FIRE"]
}
`

**Response 200:** Updated AutopilotPolicyDto

### GET /api/v1/evidence/{incidentId}/image
### GET /api/v1/evidence/{incidentId}/video
### GET /api/v1/evidence/{incidentId}/audio
### GET /api/v1/evidence/{incidentId}/thumbnail

Returns the evidence file if available, or 404 NOT_FOUND.

## WebSocket

**Endpoint:** ws://host:port/ws/events

Connect before authenticating. The server broadcasts real-time events to all connected clients.

### Event Envelope

`json
{
  "type": "INCIDENT_UPDATED",
  "timestamp": "2026-09-30T17:05:00Z",
  "payload": { ... incidentDto ... },
  "eventId": "uuid"
}
`

### Event Types

| Type | When |
|------|------|
| INCIDENT_NEW | New incident ingested |
| INCIDENT_UPDATED | Incident state changed |
| NODE_STATUS_CHANGED | Node went online/offline |
| SYSTEM_ALERT | System-level alert |