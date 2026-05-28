package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class MediaSession {
    private long id;
    private long consultationId;
    private String type;
    private String filePath;
    private String quality;
    private int durationSeconds;
    private String status;
    private LocalDateTime createdAt;

    public MediaSession(long id, long consultationId, String type, String filePath, String quality,
                        int durationSeconds, String status, LocalDateTime createdAt) {
        this.id = id;
        this.consultationId = consultationId;
        this.type = type;
        this.filePath = filePath;
        this.quality = quality;
        this.durationSeconds = durationSeconds;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public String getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getQuality() {
        return quality;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

