package com.sip.guardian.data.remote.websocket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class WebSocketMessageParserTest {
    private final WebSocketMessageParser parser = new WebSocketMessageParser();

    @Test public void parsesIncidentEvent() {
        WebSocketEvent event = parser.parse(
                "{\"type\":\"INCIDENT_NEW\",\"payload\":{\"id\":\"i-1\",\"state\":\"DETECTED\"}}");
        assertNotNull(event);
        assertNotNull(((WebSocketEvent.OnIncidentReceived) event).incident);
    }

    @Test public void dropsMalformedMessages() {
        assertNull(parser.parse("{not-json"));
        assertNull(parser.parse("{\"type\":\"UNKNOWN\"}"));
    }
}
