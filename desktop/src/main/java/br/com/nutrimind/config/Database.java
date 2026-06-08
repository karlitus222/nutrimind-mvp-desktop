package br.com.nutrimind.config;

import br.com.nutrimind.exception.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static Database instance;

    private Database() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new AppException("Driver SQLite não encontrado. Execute scripts/download-deps.ps1.", e);
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(AppConfig.DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            throw new AppException("Não foi possível conectar ao SQLite.", e);
        }
    }
}
