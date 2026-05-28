package br.com.nutrimind.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Responsável por inicializar o banco de dados na primeira execução.
 * Lê o arquivo schema.sql dos resources e executa os comandos de criação
 * das tabelas e inserção dos dados iniciais.
 *
 * Responsável: Pessoa 2 - Banco de dados e DER
 */
public class DatabaseInitializer {

    /**
     * Lê o schema.sql e executa todos os comandos SQL no banco SQLite.
     * Deve ser chamado uma vez na inicialização da aplicação (App.java).
     */
    public static void initialize() {
        try (Connection conn = Database.getInstance();
             Statement stmt = conn.createStatement()) {

            // Busca o schema.sql dentro do classpath (src/main/resources)
            var stream = DatabaseInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("schema.sql");

            if (stream == null) {
                System.err.println("ERRO: schema.sql não encontrado em resources!");
                return;
            }

            // Lê o arquivo inteiro como String
            String sql = new BufferedReader(new InputStreamReader(stream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Executa cada instrução SQL separada por ";"
            for (String instrucao : sql.split(";")) {
                String trimmed = instrucao.trim();
                // Ignora linhas vazias e comentários puros
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    stmt.execute(trimmed);
                }
            }

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados: " + e.getMessage(), e);
        }
    }
}
