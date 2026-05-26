package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.SystemUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de Usuários do sistema.
 *
 * Gerencia persistência de {@link SystemUser} (nutricionistas e admins).
 * Utiliza a conexão Singleton fornecida por {@link Database}.
 *
 * As senhas são armazenadas como hash — nunca em texto plano.
 */
public class UserDao implements CrudDao<SystemUser, Integer> {

    // ---------------------------------------------------------------
    // SQL
    // ---------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO users (name, email, password_hash, role, active) VALUES (?, ?, ?, ?, 1)";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, email, password_hash, role, active FROM users WHERE id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT id, name, email, password_hash, role, active FROM users WHERE active = 1 ORDER BY name";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, name, email, password_hash, role, active FROM users WHERE email = ? AND active = 1";

    private static final String SQL_UPDATE =
            "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";

    private static final String SQL_UPDATE_PASSWORD =
            "UPDATE users SET password_hash = ? WHERE id = ?";

    private static final String SQL_DEACTIVATE =
            "UPDATE users SET active = 0 WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM users WHERE id = ?";

    // ---------------------------------------------------------------
    // CrudDao implementation
    // ---------------------------------------------------------------

    @Override
    public SystemUser save(SystemUser user) {
        validateUser(user);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
            return user;

        } catch (SQLException e) {
            throw new AppException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SystemUser> findById(Integer id) {
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
            throw new AppException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SystemUser> findAll() {
        List<SystemUser> users = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new AppException("Erro ao listar usuários: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(SystemUser user) {
        validateUser(user);
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new AppException("Usuário não encontrado para atualização (id=" + user.getId() + ").");
            }

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao deletar usuário: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Extra queries
    // ---------------------------------------------------------------

    /**
     * Busca um usuário ativo pelo e-mail. Usado na autenticação.
     *
     * @param email e-mail do usuário
     * @return Optional com o usuário ou vazio
     */
    public Optional<SystemUser> findByEmail(String email) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new AppException("Erro ao buscar usuário por e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o hash da senha de um usuário.
     *
     * @param id           identificador do usuário
     * @param passwordHash novo hash da senha
     */
    public void updatePassword(int id, String passwordHash) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {

            stmt.setString(1, passwordHash);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao atualizar senha: " + e.getMessage(), e);
        }
    }

    /**
     * Inativação lógica do usuário.
     *
     * @param id identificador do usuário
     */
    public void deactivate(int id) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DEACTIVATE)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AppException("Erro ao inativar usuário: " + e.getMessage(), e);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private SystemUser mapRow(ResultSet rs) throws SQLException {
        SystemUser u = new SystemUser();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getInt("active") == 1);
        return u;
    }

    private void validateUser(SystemUser user) {
        if (user == null) {
            throw new AppException("Usuário não pode ser nulo.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new AppException("Nome do usuário é obrigatório.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new AppException("E-mail do usuário é obrigatório.");
        }
    }
}
