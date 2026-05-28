package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class Consultation {

    private int id;
    private Patient patient;
    private Nutritionist nutritionist;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private ConsultationStatus status;
    private String anamnesis;
    private String nutritionistNotes;
    private double weightKg;
    private double heightCm;
    private ConsultationReport report;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Consultation() {
        this.status = ConsultationStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Consultation(Patient patient, Nutritionist nutritionist, LocalDateTime scheduledAt) {
        this();
        this.patient = patient;
        this.nutritionist = nutritionist;
        this.scheduledAt = scheduledAt;
    }

    public Consultation(int id, Patient patient, Nutritionist nutritionist,
                        LocalDateTime scheduledAt, LocalDateTime completedAt,
                        ConsultationStatus status, String anamnesis,
                        String nutritionistNotes, double weightKg, double heightCm,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patient = patient;
        this.nutritionist = nutritionist;
        this.scheduledAt = scheduledAt;
        this.completedAt = completedAt;
        this.status = status;
        this.anamnesis = anamnesis;
        this.nutritionistNotes = nutritionistNotes;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public double calcularImc() {
        if (heightCm <= 0 || weightKg <= 0) return 0;
        double alturaEmMetros = heightCm / 100.0;
        return weightKg / (alturaEmMetros * alturaEmMetros);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Nutritionist getNutritionist() { return nutritionist; }
    public void setNutritionist(Nutritionist nutritionist) { this.nutritionist = nutritionist; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public ConsultationStatus getStatus() { return status; }
    public void setStatus(ConsultationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAnamnesis() { return anamnesis; }
    public void setAnamnesis(String anamnesis) { this.anamnesis = anamnesis; }

    public String getNutritionistNotes() { return nutritionistNotes; }
    public void setNutritionistNotes(String nutritionistNotes) { this.nutritionistNotes = nutritionistNotes; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public ConsultationReport getReport() { return report; }
    public void setReport(ConsultationReport report) { this.report = report; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        String nomePaciente = patient != null ? patient.getName() : "sem paciente";
        return "Consulta de " + nomePaciente + " - " + status;
    }
}
