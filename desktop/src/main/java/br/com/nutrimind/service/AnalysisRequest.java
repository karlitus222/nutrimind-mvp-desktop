package br.com.nutrimind.service;

import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.Patient;

import java.util.List;

public class AnalysisRequest {
    private final Patient patient;
    private final Consultation consultation;
    private final List<Consultation> history;
    private final String transcript;
    private final String clinicalNotes;
    private final String visualObservations;

    public AnalysisRequest(Patient patient, Consultation consultation, List<Consultation> history,
                           String transcript, String clinicalNotes, String visualObservations) {
        this.patient = patient;
        this.consultation = consultation;
        this.history = history;
        this.transcript = transcript;
        this.clinicalNotes = clinicalNotes;
        this.visualObservations = visualObservations;
    }

    public Patient getPatient() {
        return patient;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public List<Consultation> getHistory() {
        return history;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public String getVisualObservations() {
        return visualObservations;
    }
}

