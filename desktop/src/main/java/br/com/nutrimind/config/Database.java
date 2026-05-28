package br.com.nutrimind.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de conexão com o banco SQLite.
 * Implementa o padrão Singleton para garantir uma única instância de conexão.
 *
 * Responsável: Pessoa 2 - Banco de dados e DER
 */
public class Database {

    private static final String URL = "jdbc:sqlite:nutrimind.db";
    private static Connection instance;

    // Construtor privado — impede criação de instâncias externas (Singleton)
    private Database() {}

    /**
     * Retorna a instância única da conexão com o banco.
     * Cria uma nova conexão caso ainda não exista ou esteja fechada.
     *
     * @return Connection conexão ativa com o SQLite
     * @throws SQLException em caso de falha na conexão
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL);
            instance.setAutoCommit(true);
            System.out.println("Conexão com banco SQLite estabelecida.");
        }
        return instance;
    }

    /**
     * Fecha a conexão com o banco, se estiver aberta.
     */
    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("Conexão com banco encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
