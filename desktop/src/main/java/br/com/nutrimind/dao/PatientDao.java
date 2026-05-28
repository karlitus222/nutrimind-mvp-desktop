package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDao implements CrudDao<Patient> {
    @Override
    public Patient save(Patient patient) {
        return patient.getId() == 0 ? insert(patient) : update(patient);
    }

    private Patient insert(Patient patient) {
        String sql = """
                INSERT INTO patients(nutritionist_id, name, cpf, birth_date, phone, email, clinical_notes, eating_history, active, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, patient);
            ps.setString(10, DateUtil.dateTime(patient.getCreatedAt() == null ? LocalDateTime.now() : patient.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                patient.setId(keys.getLong(1));
            }
            return patient;
        } catch (SQLException e) {
            throw new AppException("Falha ao cadastrar paciente.", e);
        }
    }

    private Patient update(Patient patient) {
        String sql = """
                UPDATE patients
                SET nutritionist_id=?, name=?, cpf=?, birth_date=?, phone=?, email=?, clinical_notes=?, eating_history=?, active=?
                WHERE id=?
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, patient);
            ps.setLong(10, patient.getId());
            ps.executeUpdate();
            return patient;
        } catch (SQLException e) {
            throw new AppException("Falha ao atualizar paciente.", e);
        }
    }

    private void bind(PreparedStatement ps, Patient patient) throws SQLException {
        ps.setLong(1, patient.getNutritionistId());
        ps.setString(2, patient.getName());
        ps.setString(3, patient.getCpf());
        ps.setString(4, DateUtil.date(patient.getBirthDate()));
        ps.setString(5, patient.getPhone());
        ps.setString(6, patient.getEmail());
        ps.setString(7, patient.getClinicalNotes());
        ps.setString(8, patient.getEatingHistory());
        ps.setInt(9, patient.isActive() ? 1 : 0);
    }

    @Override
    public Optional<Patient> findById(long id) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM patients WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar paciente.", e);
        }
    }

    @Override
    public List<Patient> findAll() {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM patients WHERE active = 1 ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(map(rs));
            }
            return patients;
        } catch (SQLException e) {
            throw new AppException("Falha ao listar pacientes.", e);
        }
    }

    public List<Patient> findByNutritionist(long nutritionistId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM patients WHERE active = 1 AND nutritionist_id = ? ORDER BY name")) {
            ps.setLong(1, nutritionistId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Patient> patients = new ArrayList<>();
                while (rs.next()) {
                    patients.add(map(rs));
                }
                return patients;
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao listar pacientes do nutricionista.", e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE patients SET active = 0 WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Falha ao excluir paciente.", e);
        }
    }

    private Patient map(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getLong("id"),
                rs.getLong("nutritionist_id"),
                rs.getString("name"),
                rs.getString("cpf"),
                DateUtil.parseDate(rs.getString("birth_date")),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("clinical_notes"),
                rs.getString("eating_history"),
                rs.getInt("active") == 1,
                DateUtil.parseDateTime(rs.getString("created_at"))
        );
    }
}

