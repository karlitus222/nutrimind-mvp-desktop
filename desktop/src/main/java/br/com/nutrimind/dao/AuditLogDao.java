package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AuditLogDao {
    public void log(Long userId, String action, String details) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO audit_logs(user_id, action, details, created_at) VALUES (?, ?, ?, ?)")) {
            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId);
            }
            ps.setString(2, action);
            ps.setString(3, details);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Falha ao registrar auditoria.", e);
        }
    }
}

