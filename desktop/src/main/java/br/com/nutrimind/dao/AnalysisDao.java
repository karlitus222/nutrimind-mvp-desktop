package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.RiskAnalysis;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class AnalysisDao {
    public RiskAnalysis save(RiskAnalysis analysis) {
        String sql = """
                INSERT INTO risk_analyses(consultation_id, provider, model, raw_json, summary, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, analysis.getConsultationId());
            ps.setString(2, analysis.getProvider());
            ps.setString(3, analysis.getModel());
            ps.setString(4, analysis.getRawJson());
            ps.setString(5, analysis.getSummary());
            ps.setString(6, DateUtil.dateTime(analysis.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new RiskAnalysis(keys.getLong(1), analysis.getConsultationId(), analysis.getProvider(),
                        analysis.getModel(), analysis.getRawJson(), analysis.getSummary(), analysis.getCreatedAt());
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar análise de risco.", e);
        }
    }

    public Optional<RiskAnalysis> findLatestByConsultation(long consultationId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM risk_analyses WHERE consultation_id = ? ORDER BY created_at DESC LIMIT 1")) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new RiskAnalysis(
                        rs.getLong("id"),
                        rs.getLong("consultation_id"),
                        rs.getString("provider"),
                        rs.getString("model"),
                        rs.getString("raw_json"),
                        rs.getString("summary"),
                        DateUtil.parseDateTime(rs.getString("created_at"))
                ));
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar análise.", e);
        }
    }
}

