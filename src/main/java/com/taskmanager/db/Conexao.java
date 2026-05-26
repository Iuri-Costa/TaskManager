package com.taskmanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    private static final String URL =
            "jdbc:mariadb://localhost:3306/taskmanager";

    private static final String USER = "iuri";
    private static final String PASSWORD = "123456";

    private Conexao() {
    }

    public static Connection obterConexao()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }

    public static void inicializarBanco() {

        String sql = """
                CREATE TABLE IF NOT EXISTS tarefas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(255) NOT NULL,
                    descricao TEXT,
                    prioridade VARCHAR(50) NOT NULL,
                    data_criacao DATE NOT NULL,
                    concluida BOOLEAN NOT NULL DEFAULT FALSE
                );
                """;

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            System.out.println(
                    "[DB] Banco de dados inicializado com sucesso."
            );

        } catch (SQLException e) {

            System.err.println(
                    "[DB] Erro ao inicializar banco: "
                            + e.getMessage()
            );
        }
    }

    public static void inicializarBanco(Connection conn)
            throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS tarefas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(255) NOT NULL,
                    descricao TEXT,
                    prioridade VARCHAR(50) NOT NULL,
                    data_criacao DATE NOT NULL,
                    concluida BOOLEAN NOT NULL DEFAULT FALSE
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}