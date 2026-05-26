package br.com.nutrimind.config;

import br.com.nutrimind.exception.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerenciador de conexão com o banco de dados SQLite.
 *
 * Implementa o padrão de projeto <strong>Singleton</strong> com
 * sincronização thread-safe (double-checked locking), garantindo
 * que apenas uma instância seja criada durante toda a execução
 * do aplicativo.
 *
 * O arquivo do banco é criado automaticamente pelo SQLite na primeira
 * execução, no caminho definido em {@code DB_URL}.
 */
public class Database {

    private static final String DB_URL = "jdbc:sqlite:nutrimind.db";

    // Instância única — volatile garante visibilidade entre threads
    private static volatile Database instance;

    private Connection connection;

    // Construtor privado impede instanciação externa
    private Database() {
        connect();
    }

    /**
     * Retorna a instância única de {@code Database}.
     * Utiliza double-checked locking para segurança em ambientes multi-thread.
     *
     * @return instância Singleton
     */
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    /**
     * Retorna a conexão ativa com o banco.
     * Reconecta automaticamente se a conexão estiver fechada ou inválida.
     *
     * @return {@link Connection} ativa com o SQLite
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados.
     * Deve ser chamado ao encerrar a aplicação.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Silencioso no encerramento
        } finally {
            connection = null;
            instance = null;
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            // Habilita suporte a chaves estrangeiras no SQLite
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (ClassNotFoundException e) {
            throw new AppException(
                    "Driver SQLite não encontrado. Verifique se sqlite-jdbc está no classpath.", e);
        } catch (SQLException e) {
            throw new AppException("Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}
