package com.sip.guardian.data.repository;

import com.google.gson.Gson;
import com.sip.guardian.data.local.dao.NodeDao;
import com.sip.guardian.data.local.entity.NodeEntity;
import com.sip.guardian.data.remote.api.SipApiService;
import com.sip.guardian.data.remote.dto.NodeDto;
import com.sip.guardian.domain.model.AutopilotPolicy;
import com.sip.guardian.domain.model.Location;
import com.sip.guardian.domain.model.Node;
import com.sip.guardian.domain.model.NodeStatus;
import com.sip.guardian.domain.repository.NodeRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NodeRepositoryImpl implements NodeRepository {

    private final SipApiService api;
    private final NodeDao dao;
    private final Gson gson = new Gson();

    @Inject
    public NodeRepositoryImpl(SipApiService api, NodeDao dao) {
        this.api = api;
        this.dao = dao;
    }

    @Override
    public List<Node> getNodes() {
        try {
            var response = api.getNodes().execute();
            if (response.isSuccessful() && response.body() != null) {
                List<NodeEntity> entities = new ArrayList<>();
                List<Node> out = new ArrayList<>();
                for (NodeDto dto : response.body()) {
                    entities.add(toEntity(dto));
                    out.add(toDomain(dto));
                }
                dao.upsertAll(entities);
                return out;
            }
        } catch (IOException ignored) { }
        // offline fallback
        List<Node> cached = new ArrayList<>();
        for (NodeEntity e : dao.getAll()) cached.add(toDomain(e));
        return cached;
    }

    @Override
    public Node getNodeById(String nodeId) {
        for (Node n : getNodes()) {
            if (n.getNodeId().equals(nodeId)) return n;
        }
        return null;
    }

    private static NodeEntity toEntity(NodeDto dto) {
        NodeEntity e = new NodeEntity();
        e.nodeId = dto.nodeId;
        e.name = dto.name;
        e.status = dto.status;
        e.latitude = dto.latitude;
        e.longitude = dto.longitude;
        e.lastHeartbeatEpochMs = dto.lastHeartbeat != null
                ? Instant.parse(dto.lastHeartbeat).toEpochMilli() : 0;
        e.batteryLevel = dto.batteryLevel;
        e.firmwareVersion = dto.firmwareVersion;
        e.policyJson = dto.autopilotPolicy != null
                ? new Gson().toJson(dto.autopilotPolicy) : null;
        return e;
    }

    private Node toDomain(NodeDto dto) {
        AutopilotPolicy policy = AutopilotPolicy.disabled();
        return new Node(dto.nodeId, dto.name,
                new Location(dto.latitude, dto.longitude, "", 0),
                dto.status != null ? NodeStatus.valueOf(dto.status) : NodeStatus.UNKNOWN,
                dto.lastHeartbeat != null ? Instant.parse(dto.lastHeartbeat) : null,
                dto.batteryLevel, dto.firmwareVersion, policy);
    }

    private Node toDomain(NodeEntity e) {
        return new Node(e.nodeId, e.name,
                new Location(e.latitude, e.longitude, "", 0),
                e.status != null ? NodeStatus.valueOf(e.status) : NodeStatus.UNKNOWN,
                e.lastHeartbeatEpochMs > 0
                        ? Instant.ofEpochMilli(e.lastHeartbeatEpochMs) : null,
                e.batteryLevel, e.firmwareVersion, AutopilotPolicy.disabled());
    }
}
