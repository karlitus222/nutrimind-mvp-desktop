package br.com.nutrimind.model;

import java.time.LocalDateTime;

public class Consultation {

    private long id;
    private long patientId;
    private long nutritionistId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private ConsultationStatus status;
    private boolean consentAudio;
    private boolean consentVideo;
    private String clinicalNotes;
    private String transcript;
    private String visualObservations;

    public Consultation() {
        this.status = ConsultationStatus.EM_ANDAMENTO;
    }

    public Consultation(long patientId, long nutritionistId, LocalDateTime startedAt) {
        this();
        this.patientId = patientId;
        this.nutritionistId = nutritionistId;
        this.startedAt = startedAt;
    }

    public Consultation(long id, long patientId, long nutritionistId,
                        LocalDateTime startedAt, LocalDateTime endedAt,
                        ConsultationStatus status, boolean consentAudio, boolean consentVideo,
                        String clinicalNotes, String transcript, String visualObservations) {
        this.id = id;
        this.patientId = patientId;
        this.nutritionistId = nutritionistId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
        this.consentAudio = consentAudio;
        this.consentVideo = consentVideo;
        this.clinicalNotes = clinicalNotes;
        this.transcript = transcript;
        this.visualObservations = visualObservations;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPatientId() { return patientId; }
    public void setPatientId(long patientId) { this.patientId = patientId; }

    public long getNutritionistId() { return nutritionistId; }
    public void setNutritionistId(long nutritionistId) { this.nutritionistId = nutritionistId; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public ConsultationStatus getStatus() { return status; }
    public void setStatus(ConsultationStatus status) { this.status = status; }

    public boolean hasConsentAudio() { return consentAudio; }
    public void setConsentAudio(boolean consentAudio) { this.consentAudio = consentAudio; }

    public boolean hasConsentVideo() { return consentVideo; }
    public void setConsentVideo(boolean consentVideo) { this.consentVideo = consentVideo; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getVisualObservations() { return visualObservations; }
    public void setVisualObservations(String visualObservations) { this.visualObservations = visualObservations; }

    @Override
    public String toString() {
        return "Consulta #" + id + " - " + status;
    }
}
