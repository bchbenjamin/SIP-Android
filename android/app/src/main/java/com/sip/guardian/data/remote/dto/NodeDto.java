package com.sip.guardian.data.remote.dto;

public class NodeDto {
    public String nodeId;
    public String name;
    public String status;
    public double latitude;
    public double longitude;
    public String lastHeartbeat;
    public Integer batteryLevel;
    public String firmwareVersion;
    public AutopilotPolicyDto autopilotPolicy;
}
