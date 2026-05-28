package br.com.nutrimind.dao;

import br.com.nutrimind.config.Database;
import br.com.nutrimind.exception.AppException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatsDao {
    public Map<String, Integer> dashboardCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Pacientes", count("SELECT COUNT(*) FROM patients WHERE active = 1"));
        counts.put("Consultas", count("SELECT COUNT(*) FROM consultations"));
        counts.put("Alertas abertos", count("SELECT COUNT(*) FROM alerts WHERE status <> 'DECIDIDO'"));
        counts.put("Planos", count("SELECT COUNT(*) FROM meal_plans"));
        return counts;
    }

    private int count(String sql) {
        try (Connection connection = Database.getInstance().getConnection();
             ResultSet rs = connection.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new AppException("Falha ao calcular indicadores.", e);
        }
    }
}

