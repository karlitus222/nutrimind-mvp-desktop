package br.com.nutrimind.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealPlan {
    private long id;
    private long patientId;
    private long consultationId;
    private String objective;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long approvedBy;
    private LocalDateTime approvedAt;

    public MealPlan(long id, long patientId, long consultationId, String objective, String description, String status,
                    LocalDate startDate, LocalDate endDate, Long approvedBy, LocalDateTime approvedAt) {
        this.id = id;
        this.patientId = patientId;
        this.consultationId = consultationId;
        this.objective = objective;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public long getConsultationId() {
        return consultationId;
    }

    public String getObjective() {
        return objective;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
