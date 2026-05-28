package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.AlertDecision;
import br.com.nutrimind.model.Severity;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertDao {
    public Alert save(Alert alert) {
        String sql = """
                INSERT INTO alerts(consultation_id, analysis_id, risk_type, severity, message, justification, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, alert.getConsultationId());
            ps.setLong(2, alert.getAnalysisId());
            ps.setString(3, alert.getRiskType());
            ps.setString(4, alert.getSeverity().name());
            ps.setString(5, alert.getMessage());
            ps.setString(6, alert.getJustification());
            ps.setString(7, alert.getStatus());
            ps.setString(8, DateUtil.dateTime(alert.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Alert(keys.getLong(1), alert.getConsultationId(), alert.getAnalysisId(), alert.getRiskType(),
                        alert.getSeverity(), alert.getMessage(), alert.getJustification(), alert.getStatus(), alert.getCreatedAt());
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar alerta.", e);
        }
    }

    public List<Alert> findByConsultation(long consultationId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM alerts WHERE consultation_id = ? ORDER BY created_at DESC")) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Alert> alerts = new ArrayList<>();
                while (rs.next()) {
                    alerts.add(mapAlert(rs));
                }
                return alerts;
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao listar alertas.", e);
        }
    }

    public List<Alert> findOpenAlerts() {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM alerts WHERE status <> 'DECIDIDO' ORDER BY created_at DESC");
             ResultSet rs = ps.executeQuery()) {
            List<Alert> alerts = new ArrayList<>();
            while (rs.next()) {
                alerts.add(mapAlert(rs));
            }
            return alerts;
        } catch (SQLException e) {
            throw new AppException("Falha ao listar alertas abertos.", e);
        }
    }

    public AlertDecision decide(long alertId, String action, String notes) {
        try (Connection connection = Database.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            AlertDecision decision;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO alert_decisions(alert_id, action, notes, decided_at) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                LocalDateTime now = LocalDateTime.now();
                ps.setLong(1, alertId);
                ps.setString(2, action);
                ps.setString(3, notes);
                ps.setString(4, now.toString());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    decision = new AlertDecision(keys.getLong(1), alertId, action, notes, now);
                }
            }
            try (PreparedStatement ps = connection.prepareStatement("UPDATE alerts SET status = 'DECIDIDO' WHERE id = ?")) {
                ps.setLong(1, alertId);
                ps.executeUpdate();
            }
            connection.commit();
            return decision;
        } catch (SQLException e) {
            throw new AppException("Falha ao registrar decisão do alerta.", e);
        }
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        return new Alert(
                rs.getLong("id"),
                rs.getLong("consultation_id"),
                rs.getLong("analysis_id"),
                rs.getString("risk_type"),
                Severity.valueOf(rs.getString("severity")),
                rs.getString("message"),
                rs.getString("justification"),
                rs.getString("status"),
                DateUtil.parseDateTime(rs.getString("created_at"))
        );
    }
}

