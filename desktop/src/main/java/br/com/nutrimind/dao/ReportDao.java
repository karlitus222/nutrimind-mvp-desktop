package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ReportDao {
    public ConsultationReport save(ConsultationReport report) {
        String sql = """
                INSERT INTO consultation_reports(consultation_id, identification_section, clinical_section, recommendations_section, limitations_section, generated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, report.getConsultationId());
            ps.setString(2, report.getIdentificationSection());
            ps.setString(3, report.getClinicalSection());
            ps.setString(4, report.getRecommendationsSection());
            ps.setString(5, report.getLimitationsSection());
            ps.setString(6, DateUtil.dateTime(report.getGeneratedAt()));
            ps.executeUpdate();
            return report;
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar relatório.", e);
        }
    }

    public Optional<ConsultationReport> findLatestByConsultation(long consultationId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM consultation_reports WHERE consultation_id = ? ORDER BY generated_at DESC LIMIT 1")) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new ConsultationReport(
                        rs.getLong("id"),
                        rs.getLong("consultation_id"),
                        rs.getString("identification_section"),
                        rs.getString("clinical_section"),
                        rs.getString("recommendations_section"),
                        rs.getString("limitations_section"),
                        DateUtil.parseDateTime(rs.getString("generated_at"))
                ));
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar relatório.", e);
        }
    }
}

