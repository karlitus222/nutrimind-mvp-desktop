package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class Alert {
    private long id;
    private long consultationId;
    private long analysisId;
    private String riskType;
    private Severity severity;
    private String message;
    private String justification;
    private String status;
    private LocalDateTime createdAt;

    public Alert(long id, long consultationId, long analysisId, String riskType, Severity severity,
                 String message, String justification, String status, LocalDateTime createdAt) {
        this.id = id;
        this.consultationId = consultationId;
        this.analysisId = analysisId;
        this.riskType = riskType;
        this.severity = severity;
        this.message = message;
        this.justification = justification;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public long getAnalysisId() {
        return analysisId;
    }

    public String getRiskType() {
        return riskType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getJustification() {
        return justification;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

