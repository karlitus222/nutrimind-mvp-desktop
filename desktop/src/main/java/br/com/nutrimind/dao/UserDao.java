package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.Nutritionist;
import br.com.nutrimind.model.Role;
import br.com.nutrimind.model.SystemUser;
import br.com.nutrimind.model.User;
import br.com.nutrimind.util.DateUtil;
import br.com.nutrimind.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements CrudDao<User> {
    @Override
    public User save(User entity) {
        if (entity.getId() == 0) {
            return insert(entity);
        }
        throw new AppException("Atualização genérica de usuário não implementada nesta tela.");
    }

    public User createNutritionist(String name, String email, char[] password, String crn, String specialty) {
        try (Connection connection = Database.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            long id;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users(name, email, password_hash, role, active, created_at) VALUES (?, ?, ?, ?, 1, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, PasswordUtil.hash(password));
                ps.setString(4, Role.NUTRICIONISTA.name());
                ps.setString(5, LocalDateTime.now().toString());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getLong(1);
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO nutritionists(user_id, crn, specialty) VALUES (?, ?, ?)")) {
                ps.setLong(1, id);
                ps.setString(2, crn);
                ps.setString(3, specialty);
                ps.executeUpdate();
            }
            connection.commit();
            return findById(id).orElseThrow();
        } catch (SQLException e) {
            throw new AppException("Falha ao cadastrar nutricionista.", e);
        }
    }

    private User insert(User entity) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO users(name, email, password_hash, role, active, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getPasswordHash());
            ps.setString(4, entity.getRole().name());
            ps.setInt(5, entity.isActive() ? 1 : 0);
            ps.setString(6, DateUtil.dateTime(entity.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                entity.setId(keys.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar usuário.", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = """
                SELECT u.*, n.crn, n.specialty
                FROM users u
                LEFT JOIN nutritionists n ON n.user_id = u.id
                WHERE u.id = ?
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar usuário.", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT u.*, n.crn, n.specialty
                FROM users u
                LEFT JOIN nutritionists n ON n.user_id = u.id
                WHERE lower(u.email) = lower(?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao buscar usuário por e-mail.", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT u.*, n.crn, n.specialty
                FROM users u
                LEFT JOIN nutritionists n ON n.user_id = u.id
                ORDER BY u.name
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(map(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new AppException("Falha ao listar usuários.", e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE users SET active = 0 WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Falha ao desativar usuário.", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        if (role == Role.NUTRICIONISTA) {
            return new Nutritionist(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    role,
                    rs.getInt("active") == 1,
                    DateUtil.parseDateTime(rs.getString("created_at")),
                    rs.getString("crn"),
                    rs.getString("specialty")
            );
        }
        return new SystemUser(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                role,
                rs.getInt("active") == 1,
                DateUtil.parseDateTime(rs.getString("created_at"))
        );
    }
}

