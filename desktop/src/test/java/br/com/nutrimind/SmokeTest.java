package br.com.nutrimind;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.config.DatabaseInitializer;
import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.AnalysisDao;
import br.com.nutrimind.dao.AuditLogDao;
import br.com.nutrimind.dao.ConsultationDao;
import br.com.nutrimind.dao.MealPlanDao;
import br.com.nutrimind.dao.MediaSessionDao;
import br.com.nutrimind.dao.PatientDao;
import br.com.nutrimind.dao.ReportDao;
import br.com.nutrimind.dao.UserDao;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.AiAnalysisResult;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.model.Role;
import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.Severity;
import br.com.nutrimind.model.User;
import br.com.nutrimind.service.AuthService;
import br.com.nutrimind.service.ConsultationResult;
import br.com.nutrimind.service.ConsultationWorkflowService;
import br.com.nutrimind.service.LocalRiskHeuristics;
import br.com.nutrimind.service.OpenAiClient;
import br.com.nutrimind.service.RoleAccessPolicy;

import java.util.List;

public class SmokeTest {
    public static void main(String[] args) {
        new DatabaseInitializer().initialize();
        testLogin();
        testPatients();
        testLocalHeuristics();
        testAiConfigurationGate();
        testRoleAccessPolicy();
        testConsultationWorkflow();
        testAdminUserDeactivation();
        System.out.println("SmokeTest OK");
    }

    private static void testLogin() {
        AuthService auth = new AuthService(new UserDao(), new AuditLogDao());
        User user = auth.login("nutri@nutrimind.com", "123456".toCharArray());
        assertTrue(user.getId() > 0, "login deve retornar usuário persistido");
    }

    private static void testPatients() {
        List<Patient> patients = new PatientDao().findAll();
        assertTrue(patients.size() >= 3, "seed deve conter pacientes de demonstração");
    }

    private static void testLocalHeuristics() {
        List<RiskItem> risks = new LocalRiskHeuristics().validate(
                "Tenho ansiedade, sinto culpa depois de comer e às vezes pulo refeições.");
        assertTrue(!risks.isEmpty(), "heurística auxiliar deve detectar riscos conhecidos");
    }

    private static void testAiConfigurationGate() {
        if (AppConfig.hasOpenAiKey()) {
            return;
        }
        try {
            new OpenAiClient().ensureConfigured();
            throw new AssertionError("IA sem chave deveria ser bloqueada");
        } catch (AppException expected) {
            assertTrue(expected.getMessage().contains("OPENAI_API_KEY"), "mensagem deve orientar configuração da chave");
        }
    }

    private static void testRoleAccessPolicy() {
        RoleAccessPolicy policy = new RoleAccessPolicy();
        assertTrue(policy.canAccessClinicalWorkspace(Role.NUTRICIONISTA),
                "nutricionista deve acessar area clinica");
        assertTrue(!policy.canAccessAdministration(Role.NUTRICIONISTA),
                "nutricionista nao deve acessar administracao");
        assertTrue(policy.canAccessAdministration(Role.ADMIN),
                "admin deve acessar administracao");
        assertTrue(!policy.canAccessClinicalWorkspace(Role.ADMIN),
                "admin nao deve acessar area clinica");
    }

    private static void testConsultationWorkflow() {
        User nutritionist = new AuthService(new UserDao(), new AuditLogDao())
                .login("nutri@nutrimind.com", "123456".toCharArray());
        Patient patient = new PatientDao().findAll().get(0);
        ConsultationDao consultationDao = new ConsultationDao();
        AlertDao alertDao = new AlertDao();
        MealPlanDao mealPlanDao = new MealPlanDao();
        ConsultationWorkflowService workflow = new ConsultationWorkflowService(
                consultationDao,
                new MediaSessionDao(),
                new AnalysisDao(),
                alertDao,
                new ReportDao(),
                mealPlanDao,
                new AuditLogDao(),
                () -> {
                },
                audioFile -> "Transcricao simulada.",
                request -> new AiAnalysisResult(
                        "TESTE",
                        "fake-model",
                        "{}",
                        "Resumo gerado pela IA falsa.",
                        "Recomendacoes para revisao profissional.",
                        "Plano alimentar inicial para revisao.",
                        List.of(new RiskItem(
                                "Ansiedade alimentar",
                                Severity.MODERADO,
                                "Relato de ansiedade associado a alimentacao.",
                                "Teste estruturado do fluxo."
                        ))
                ),
                new LocalRiskHeuristics()
        );

        ConsultationResult result = workflow.runConsultation(
                patient,
                nutritionist,
                false,
                false,
                "Paciente relata ansiedade alimentar.",
                "Sinto culpa depois de comer.",
                "Paciente demonstra desconforto ao relatar rotina.",
                null,
                null,
                null
        );

        assertTrue(result.getConsultation().getId() > 0, "consulta deve ser persistida");
        assertTrue(result.getAnalysis().getId() > 0, "analise deve ser persistida");
        assertTrue(!result.getAlerts().isEmpty(), "consulta deve gerar alerta");
        assertTrue(result.getReport().getConsultationId() == result.getConsultation().getId(),
                "relatorio deve pertencer a consulta");
        assertTrue(result.getMealPlan().getId() > 0, "plano deve receber id persistido");

        mealPlanDao.approve(result.getMealPlan().getId(), nutritionist.getId());
        MealPlan approvedPlan = mealPlanDao.findByPatient(patient.getId()).stream()
                .filter(plan -> plan.getId() == result.getMealPlan().getId())
                .findFirst()
                .orElseThrow();
        assertTrue("APROVADO".equals(approvedPlan.getStatus()), "plano deve ser aprovado pelo nutricionista");
        assertTrue(approvedPlan.getApprovedBy() != null && approvedPlan.getApprovedBy() == nutritionist.getId(),
                "plano deve registrar nutricionista responsavel");

        Alert alert = result.getAlerts().get(0);
        alertDao.decide(alert.getId(), "MONITORAR", "Acompanhar na proxima consulta.");
        Alert decidedAlert = alertDao.findByConsultation(result.getConsultation().getId()).stream()
                .filter(item -> item.getId() == alert.getId())
                .findFirst()
                .orElseThrow();
        assertTrue("DECIDIDO".equals(decidedAlert.getStatus()), "alerta deve registrar decisao clinica");
    }

    private static void testAdminUserDeactivation() {
        UserDao userDao = new UserDao();
        String email = "smoke." + System.nanoTime() + "@nutrimind.test";
        User created = userDao.createNutritionist(
                "Nutricionista Smoke",
                email,
                "senha123".toCharArray(),
                "CRN TESTE",
                "Teste automatizado"
        );
        assertTrue(created.isActive(), "usuario criado deve iniciar ativo");

        new AppController().adminController().deactivateUser(created.getId());

        User deactivated = userDao.findById(created.getId()).orElseThrow();
        assertTrue(!deactivated.isActive(), "admin deve conseguir desativar usuario");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
