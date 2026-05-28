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

    public Consultation(long id, long patientId, long nutritionistId, LocalDateTime startedAt, LocalDateTime endedAt,
                        ConsultationStatus status, boolean consentAudio, boolean consentVideo, String clinicalNotes,
                        String transcript, String visualObservations) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public long getNutritionistId() {
        return nutritionistId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public boolean hasConsentAudio() {
        return consentAudio;
    }

    public boolean hasConsentVideo() {
        return consentVideo;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getVisualObservations() {
        return visualObservations;
    }
}

