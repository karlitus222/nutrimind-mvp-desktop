package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MealPlanDao {
    public MealPlan save(MealPlan plan) {
        String sql = """
                INSERT INTO meal_plans(patient_id, consultation_id, objective, description, status, start_date, end_date, approved_by, approved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, plan);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    plan.setId(keys.getLong(1));
                }
            }
            return plan;
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar plano alimentar.", e);
        }
    }

    public void approve(long planId, long userId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE meal_plans SET status='APROVADO', approved_by=?, approved_at=? WHERE id=?")) {
            ps.setLong(1, userId);
            ps.setString(2, LocalDateTime.now().toString());
            ps.setLong(3, planId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Falha ao aprovar plano.", e);
        }
    }

    public List<MealPlan> findByPatient(long patientId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM meal_plans WHERE patient_id = ? ORDER BY id DESC")) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                List<MealPlan> plans = new ArrayList<>();
                while (rs.next()) {
                    plans.add(map(rs));
                }
                return plans;
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao listar planos.", e);
        }
    }

    private void bind(PreparedStatement ps, MealPlan plan) throws SQLException {
        ps.setLong(1, plan.getPatientId());
        ps.setLong(2, plan.getConsultationId());
        ps.setString(3, plan.getObjective());
        ps.setString(4, plan.getDescription());
        ps.setString(5, plan.getStatus());
        ps.setString(6, DateUtil.date(plan.getStartDate()));
        ps.setString(7, DateUtil.date(plan.getEndDate()));
        if (plan.getApprovedBy() == null) {
            ps.setObject(8, null);
        } else {
            ps.setLong(8, plan.getApprovedBy());
        }
        ps.setString(9, DateUtil.dateTime(plan.getApprovedAt()));
    }

    private MealPlan map(ResultSet rs) throws SQLException {
        long approvedByValue = rs.getLong("approved_by");
        Long approvedBy = rs.wasNull() ? null : approvedByValue;
        return new MealPlan(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getLong("consultation_id"),
                rs.getString("objective"),
                rs.getString("description"),
                rs.getString("status"),
                DateUtil.parseDate(rs.getString("start_date")),
                DateUtil.parseDate(rs.getString("end_date")),
                approvedBy,
                DateUtil.parseDateTime(rs.getString("approved_at"))
        );
    }
}
