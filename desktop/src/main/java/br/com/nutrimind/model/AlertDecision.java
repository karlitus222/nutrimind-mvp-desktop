package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class AlertDecision {
    private long id;
    private long alertId;
    private String action;
    private String notes;
    private LocalDateTime decidedAt;

    public AlertDecision(long id, long alertId, String action, String notes, LocalDateTime decidedAt) {
        this.id = id;
        this.alertId = alertId;
        this.action = action;
        this.notes = notes;
        this.decidedAt = decidedAt;
    }

    public long getId() {
        return id;
    }

    public long getAlertId() {
        return alertId;
    }

    public String getAction() {
        return action;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}

