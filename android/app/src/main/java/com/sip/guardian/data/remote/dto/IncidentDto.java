package com.sip.guardian.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Wire format for an incident (plan §13). */
public class IncidentDto {
    public String id;
    public String state;
    public ThreatDto threat;
    public LocationDto location;
    public EvidenceDto evidence;
    public DetectionDto detection;
    public List<AnnotationDto> annotations;
    public List<ResponseEventDto> responseEvents;
    public String nodeId;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("updated_at") public String updatedAt;
    public boolean autopilotHandled;

    public static class ThreatDto {
        public String type;
        public String severity;
        public String description;
    }

    public static class LocationDto {
        public double latitude;
        public double longitude;
        public String humanReadable;
        public double accuracy;
    }

    public static class EvidenceDto {
        public String imageUrl;
        public String videoUrl;
        public String audioUrl;
        public String thumbnailUrl;
        public String captureTimestamp;
        public String retentionExpiry;
        public boolean pendingUpload;
    }

    public static class DetectionDto {
        public String predictedClass;
        public double confidence;
        public String modelVersion;
        public String detectionTimestamp;
        public List<String> sensorModalities;
    }

    public static class AnnotationDto {
        public String id;
        public String label;
        public String annotatorId;
        public String timestamp;
        public String notes;
        public Double confidence;
        public int version;
    }

    public static class ResponseEventDto {
        public String id;
        public String actionType;
        public String timestamp;
        public String result;
        public boolean autonomous;
    }
}
