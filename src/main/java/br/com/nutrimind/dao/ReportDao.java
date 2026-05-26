package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.Severity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de Relatórios de Consulta.
 *
 * Gerencia persistência de {@link ConsultationReport}, que é gerado
 * automaticamente ao finalizar uma consulta e pode conter análise de IA
 * como apoio opcional (quando chave configurada).
 *
 * Utiliza a conexão Singleton fornecida por {@link Database}.
 */
public class ReportDao implements CrudDao<ConsultationReport, Integer> {

    // ---------------------------------------------------------------
    // SQL
    // ---------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO consultation_reports (consultation_id, summary, ai_summary, " +
            "severity, reviewed_by_professional, created_at) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT r.id, r.consultation_id, r.summary, r.ai_summary, r.severity, " +
            "r.reviewed_by_professional, r.created_at, " +
            "c.date AS consultation_date, p.name AS patient_name " +
            "FROM consultation_reports r " +
            "JOIN consultations c ON c.id = r.consultation_id " +
            "JOIN patients p ON p.id = c.patient_id " +
            "WHERE r.id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT r.id, r.consultation_id, r.summary, r.ai_summary, r.severity, " +
            "r.reviewed_by_professional, r.created_at, " +
            "c.date AS consultation_date, p.name AS patient_name " +
            "FROM consultation_reports r " +
            "JOIN consultations c ON c.id = r.consultation_id " +
            "JOIN patients p ON p.id = c.patient_id " +
            "ORDER BY r.created_at DESC";

    private static final String SQL_FIND_BY_CONSULTATION =
            "SELECT r.id, r.consultation_id, r.summary, r.ai_summary, r.severity, " +
            "r.reviewed_by_professional, r.created_at, " +
            "c.date AS consultation_date, p.name AS patient_name " +
            "FROM consultation_reports r " +
            "JOIN consultations c ON c.id = r.consultation_id " +
            "JOIN patients p ON p.id = c.patient_id " +
            "WHERE r.consultation_id = ?";

    private static final String SQL_FIND_BY_PATIENT =
            "SELECT r.id, r.consultation_id, r.summary, r.ai_summary, r.severity, " +
            "r.reviewed_by_professional, r.created_at, " +
            "c.date AS consultation_date, p.name AS patient_name " +
            "FROM consultation_reports r " +
            "JOIN consultations c ON c.id = r.consultation_id " +
            "JOIN patients p ON p.id = c.patient_id " +
            "WHERE p.id = ? ORDER BY r.created_at DESC";

    private static final String SQL_UPDATE =
            "UPDATE consultation_reports SET summary = ?, ai_summary = ?, " +
            "severity = ?, reviewed_by_professional = ? WHERE id = ?";

    private static final String SQL_MARK_REVIEWED =
            "UPDATE consultation_reports SET reviewed_by_professional = 1 WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM consultation_reports WHERE id = ?";

    // ---------------------------------------------------------------
    // CrudDao implementation
    // ---------------------------------------------------------------

    @Override
    public ConsultationReport save(ConsultationReport report) {
        validateReport(report);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, report.getConsultationId());
            stmt.setString(2, report.getSummary());
            stmt.setString(3, report.getAiSummary());
            stmt.setString(4, report.getSeverity() != null ? report.getSeverity().name() : Severity.LOW.name());
            stmt.setInt(5, report.isReviewedByProfessional() ? 1 : 0);
            stmt.setString(6, report.getCreatedAt());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
            return report;

        } catch (SQLException e) {
            throw new AppException("Erro ao salvar relatório: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ConsultationReport> findById(Integer id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar relatório por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ConsultationReport> findAll() {
        List<ConsultationReport> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new AppException("Erro ao listar relatórios: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ConsultationReport report) {
        validateReport(report);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, report.getSummary());
            stmt.setString(2, report.getAiSummary());
            stmt.setString(3, report.getSeverity() != null ? report.getSeverity().name() : Severity.LOW.name());
            stmt.setInt(4, report.isReviewedByProfessional() ? 1 : 0);
            stmt.setInt(5, report.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Relatório não encontrado para atualização (id=" + report.getId() + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar relatório: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao deletar relatório: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Extra queries
    // ---------------------------------------------------------------

    /**
     * Busca o relatório de uma consulta específica.
     *
     * @param consultationId identificador da consulta
     * @return Optional com o relatório ou vazio
     */
    public Optional<ConsultationReport> findByConsultation(int consultationId) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CONSULTATION)) {

            stmt.setInt(1, consultationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar relatório da consulta: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos os relatórios de um paciente, ordenados do mais recente.
     *
     * @param patientId identificador do paciente
     * @return lista de relatórios
     */
    public List<ConsultationReport> findByPatient(int patientId) {
        List<ConsultationReport> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_PATIENT)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar relatórios do paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Marca um relatório como revisado pelo profissional.
     * Demonstra que a IA é apenas apoio — a revisão humana é obrigatória.
     *
     * @param id identificador do relatório
     */
    public void markAsReviewed(int id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MARK_REVIEWED)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Relatório não encontrado para revisão (id=" + id + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao marcar relatório como revisado: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private ConsultationReport mapRow(ResultSet rs) throws SQLException {
        ConsultationReport r = new ConsultationReport();
        r.setId(rs.getInt("id"));
        r.setConsultationId(rs.getInt("consultation_id"));
        r.setSummary(rs.getString("summary"));
        r.setAiSummary(rs.getString("ai_summary"));
        r.setSeverity(Severity.valueOf(rs.getString("severity")));
        r.setReviewedByProfessional(rs.getInt("reviewed_by_professional") == 1);
        r.setCreatedAt(rs.getString("created_at"));
        r.setConsultationDate(rs.getString("consultation_date"));
        r.setPatientName(rs.getString("patient_name"));
        return r;
    }

    private void validateReport(ConsultationReport report) {
        if (report == null) {
            throw new AppException("Relatório não pode ser nulo.");
        }
        if (report.getConsultationId() <= 0) {
            throw new AppException("Relatório deve estar vinculado a uma consulta válida.");
        }
        if (report.getSummary() == null || report.getSummary().isBlank()) {
            throw new AppException("Resumo do relatório é obrigatório.");
        }
    }
}
