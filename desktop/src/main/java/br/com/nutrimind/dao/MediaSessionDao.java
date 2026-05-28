package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.MediaSession;
import br.com.nutrimind.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MediaSessionDao {
    public MediaSession save(MediaSession media) {
        String sql = """
                INSERT INTO media_sessions(consultation_id, type, file_path, quality, duration_seconds, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, media.getConsultationId());
            ps.setString(2, media.getType());
            ps.setString(3, media.getFilePath());
            ps.setString(4, media.getQuality());
            ps.setInt(5, media.getDurationSeconds());
            ps.setString(6, media.getStatus());
            ps.setString(7, DateUtil.dateTime(media.getCreatedAt()));
            ps.executeUpdate();
            return media;
        } catch (SQLException e) {
            throw new AppException("Falha ao salvar mídia da consulta.", e);
        }
    }

    public List<MediaSession> findByConsultation(long consultationId) {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM media_sessions WHERE consultation_id = ? ORDER BY created_at")) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                List<MediaSession> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new MediaSession(
                            rs.getLong("id"),
                            rs.getLong("consultation_id"),
                            rs.getString("type"),
                            rs.getString("file_path"),
                            rs.getString("quality"),
                            rs.getInt("duration_seconds"),
                            rs.getString("status"),
                            DateUtil.parseDateTime(rs.getString("created_at"))
                    ));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new AppException("Falha ao listar mídias.", e);
        }
    }
}

