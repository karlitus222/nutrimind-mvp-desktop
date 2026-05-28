package br.com.nutrimind;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.config.DatabaseInitializer;
import br.com.nutrimind.dao.AuditLogDao;
import br.com.nutrimind.dao.PatientDao;
import br.com.nutrimind.dao.UserDao;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.User;
import br.com.nutrimind.service.AuthService;
import br.com.nutrimind.service.LocalRiskHeuristics;
import br.com.nutrimind.service.OpenAiClient;

import java.util.List;

public class SmokeTest {
    public static void main(String[] args) {
        new DatabaseInitializer().initialize();
        testLogin();
        testPatients();
        testLocalHeuristics();
        testAiConfigurationGate();
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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

