package br.com.nutrimind.controller;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.ConsultationDao;
import br.com.nutrimind.dao.ReportDao;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.model.User;
import br.com.nutrimind.service.AudioRecorderService;
import br.com.nutrimind.service.ConsultationResult;
import br.com.nutrimind.service.ConsultationWorkflowService;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ConsultationController {
    private final ConsultationWorkflowService workflowService;
    private final ConsultationDao consultationDao;
    private final AlertDao alertDao;
    private final ReportDao reportDao;
    private final AudioRecorderService audioRecorderService;

    public ConsultationController(ConsultationWorkflowService workflowService, ConsultationDao consultationDao,
                                  AlertDao alertDao, ReportDao reportDao, AudioRecorderService audioRecorderService) {
        this.workflowService = workflowService;
        this.consultationDao = consultationDao;
        this.alertDao = alertDao;
        this.reportDao = reportDao;
        this.audioRecorderService = audioRecorderService;
    }

    public Path startAudioRecording() {
        Path file = AppConfig.MEDIA_DIR.resolve("consulta-" + LocalDateTime.now().toString().replace(":", "-") + ".wav");
        audioRecorderService.start(file);
        return file;
    }

    public AudioRecorderService.RecordedAudio stopAudioRecording() {
        return audioRecorderService.stop();
    }

    public boolean isRecording() {
        return audioRecorderService.isRecording();
    }

    public ConsultationResult run(Patient patient, User nutritionist, boolean consentAudio, boolean consentVideo,
                                  String clinicalNotes, String manualTranscript, String visualObservations,
                                  Path audioPath, Integer audioDuration, Path videoPath) {
        return workflowService.runConsultation(patient, nutritionist, consentAudio, consentVideo, clinicalNotes,
                manualTranscript, visualObservations, audioPath, audioDuration, videoPath);
    }

    public List<Consultation> history(long patientId) {
        return consultationDao.findByPatient(patientId);
    }

    public List<Alert> alerts(long consultationId) {
        return alertDao.findByConsultation(consultationId);
    }

    public Optional<ConsultationReport> report(long consultationId) {
        return reportDao.findLatestByConsultation(consultationId);
    }
}

