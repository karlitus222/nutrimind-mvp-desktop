package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class RiskAnalysis {
    private long id;
    private long consultationId;
    private String provider;
    private String model;
    private String rawJson;
    private String summary;
    private LocalDateTime createdAt;

    public RiskAnalysis(long id, long consultationId, String provider, String model, String rawJson,
                        String summary, LocalDateTime createdAt) {
        this.id = id;
        this.consultationId = consultationId;
        this.provider = provider;
        this.model = model;
        this.rawJson = rawJson;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public String getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public String getRawJson() {
        return rawJson;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

