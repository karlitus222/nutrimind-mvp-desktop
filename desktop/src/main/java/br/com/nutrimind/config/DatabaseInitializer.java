package br.com.nutrimind.config;

import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.util.PasswordUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatabaseInitializer {
    public void initialize() {
        try {
            Files.createDirectories(AppConfig.DATA_DIR);
            Files.createDirectories(AppConfig.MEDIA_DIR);
            Files.createDirectories(AppConfig.EXPORT_DIR);
            executeSchema();
            seedDemoData();
        } catch (IOException e) {
            throw new AppException("Não foi possível preparar diretórios de dados.", e);
        }
    }

    private void executeSchema() {
        Path schema = AppConfig.DESKTOP_DIR.resolve("sql").resolve("schema.sql");
        try (Connection connection = Database.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            String sql = Files.readString(schema);
            for (String command : sql.split(";")) {
                if (!command.isBlank()) {
                    statement.execute(command);
                }
            }
        } catch (IOException | SQLException e) {
            throw new AppException("Falha ao criar schema do banco.", e);
        }
    }

    private void seedDemoData() {
        try (Connection connection = Database.getInstance().getConnection()) {
            if (hasUsers(connection)) {
                return;
            }
            long nutritionistId = insertUser(connection, "Dra. Juliana Alves", "nutri@nutrimind.com",
                    "123456", "NUTRICIONISTA");
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO nutritionists(user_id, crn, specialty) VALUES (?, ?, ?)")) {
                ps.setLong(1, nutritionistId);
                ps.setString(2, "CRN 12345/P");
                ps.setString(3, "Nutrição comportamental");
                ps.executeUpdate();
            }
            insertUser(connection, "Admin Nutrimind", "admin@nutrimind.com", "admin123", "ADMIN");
            insertPatient(connection, nutritionistId, "Ana Maria Souza", "111.222.333-44", "1992-03-14",
                    "Ansiedade alimentar relatada; episódios de comer rápido à noite.",
                    "Rotina irregular, pula café da manhã e relata culpa após refeições.");
            insertPatient(connection, nutritionistId, "Rafael Costa", "222.333.444-55", "1997-08-21",
                    "Busca reeducação alimentar e controle de compulsão.",
                    "Alterna restrição durante o dia e excesso no período noturno.");
            insertPatient(connection, nutritionistId, "Julia Fernandes", "333.444.555-66", "1984-01-02",
                    "Acompanhamento preventivo.",
                    "Boa adesão, mas refere baixa energia em semanas de estresse.");
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO subscriptions(nutritionist_id, plan_name, status, last_payment_status, next_due_date) VALUES (?, ?, ?, ?, ?)")) {
                ps.setLong(1, nutritionistId);
                ps.setString(2, "Clínica IA");
                ps.setString(3, "ATIVA");
                ps.setString(4, "PAGO_SIMULADO");
                ps.setString(5, LocalDate.now().plusMonths(1).toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao inserir dados iniciais.", e);
        }
    }

    private boolean hasUsers(Connection connection) throws SQLException {
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private long insertUser(Connection connection, String name, String email, String password, String role) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users(name, email, password_hash, role, active, created_at) VALUES (?, ?, ?, ?, 1, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password.toCharArray()));
            ps.setString(4, role);
            ps.setString(5, LocalDateTime.now().toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void insertPatient(Connection connection, long nutritionistId, String name, String cpf, String birthDate,
                               String clinicalNotes, String eatingHistory) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO patients(nutritionist_id, name, cpf, birth_date, phone, email, clinical_notes, eating_history, active, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, ?)")) {
            ps.setLong(1, nutritionistId);
            ps.setString(2, name);
            ps.setString(3, cpf);
            ps.setString(4, birthDate);
            ps.setString(5, "(86) 99999-0000");
            ps.setString(6, name.toLowerCase().replace(" ", ".") + "@email.com");
            ps.setString(7, clinicalNotes);
            ps.setString(8, eatingHistory);
            ps.setString(9, LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }
}
