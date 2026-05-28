package br.com.nutrimind.service;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.AnalysisDao;
import br.com.nutrimind.dao.ConsultationDao;
import br.com.nutrimind.dao.MealPlanDao;
import br.com.nutrimind.dao.PatientDao;
import br.com.nutrimind.dao.ReportDao;
import br.com.nutrimind.dao.StatsDao;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.model.RiskAnalysis;
import br.com.nutrimind.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class JsonExportService {
    private final PatientDao patientDao;
    private final ConsultationDao consultationDao;
    private final AlertDao alertDao;
    private final AnalysisDao analysisDao;
    private final ReportDao reportDao;
    private final MealPlanDao mealPlanDao;
    private final StatsDao statsDao;

    public JsonExportService(PatientDao patientDao, ConsultationDao consultationDao, AlertDao alertDao,
                             AnalysisDao analysisDao, ReportDao reportDao, MealPlanDao mealPlanDao, StatsDao statsDao) {
        this.patientDao = patientDao;
        this.consultationDao = consultationDao;
        this.alertDao = alertDao;
        this.analysisDao = analysisDao;
        this.reportDao = reportDao;
        this.mealPlanDao = mealPlanDao;
        this.statsDao = statsDao;
    }

    public Path export() {
        try {
            Files.createDirectories(AppConfig.EXPORT_DIR);
            Path file = AppConfig.EXPORT_DIR.resolve("nutrimind-demo-export.json");
            Files.writeString(file, buildJson());
            return file;
        } catch (IOException e) {
            throw new AppException("Falha ao exportar dados JSON.", e);
        }
    }

    private String buildJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": ").append(JsonUtil.quote(LocalDateTime.now().toString())).append(",\n");
        json.append("  \"stats\": ").append(statsJson()).append(",\n");
        json.append("  \"patients\": [\n");
        List<Patient> patients = patientDao.findAll();
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            json.append(patientJson(patient));
            if (i < patients.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private String statsJson() {
        StringBuilder json = new StringBuilder("{");
        int index = 0;
        for (Map.Entry<String, Integer> entry : statsDao.dashboardCounts().entrySet()) {
            json.append(JsonUtil.quote(entry.getKey())).append(":").append(entry.getValue());
            if (index < statsDao.dashboardCounts().size() - 1) {
                json.append(",");
            }
            index++;
        }
        json.append("}");
        return json.toString();
    }

    private String patientJson(Patient patient) {
        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"id\": ").append(patient.getId()).append(",\n");
        json.append("      \"name\": ").append(JsonUtil.quote(patient.getName())).append(",\n");
        json.append("      \"clinicalNotes\": ").append(JsonUtil.quote(patient.getClinicalNotes())).append(",\n");
        json.append("      \"eatingHistory\": ").append(JsonUtil.quote(patient.getEatingHistory())).append(",\n");
        json.append("      \"consultations\": [\n");
        List<Consultation> consultations = consultationDao.findByPatient(patient.getId());
        for (int i = 0; i < consultations.size(); i++) {
            json.append(consultationJson(consultations.get(i), patient));
            if (i < consultations.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("      ],\n");
        json.append("      \"plans\": [\n");
        List<MealPlan> plans = mealPlanDao.findByPatient(patient.getId());
        for (int i = 0; i < plans.size(); i++) {
            MealPlan plan = plans.get(i);
            json.append("        {\"objective\":").append(JsonUtil.quote(plan.getObjective()))
                    .append(",\"status\":").append(JsonUtil.quote(plan.getStatus()))
                    .append(",\"description\":").append(JsonUtil.quote(plan.getDescription())).append("}");
            if (i < plans.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("      ]\n");
        json.append("    }");
        return json.toString();
    }

    private String consultationJson(Consultation consultation, Patient patient) {
        RiskAnalysis analysis = analysisDao.findLatestByConsultation(consultation.getId()).orElse(null);
        ConsultationReport report = reportDao.findLatestByConsultation(consultation.getId()).orElse(null);
        List<Alert> alerts = alertDao.findByConsultation(consultation.getId());
        StringBuilder json = new StringBuilder();
        json.append("        {\n");
        json.append("          \"id\": ").append(consultation.getId()).append(",\n");
        json.append("          \"patientName\": ").append(JsonUtil.quote(patient.getName())).append(",\n");
        json.append("          \"date\": ").append(JsonUtil.quote(consultation.getStartedAt().toLocalDate().toString())).append(",\n");
        json.append("          \"summary\": ").append(JsonUtil.quote(analysis == null ? "Aguardando análise de IA." : analysis.getSummary())).append(",\n");
        json.append("          \"alerts\": [");
        for (int i = 0; i < alerts.size(); i++) {
            Alert alert = alerts.get(i);
            json.append("{\"type\":").append(JsonUtil.quote(alert.getRiskType()))
                    .append(",\"severity\":").append(JsonUtil.quote(alert.getSeverity().name()))
                    .append(",\"message\":").append(JsonUtil.quote(alert.getMessage())).append("}");
            if (i < alerts.size() - 1) {
                json.append(",");
            }
        }
        json.append("],\n");
        json.append("          \"report\": ").append(JsonUtil.quote(report == null ? "" :
                report.getIdentificationSection() + "\n\n" + report.getClinicalSection() + "\n\n" +
                        report.getRecommendationsSection() + "\n\n" + report.getLimitationsSection())).append("\n");
        json.append("        }");
        return json.toString();
    }
}
