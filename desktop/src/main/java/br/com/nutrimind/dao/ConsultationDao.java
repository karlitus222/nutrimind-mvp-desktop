package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationStatus;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationDao implements CrudDao<Consultation> {
    @Override
    public Consultation save(Consultation consultation) {
        return consultation.getId() == 0 ? insert(consultation) : update(consultation);
    }

    private Consultation insert(Consultation consultation) {
        String sql = """
                INSERT INTO consultations(patient_id, nutritionist_id, started_at, ended_at, status, consent_audio, consent_video,
                clinical_notes, transcript, visual_observations)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, consultation);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                consultation.setId(keys.getLong(1));
            }
            return consultation;
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar consulta.", e);
        }
    }

    private Consultation update(Consultation consultation) {
        String sql = """
                UPDATE consultations
                SET patient_id=?, nutritionist_id=?, started_at=?, ended_at=?, status=?, consent_audio=?, consent_video=?,
                clinical_notes=?, transcript=?, visual_observations=?
                WHERE id=?
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, consultation);
            ps.setLong(11, consultation.getId());
            ps.executeUpdate();
            return consultation;
        } catch (SQLException e) {
            throw new AppException("Falha ao atualizar consulta.", e);
        }
    }

    private void bind(PreparedStatement ps, Consultation consultation) throws SQLException {
        ps.setLong(1, consultation.getPatientId());
        ps.setLong(2, consultation.getNutritionistId());
        ps.setString(3, DateUtil.dateTime(consultation.getStartedAt()));
        ps.setString(4, DateUtil.dateTime(consultation.getEndedAt()));
        ps.setString(5, consultation.getStatus().name());
        ps.setInt(6, consultation.hasConsentAudio() ? 1 : 0);
        ps.setInt(7, consultation.hasConsentVideo() ? 1 : 0);
        ps.setString(8, consultation.getClinicalNotes());
        ps.setString(9, consultation.getTranscript());
        ps.setString(10, consultation.getVisualObservations());
    }

    @Override
    public Optional<Consultation> findById(long id) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM consultations WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar consulta.", e);
        }
    }

    @Override
    public List<Consultation> findAll() {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM consultations ORDER BY started_at DESC");
             ResultSet rs = ps.executeQuery()) {
            List<Consultation> consultations = new ArrayList<>();
            while (rs.next()) {
                consultations.add(map(rs));
            }
            return consultations;
        } catch (SQLException e) {
            throw new AppException("Falha ao listar consultas.", e);
        }
    }

    public List<Consultation> findByPatient(long patientId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM consultations WHERE patient_id = ? ORDER BY started_at DESC")) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Consultation> consultations = new ArrayList<>();
                while (rs.next()) {
                    consultations.add(map(rs));
                }
                return consultations;
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao listar histórico.", e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM consultations WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Falha ao excluir consulta.", e);
        }
    }

    private Consultation map(ResultSet rs) throws SQLException {
        return new Consultation(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getLong("nutritionist_id"),
                DateUtil.parseDateTime(rs.getString("started_at")),
                DateUtil.parseDateTime(rs.getString("ended_at")),
                ConsultationStatus.valueOf(rs.getString("status")),
                rs.getInt("consent_audio") == 1,
                rs.getInt("consent_video") == 1,
                rs.getString("clinical_notes"),
                rs.getString("transcript"),
                rs.getString("visual_observations")
        );
    }
}

