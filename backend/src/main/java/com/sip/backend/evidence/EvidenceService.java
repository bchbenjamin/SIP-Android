package com.sip.backend.evidence;

import com.sip.backend.common.ResourceNotFoundException;
import com.sip.backend.entity.Evidence;
import com.sip.backend.repository.EvidenceRepository;
import com.sip.backend.repository.IncidentRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final IncidentRepository incidentRepository;

    public EvidenceService(EvidenceRepository evidenceRepository, IncidentRepository incidentRepository) {
        this.evidenceRepository = evidenceRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional(readOnly = true)
    public List<Evidence> getEvidenceForIncident(String incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException("INCIDENT_NOT_FOUND", "Incident not found: " + incidentId);
        }
        return evidenceRepository.findByIncidentId(incidentId);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getEvidenceMedia(String incidentId, Evidence.EvidenceType type) {
        List<Evidence> records = evidenceRepository.findByIncidentIdAndType(incidentId, type);
        if (records.isEmpty()) {
            throw new ResourceNotFoundException("Evidence not found for incident " + incidentId + " and type " + type);
        }
        Evidence e = records.get(0);
        if (e.storageKey == null || e.storageKey.isBlank()) {
            throw new ResourceNotFoundException("Evidence file not available for " + incidentId);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(e.mimeType != null ? e.mimeType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + e.id + "\"")
                .body(new ByteArrayResource(new byte[0]));
    }
}