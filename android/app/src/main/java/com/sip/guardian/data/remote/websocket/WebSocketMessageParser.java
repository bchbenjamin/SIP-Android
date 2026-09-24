package com.sip.guardian.data.remote.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sip.guardian.data.remote.dto.IncidentDto;

/**
 * Parses gateway messages of the form:
 * { "type": "INCIDENT_NEW | INCIDENT_UPDATED | NODE_STATUS | POLICY_SYNCED | SYSTEM_ALERT",
 *   "timestamp": "...", "payload": { ... } }
 * Returns null for malformed or unknown messages — callers must tolerate lossy parsing.
 */
public class WebSocketMessageParser {

    private final Gson gson = new Gson();

    public WebSocketEvent parse(String text) {
        try {
            JsonObject root = JsonParser.parseString(text).getAsJsonObject();
            String type = root.get("type").getAsString();
            JsonObject payload = root.has("payload") && root.get("payload").isJsonObject()
                    ? root.getAsJsonObject("payload") : new JsonObject();

            switch (type) {
                case "INCIDENT_NEW":
                    return new WebSocketEvent.OnIncidentReceived(
                            gson.fromJson(payload, IncidentDto.class));
                case "INCIDENT_UPDATED":
                    return new WebSocketEvent.OnIncidentUpdated(
                            str(payload, "incidentId"), str(payload, "state"));
                case "NODE_STATUS":
                    return new WebSocketEvent.OnNodeStatusChanged(
                            str(payload, "nodeId"), str(payload, "status"),
                            str(payload, "lastHeartbeat"));
                case "POLICY_SYNCED":
                    return new WebSocketEvent.OnPolicySynced(
                            str(payload, "nodeId"), str(payload, "syncState"));
                case "SYSTEM_ALERT":
                    return new WebSocketEvent.OnSystemAlert(
                            str(payload, "level"), str(payload, "message"),
                            str(payload, "nodeId"));
                default:
                    return null;
            }
        } catch (Exception e) {
            return null; // malformed message — drop, connection stays alive
        }
    }

    private static String str(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : null;
    }
}
