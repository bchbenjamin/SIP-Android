package com.sip.backend.autopilot;

import com.sip.backend.common.ResourceNotFoundException;
import com.sip.backend.dto.AutopilotPolicyDto;
import com.sip.backend.entity.AutopilotPolicy;
import com.sip.backend.repository.AutopilotPolicyRepository;
import com.sip.backend.repository.NodeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AutopilotService {

    private final AutopilotPolicyRepository policyRepository;
    private final NodeRepository nodeRepository;
    private final ObjectMapper objectMapper;

    public AutopilotService(AutopilotPolicyRepository policyRepository,
                            NodeRepository nodeRepository) {
        this.policyRepository = policyRepository;
        this.nodeRepository = nodeRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional(readOnly = true)
    public AutopilotPolicyDto getPolicy(String nodeId) {
        AutopilotPolicy policy = policyRepository.findById(nodeId).orElse(null);
        if (policy == null) {
            policy = new AutopilotPolicy();
            policy.nodeId = nodeId;
            policy.enabled = false;
            policy.confidenceThreshold = 0.85;
            policy.deterrenceTimeoutSeconds = 5;
            policy.autoEscalateOnDeterrenceFailure = true;
            policy.maxDeterrenceAttempts = 1;
            policy.requireMinimumConfidence = true;
            policy.requireMultiModalConfirmation = false;
            policy.allowedThreatTypes = "[]";
            policy.alwaysEscalateTypes = "[]";
            policy.neverAutonomousTypes = "[]";
            policy.syncState = AutopilotPolicy.SyncState.DISABLED;
        }
        return toDto(policy);
    }

    @Transactional
    public AutopilotPolicyDto updatePolicy(String nodeId, AutopilotPolicyDto dto) {
        if (!nodeRepository.existsById(nodeId)) {
            throw new ResourceNotFoundException("NODE_NOT_FOUND", "Node not found: " + nodeId);
        }

        AutopilotPolicy policy = policyRepository.findById(nodeId).orElse(new AutopilotPolicy());
        policy.nodeId = nodeId;
        policy.enabled = dto.enabled != null ? dto.enabled : false;
        policy.confidenceThreshold = dto.confidenceThreshold != null ? dto.confidenceThreshold : 0.85;
        policy.deterrenceTimeoutSeconds = dto.deterrenceTimeoutSeconds != null ? dto.deterrenceTimeoutSeconds : 5;
        policy.autoEscalateOnDeterrenceFailure = dto.autoEscalateOnDeterrenceFailure != null ? dto.autoEscalateOnDeterrenceFailure : true;
        policy.maxDeterrenceAttempts = dto.maxDeterrenceAttempts != null ? dto.maxDeterrenceAttempts : 1;
        policy.requireMinimumConfidence = dto.requireMinimumConfidence != null ? dto.requireMinimumConfidence : true;
        policy.requireMultiModalConfirmation = dto.requireMultiModalConfirmation != null ? dto.requireMultiModalConfirmation : false;
        policy.allowedThreatTypes = toJson(dto.allowedThreatTypes);
        policy.alwaysEscalateTypes = toJson(dto.alwaysEscalateTypes);
        policy.neverAutonomousTypes = toJson(dto.neverAutonomousTypes);
        policy.syncState = AutopilotPolicy.SyncState.PENDING;

        policyRepository.save(policy);
        return toDto(policy);
    }

    private AutopilotPolicyDto toDto(AutopilotPolicy p) {
        AutopilotPolicyDto dto = new AutopilotPolicyDto();
        dto.nodeId = p.nodeId;
        dto.enabled = p.enabled;
        dto.confidenceThreshold = p.confidenceThreshold;
        dto.deterrenceTimeoutSeconds = p.deterrenceTimeoutSeconds;
        dto.autoEscalateOnDeterrenceFailure = p.autoEscalateOnDeterrenceFailure;
        dto.maxDeterrenceAttempts = p.maxDeterrenceAttempts;
        dto.requireMinimumConfidence = p.requireMinimumConfidence;
        dto.requireMultiModalConfirmation = p.requireMultiModalConfirmation;
        dto.alwaysEscalateTypes = parseJson(p.alwaysEscalateTypes);
        dto.neverAutonomousTypes = parseJson(p.neverAutonomousTypes);
        dto.allowedThreatTypes = parseJson(p.allowedThreatTypes);
        dto.syncState = p.syncState != null ? p.syncState.name() : "DISABLED";
        dto.lastSync = p.lastSync;
        return dto;
    }

    private List<String> parseJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try { return objectMapper.readValue(json, new TypeReference<List<String>>() {}); }
        catch (Exception e) { return List.of(); }
    }

    private String toJson(List<String> list) {
        try { return objectMapper.writeValueAsString(list != null ? list : List.of()); }
        catch (Exception e) { return "[]"; }
    }
}