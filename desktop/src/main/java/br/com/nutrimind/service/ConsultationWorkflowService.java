package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.AnalysisDao;
import br.com.nutrimind.dao.AuditLogDao;
import br.com.nutrimind.dao.ConsultationDao;
import br.com.nutrimind.dao.MealPlanDao;
import br.com.nutrimind.dao.MediaSessionDao;
import br.com.nutrimind.dao.ReportDao;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.AiAnalysisResult;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.ConsultationStatus;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.model.MediaSession;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.model.RiskAnalysis;
import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.User;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationWorkflowService {
    private final ConsultationDao consultationDao;
    private final MediaSessionDao mediaSessionDao;
    private final AnalysisDao analysisDao;
    private final AlertDao alertDao;
    private final ReportDao reportDao;
    private final MealPlanDao mealPlanDao;
    private final AuditLogDao auditLogDao;
    private final OpenAiTranscriptionService transcriptionService;
    private final AiAnalysisService analysisService;
    private final LocalRiskHeuristics localRiskHeuristics;

    public ConsultationWorkflowService(ConsultationDao consultationDao, MediaSessionDao mediaSessionDao,
                                       AnalysisDao analysisDao, AlertDao alertDao, ReportDao reportDao,
                                       MealPlanDao mealPlanDao, AuditLogDao auditLogDao,
                                       OpenAiTranscriptionService transcriptionService,
                                       AiAnalysisService analysisService, LocalRiskHeuristics localRiskHeuristics) {
        this.consultationDao = consultationDao;
        this.mediaSessionDao = mediaSessionDao;
        this.analysisDao = analysisDao;
        this.alertDao = alertDao;
        this.reportDao = reportDao;
        this.mealPlanDao = mealPlanDao;
        this.auditLogDao = auditLogDao;
        this.transcriptionService = transcriptionService;
        this.analysisService = analysisService;
        this.localRiskHeuristics = localRiskHeuristics;
    }

    public ConsultationResult runConsultation(Patient patient, User nutritionist, boolean consentAudio, boolean consentVideo,
                                              String clinicalNotes, String manualTranscript, String visualObservations,
                                              Path audioPath, Integer audioDuration, Path videoPath) {
        if (!AppConfig.hasOpenAiKey()) {
            throw new AppException("A análise é obrigatoriamente feita por IA. Configure OPENAI_API_KEY antes de encerrar a consulta.");
        }
        String transcript = manualTranscript == null ? "" : manualTranscript.trim();
        Consultation draft = new Consultation(0, patient.getId(), nutritionist.getId(), LocalDateTime.now(), null,
                ConsultationStatus.EM_ANDAMENTO, consentAudio, consentVideo, clinicalNotes, transcript, visualObservations);
        Consultation consultation = consultationDao.save(draft);

        if (consentAudio && audioPath != null) {
            transcript = mergeTranscript(transcript, transcriptionService.transcribe(audioPath));
            mediaSessionDao.save(new MediaSession(0, consultation.getId(), "AUDIO", audioPath.toString(), "16000Hz mono",
                    audioDuration == null ? 0 : audioDuration, "FINALIZADA", LocalDateTime.now()));
        }
        if (consentVideo && videoPath != null) {
            mediaSessionDao.save(new MediaSession(0, consultation.getId(), "VIDEO", videoPath.toString(), "arquivo vinculado",
                    0, "VINCULADA", LocalDateTime.now()));
        }

        Consultation completed = new Consultation(consultation.getId(), patient.getId(), nutritionist.getId(),
                consultation.getStartedAt(), LocalDateTime.now(), ConsultationStatus.ENCERRADA, consentAudio, consentVideo,
                clinicalNotes, transcript, visualObservations);
        consultationDao.save(completed);

        AiAnalysisResult ai = analysisService.analyze(new AnalysisRequest(patient, completed,
                consultationDao.findByPatient(patient.getId()), transcript, clinicalNotes, visualObservations));
        RiskAnalysis analysis = analysisDao.save(new RiskAnalysis(0, completed.getId(), ai.getProvider(), ai.getModel(),
                ai.getRawJson(), ai.getSummary(), LocalDateTime.now()));

        List<Alert> alerts = new ArrayList<>();
        for (RiskItem risk : ai.getRisks()) {
            alerts.add(alertDao.save(new Alert(0, completed.getId(), analysis.getId(), risk.getType(), risk.getSeverity(),
                    risk.getMessage(), risk.getJustification(), "ABERTO", LocalDateTime.now())));
        }
        for (RiskItem localRisk : localRiskHeuristics.validate(clinicalNotes + "\n" + transcript + "\n" + visualObservations)) {
            boolean exists = alerts.stream().anyMatch(alert -> alert.getRiskType().equalsIgnoreCase(localRisk.getType()));
            if (!exists) {
                alerts.add(alertDao.save(new Alert(0, completed.getId(), analysis.getId(), localRisk.getType(), localRisk.getSeverity(),
                        "Validação auxiliar: " + localRisk.getMessage(), localRisk.getJustification(), "ABERTO", LocalDateTime.now())));
            }
        }

        ConsultationReport report = reportDao.save(buildReport(completed, patient, ai));
        MealPlan plan = mealPlanDao.save(new MealPlan(0, patient.getId(), completed.getId(),
                "Plano inicial com revisão obrigatória", ai.getMealPlanSuggestion(), "EM_REVISAO",
                LocalDate.now(), LocalDate.now().plusDays(30), null, null));
        auditLogDao.log(nutritionist.getId(), "CONSULTA_IA_CONCLUIDA",
                "Consulta " + completed.getId() + " analisada com " + ai.getModel());
        return new ConsultationResult(completed, analysis, alerts, report, plan);
    }

    private ConsultationReport buildReport(Consultation consultation, Patient patient, AiAnalysisResult ai) {
        String identification = "Paciente: " + patient.getName() + "\nConsulta: " + consultation.getStartedAt() +
                "\nObjetivo: análise comportamental e nutricional assistida por IA.";
        String clinical = "Resumo da IA:\n" + ai.getSummary() + "\n\nAchados estruturados:\n" + risksText(ai.getRisks());
        String recommendations = "Recomendações iniciais para revisão profissional:\n" + ai.getRecommendations() +
                "\n\nSugestão de plano:\n" + ai.getMealPlanSuggestion();
        String limitations = "A IA apoia a decisão clínica, mas não substitui anamnese, avaliação presencial, diagnóstico ou prescrição do nutricionista.";
        return new ConsultationReport(0, consultation.getId(), identification, clinical, recommendations, limitations, LocalDateTime.now());
    }

    private String risksText(List<RiskItem> risks) {
        if (risks.isEmpty()) {
            return "Nenhum risco relevante foi destacado pela IA.";
        }
        StringBuilder builder = new StringBuilder();
        for (RiskItem risk : risks) {
            builder.append("- ").append(risk.getSeverity()).append(" | ").append(risk.getType())
                    .append(": ").append(risk.getMessage()).append("\n  Justificativa: ")
                    .append(risk.getJustification()).append("\n");
        }
        return builder.toString();
    }

    private String mergeTranscript(String manual, String fromAudio) {
        if (manual == null || manual.isBlank()) {
            return fromAudio == null ? "" : fromAudio;
        }
        if (fromAudio == null || fromAudio.isBlank()) {
            return manual;
        }
        return manual + "\n\nTranscrição automática:\n" + fromAudio;
    }
}

