package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de Pacientes.
 *
 * Implementa {@link CrudDao} com operações de persistência para a entidade
 * {@link Patient}. Utiliza a conexão Singleton fornecida por {@link Database}.
 *
 * Suporta inativação lógica: pacientes nunca são deletados fisicamente,
 * apenas marcados como inativos (active = 0).
 */
public class PatientDao implements CrudDao<Patient, Integer> {

    // ---------------------------------------------------------------
    // SQL
    // ---------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO patients (name, cpf, birth_date, phone, email, notes, active) " +
            "VALUES (?, ?, ?, ?, ?, ?, 1)";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, cpf, birth_date, phone, email, notes, active " +
            "FROM patients WHERE id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT id, name, cpf, birth_date, phone, email, notes, active " +
            "FROM patients WHERE active = 1 ORDER BY name";

    private static final String SQL_FIND_ALL_INCLUDING_INACTIVE =
            "SELECT id, name, cpf, birth_date, phone, email, notes, active " +
            "FROM patients ORDER BY name";

    private static final String SQL_UPDATE =
            "UPDATE patients SET name = ?, cpf = ?, birth_date = ?, phone = ?, " +
            "email = ?, notes = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM patients WHERE id = ?";

    private static final String SQL_DEACTIVATE =
            "UPDATE patients SET active = 0 WHERE id = ?";

    private static final String SQL_FIND_BY_CPF =
            "SELECT id, name, cpf, birth_date, phone, email, notes, active " +
            "FROM patients WHERE cpf = ? AND active = 1";

    private static final String SQL_SEARCH =
            "SELECT id, name, cpf, birth_date, phone, email, notes, active " +
            "FROM patients WHERE active = 1 AND (name LIKE ? OR cpf LIKE ?) ORDER BY name";

    // ---------------------------------------------------------------
    // CrudDao implementation
    // ---------------------------------------------------------------

    @Override
    public Patient save(Patient patient) {
        validatePatient(patient);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getCpf());
            stmt.setString(3, patient.getBirthDate());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getEmail());
            stmt.setString(6, patient.getNotes());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    patient.setId(keys.getInt(1));
                }
            }
            return patient;

        } catch (SQLException e) {
            throw new AppException("Erro ao salvar paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findById(Integer id) {
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
            throw new AppException("Erro ao buscar paciente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRow(rs));
            }
            return patients;

        } catch (SQLException e) {
            throw new AppException("Erro ao listar pacientes: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Patient patient) {
        validatePatient(patient);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getCpf());
            stmt.setString(3, patient.getBirthDate());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getEmail());
            stmt.setString(6, patient.getNotes());
            stmt.setInt(7, patient.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Paciente não encontrado para atualização (id=" + patient.getId() + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Remove fisicamente o paciente. Use apenas em testes ou administração.
     * Para uso normal, prefira {@link #deactivate(int)}.
     */
    @Override
    public void delete(Integer id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao deletar paciente: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Extra queries
    // ---------------------------------------------------------------

    /**
     * Inativação lógica: marca o paciente como inativo sem remover do banco.
     *
     * @param id identificador do paciente
     */
    public void deactivate(int id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DEACTIVATE)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Paciente não encontrado para inativação (id=" + id + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao inativar paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um paciente ativo pelo CPF.
     *
     * @param cpf CPF sem formatação
     * @return Optional com o paciente ou vazio
     */
    public Optional<Patient> findByCpf(String cpf) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_CPF)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar paciente por CPF: " + e.getMessage(), e);
        }
    }

    /**
     * Pesquisa pacientes ativos por nome ou CPF (busca parcial com LIKE).
     *
     * @param term termo de busca
     * @return lista de pacientes correspondentes
     */
    public List<Patient> search(String term) {
        List<Patient> results = new ArrayList<>();
        String like = "%" + term + "%";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH)) {

            stmt.setString(1, like);
            stmt.setString(2, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
            return results;

        } catch (SQLException e) {
            throw new AppException("Erro ao pesquisar pacientes: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos os pacientes, incluindo inativos.
     * Útil para relatórios administrativos.
     */
    public List<Patient> findAllIncludingInactive() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL_INCLUDING_INACTIVE);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRow(rs));
            }
            return patients;

        } catch (SQLException e) {
            throw new AppException("Erro ao listar todos os pacientes: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCpf(rs.getString("cpf"));
        p.setBirthDate(rs.getString("birth_date"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setNotes(rs.getString("notes"));
        p.setActive(rs.getInt("active") == 1);
        return p;
    }

    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new AppException("Paciente não pode ser nulo.");
        }
        if (patient.getName() == null || patient.getName().isBlank()) {
            throw new AppException("Nome do paciente é obrigatório.");
        }
        if (patient.getCpf() == null || patient.getCpf().isBlank()) {
            throw new AppException("CPF do paciente é obrigatório.");
        }
    }
}
