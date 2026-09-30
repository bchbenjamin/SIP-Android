package com.sip.guardian.data.mapper;

import com.sip.guardian.data.remote.dto.IncidentDto;
import org.junit.Test;

import static org.junit.Assert.*;

class IncidentMapperTest {

    @Test
    void mapsFlatBackendIncidentFieldsIntoRoomEntity() {
        IncidentDto dto = new IncidentDto();
        dto.id = "incident-1";
        dto.state = "PENDING_VERIFICATION";
        dto.threatType = "WEAPON";
        dto.threatSeverity = "HIGH";
        dto.threatDescription = "Detected object";
        dto.latitude = 12.9716;
        dto.longitude = 77.5946;
        dto.locationReadable = "Bengaluru";
        dto.nodeId = "node-01";
        dto.createdAt = "2026-10-01T00:00:00Z";
        dto.updatedAt = "2026-10-01T00:01:00Z";

        IncidentMapper mapper = new IncidentMapper();
        var entity = mapper.toEntity(dto);

        assertEquals("incident-1", entity.id);
        assertEquals("PENDING_VERIFICATION", entity.state);
        assertEquals("WEAPON", entity.threatType);
        assertEquals("HIGH", entity.threatSeverity);
        assertEquals("Detected object", entity.threatDescription);
        assertEquals(12.9716, entity.latitude, 0.00001);
        assertEquals(77.5946, entity.longitude, 0.00001);
        assertEquals("Bengaluru", entity.locationReadable);
        assertEquals("node-01", entity.nodeId);
        assertTrue(entity.createdAtEpochMs > 0);
        assertTrue(entity.updatedAtEpochMs > entity.createdAtEpochMs);
    }

    @Test
    void mapsLegacyNestedThreatAndLocationFields() {
        IncidentDto dto = new IncidentDto();
        dto.id = "incident-legacy";
        dto.state = "DETECTED";
        dto.threat = new IncidentDto.ThreatDto();
        dto.threat.type = "FIRE";
        dto.threat.severity = "MEDIUM";
        dto.threat.description = "Smoke";
        dto.location = new IncidentDto.LocationDto();
        dto.location.latitude = 1.5;
        dto.location.longitude = 2.5;
        dto.location.humanReadable = "Test location";

        var entity = new IncidentMapper().toEntity(dto);

        assertEquals("FIRE", entity.threatType);
        assertEquals("MEDIUM", entity.threatSeverity);
        assertEquals("Smoke", entity.threatDescription);
        assertEquals(1.5, entity.latitude, 0.00001);
        assertEquals(2.5, entity.longitude, 0.00001);
    }
}
