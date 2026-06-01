package br.com.nutrimind.controller;

import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.AnalysisDao;
import br.com.nutrimind.dao.AuditLogDao;
import br.com.nutrimind.dao.ConsultationDao;
import br.com.nutrimind.dao.MealPlanDao;
import br.com.nutrimind.dao.MediaSessionDao;
import br.com.nutrimind.dao.PatientDao;
import br.com.nutrimind.dao.ReportDao;
import br.com.nutrimind.dao.StatsDao;
import br.com.nutrimind.dao.UserDao;
import br.com.nutrimind.model.User;
import br.com.nutrimind.service.AiAnalysisService;
import br.com.nutrimind.service.AudioRecorderService;
import br.com.nutrimind.service.AuthService;
import br.com.nutrimind.service.ConsultationWorkflowService;
import br.com.nutrimind.service.JsonExportService;
import br.com.nutrimind.service.LocalRiskHeuristics;
import br.com.nutrimind.service.OpenAiClient;
import br.com.nutrimind.service.OpenAiRiskAnalysisService;
import br.com.nutrimind.service.OpenAiTranscriptionService;

public class AppController {
    private final UserDao userDao = new UserDao();
    private final PatientDao patientDao = new PatientDao();
    private final ConsultationDao consultationDao = new ConsultationDao();
    private final MediaSessionDao mediaSessionDao = new MediaSessionDao();
    private final AnalysisDao analysisDao = new AnalysisDao();
    private final AlertDao alertDao = new AlertDao();
    private final ReportDao reportDao = new ReportDao();
    private final MealPlanDao mealPlanDao = new MealPlanDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();
    private final StatsDao statsDao = new StatsDao();
    private final OpenAiClient openAiClient = new OpenAiClient();
    private final OpenAiTranscriptionService transcriptionService = new OpenAiTranscriptionService(openAiClient);
    private final AiAnalysisService aiAnalysisService = new OpenAiRiskAnalysisService(openAiClient);
    private final LocalRiskHeuristics localRiskHeuristics = new LocalRiskHeuristics();
    private final AudioRecorderService audioRecorderService = new AudioRecorderService();

    private User currentUser;

    public AuthService authService() {
        return new AuthService(userDao, auditLogDao);
    }

    public PatientController patientController() {
        return new PatientController(patientDao);
    }

    public ConsultationController consultationController() {
        ConsultationWorkflowService workflow = new ConsultationWorkflowService(consultationDao, mediaSessionDao,
                analysisDao, alertDao, reportDao, mealPlanDao, auditLogDao, openAiClient::ensureConfigured,
                transcriptionService, aiAnalysisService, localRiskHeuristics);
        return new ConsultationController(workflow, consultationDao, alertDao, reportDao, audioRecorderService);
    }

    public MealPlanController mealPlanController() {
        return new MealPlanController(mealPlanDao);
    }

    public AdminController adminController() {
        JsonExportService exportService = new JsonExportService(patientDao, consultationDao, alertDao, analysisDao,
                reportDao, mealPlanDao, statsDao);
        return new AdminController(userDao, alertDao, statsDao, exportService);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
