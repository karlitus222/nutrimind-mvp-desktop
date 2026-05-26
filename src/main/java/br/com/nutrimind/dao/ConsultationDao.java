package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de Consultas.
 *
 * Implementa {@link CrudDao} para a entidade {@link Consultation}.
 * As consultas são sempre vinculadas a um paciente (FK) e a um
 * nutricionista (FK), garantindo a integridade relacional exigida pelo DER.
 *
 * Utiliza a conexão Singleton fornecida por {@link Database}.
 */
public class ConsultationDao implements CrudDao<Consultation, Integer> {

    // ---------------------------------------------------------------
    // SQL
    // ---------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO consultations (patient_id, nutritionist_id, date, weight, height, " +
            "bmi, observations, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT c.id, c.patient_id, c.nutritionist_id, c.date, c.weight, c.height, " +
            "c.bmi, c.observations, c.status, " +
            "p.name AS patient_name, u.name AS nutritionist_name " +
            "FROM consultations c " +
            "JOIN patients p ON p.id = c.patient_id " +
            "JOIN users u ON u.id = c.nutritionist_id " +
            "WHERE c.id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT c.id, c.patient_id, c.nutritionist_id, c.date, c.weight, c.height, " +
            "c.bmi, c.observations, c.status, " +
            "p.name AS patient_name, u.name AS nutritionist_name " +
            "FROM consultations c " +
            "JOIN patients p ON p.id = c.patient_id " +
            "JOIN users u ON u.id = c.nutritionist_id " +
            "ORDER BY c.date DESC";

    private static final String SQL_FIND_BY_PATIENT =
            "SELECT c.id, c.patient_id, c.nutritionist_id, c.date, c.weight, c.height, " +
            "c.bmi, c.observations, c.status, " +
            "p.name AS patient_name, u.name AS nutritionist_name " +
            "FROM consultations c " +
            "JOIN patients p ON p.id = c.patient_id " +
            "JOIN users u ON u.id = c.nutritionist_id " +
            "WHERE c.patient_id = ? ORDER BY c.date DESC";

    private static final String SQL_FIND_BY_NUTRITIONIST =
            "SELECT c.id, c.patient_id, c.nutritionist_id, c.date, c.weight, c.height, " +
            "c.bmi, c.observations, c.status, " +
            "p.name AS patient_name, u.name AS nutritionist_name " +
            "FROM consultations c " +
            "JOIN patients p ON p.id = c.patient_id " +
            "JOIN users u ON u.id = c.nutritionist_id " +
            "WHERE c.nutritionist_id = ? ORDER BY c.date DESC";

    private static final String SQL_UPDATE =
            "UPDATE consultations SET patient_id = ?, nutritionist_id = ?, date = ?, " +
            "weight = ?, height = ?, bmi = ?, observations = ?, status = ? WHERE id = ?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE consultations SET status = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM consultations WHERE id = ?";

    // ---------------------------------------------------------------
    // CrudDao implementation
    // ---------------------------------------------------------------

    @Override
    public Consultation save(Consultation consultation) {
        validateConsultation(consultation);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, consultation.getPatientId());
            stmt.setInt(2, consultation.getNutritionistId());
            stmt.setString(3, consultation.getDate());
            stmt.setDouble(4, consultation.getWeight());
            stmt.setDouble(5, consultation.getHeight());
            stmt.setDouble(6, consultation.getBmi());
            stmt.setString(7, consultation.getObservations());
            stmt.setString(8, consultation.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    consultation.setId(keys.getInt(1));
                }
            }
            return consultation;

        } catch (SQLException e) {
            throw new AppException("Erro ao salvar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Consultation> findById(Integer id) {
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
            throw new AppException("Erro ao buscar consulta por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Consultation> findAll() {
        List<Consultation> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new AppException("Erro ao listar consultas: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Consultation consultation) {
        validateConsultation(consultation);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setInt(1, consultation.getPatientId());
            stmt.setInt(2, consultation.getNutritionistId());
            stmt.setString(3, consultation.getDate());
            stmt.setDouble(4, consultation.getWeight());
            stmt.setDouble(5, consultation.getHeight());
            stmt.setDouble(6, consultation.getBmi());
            stmt.setString(7, consultation.getObservations());
            stmt.setString(8, consultation.getStatus().name());
            stmt.setInt(9, consultation.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Consulta não encontrada para atualização (id=" + consultation.getId() + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao deletar consulta: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Extra queries
    // ---------------------------------------------------------------

    /**
     * Retorna o histórico de consultas de um paciente específico.
     *
     * @param patientId identificador do paciente
     * @return lista de consultas ordenadas por data (mais recente primeiro)
     */
    public List<Consultation> findByPatient(int patientId) {
        List<Consultation> list = new ArrayList<>();
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
            throw new AppException("Erro ao buscar consultas do paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todas as consultas registradas por um nutricionista.
     *
     * @param nutritionistId identificador do nutricionista
     * @return lista de consultas
     */
    public List<Consultation> findByNutritionist(int nutritionistId) {
        List<Consultation> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NUTRITIONIST)) {

            stmt.setInt(1, nutritionistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar consultas do nutricionista: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza apenas o status de uma consulta existente.
     *
     * @param id     identificador da consulta
     * @param status novo status
     */
    public void updateStatus(int id, ConsultationStatus status) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_STATUS)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Consulta não encontrada para atualização de status (id=" + id + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar status da consulta: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Consultation mapRow(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setId(rs.getInt("id"));
        c.setPatientId(rs.getInt("patient_id"));
        c.setNutritionistId(rs.getInt("nutritionist_id"));
        c.setDate(rs.getString("date"));
        c.setWeight(rs.getDouble("weight"));
        c.setHeight(rs.getDouble("height"));
        c.setBmi(rs.getDouble("bmi"));
        c.setObservations(rs.getString("observations"));
        c.setStatus(ConsultationStatus.valueOf(rs.getString("status")));
        c.setPatientName(rs.getString("patient_name"));
        c.setNutritionistName(rs.getString("nutritionist_name"));
        return c;
    }

    private void validateConsultation(Consultation consultation) {
        if (consultation == null) {
            throw new AppException("Consulta não pode ser nula.");
        }
        if (consultation.getPatientId() <= 0) {
            throw new AppException("Consulta deve estar vinculada a um paciente válido.");
        }
        if (consultation.getNutritionistId() <= 0) {
            throw new AppException("Consulta deve estar vinculada a um nutricionista válido.");
        }
        if (consultation.getDate() == null || consultation.getDate().isBlank()) {
            throw new AppException("Data da consulta é obrigatória.");
        }
    }
}
