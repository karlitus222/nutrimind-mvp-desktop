package br.com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationReport {

    private int id;
    private Consultation consultation;
    private String summary;
    private String dietPlan;
    private String recommendations;
    private boolean aiAssisted;
    private String aiSummary;
    private List<String> aiAlerts = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConsultationReport() {
        this.aiAssisted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ConsultationReport(Consultation consultation, String summary,
                               String dietPlan, String recommendations) {
        this();
        this.consultation = consultation;
        this.summary = summary;
        this.dietPlan = dietPlan;
        this.recommendations = recommendations;
    }

    public ConsultationReport(int id, Consultation consultation, String summary,
                               String dietPlan, String recommendations,
                               boolean aiAssisted, String aiSummary,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.consultation = consultation;
        this.summary = summary;
        this.dietPlan = dietPlan;
        this.recommendations = recommendations;
        this.aiAssisted = aiAssisted;
        this.aiSummary = aiSummary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDietPlan() { return dietPlan; }
    public void setDietPlan(String dietPlan) { this.dietPlan = dietPlan; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public boolean isAiAssisted() { return aiAssisted; }
    public void setAiAssisted(boolean aiAssisted) { this.aiAssisted = aiAssisted; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public List<String> getAiAlerts() { return aiAlerts; }
    public void setAiAlerts(List<String> aiAlerts) { this.aiAlerts = aiAlerts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
