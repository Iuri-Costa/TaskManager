package com.taskmanager.dao;

import com.taskmanager.db.Conexao;
import com.taskmanager.generic.Repositorio;
import com.taskmanager.model.Tarefa;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaDAO implements Repositorio<Tarefa, Integer> {

    public TarefaDAO() {
    }

    protected Connection getConn() throws SQLException {
        return Conexao.obterConexao();
    }

    @Override
    public void adicionar(Tarefa tarefa) {

        String sql = """
                INSERT INTO tarefas
                (titulo, descricao, prioridade, data_criacao, concluida)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setString(1, tarefa.getTitulo());
            ps.setString(2, tarefa.getDescricao());
            ps.setString(3, tarefa.getPrioridade());

            ps.setDate(
                    4,
                    Date.valueOf(tarefa.getDataCriacao())
            );

            ps.setBoolean(
                    5,
                    tarefa.isConcluida()
            );

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tarefa.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "[DAO] Erro ao inserir tarefa: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Erro ao salvar tarefa.",
                    e
            );
        }
    }

    @Override
    public Optional<Tarefa> buscarPorId(Integer id) {

        String sql =
                "SELECT * FROM tarefas WHERE id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            mapearTarefa(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao buscar tarefa: "
                            + e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Tarefa> listarTodos() {

        String sql =
                "SELECT * FROM tarefas ORDER BY id DESC";

        List<Tarefa> tarefas =
                new ArrayList<>();

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tarefas.add(
                        mapearTarefa(rs)
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao listar tarefas: "
                            + e.getMessage()
            );
        }

        return tarefas;
    }

    public List<Tarefa> listarPorStatus(
            boolean concluida
    ) {

        String sql = """
                SELECT *
                FROM tarefas
                WHERE concluida = ?
                ORDER BY prioridade
                """;

        List<Tarefa> tarefas =
                new ArrayList<>();

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setBoolean(1, concluida);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {
                    tarefas.add(
                            mapearTarefa(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao filtrar tarefas: "
                            + e.getMessage()
            );
        }

        return tarefas;
    }

    @Override
    public void atualizar(Tarefa tarefa) {

        String sql = """
                UPDATE tarefas
                SET titulo = ?,
                    descricao = ?,
                    prioridade = ?,
                    concluida = ?
                WHERE id = ?
                """;

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            ps.setString(1,
                    tarefa.getTitulo());

            ps.setString(2,
                    tarefa.getDescricao());

            ps.setString(3,
                    tarefa.getPrioridade());

            ps.setBoolean(4,
                    tarefa.isConcluida());

            ps.setInt(5,
                    tarefa.getId());

            ps.executeUpdate();

            conn.commit();

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao atualizar tarefa: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Erro ao atualizar tarefa.",
                    e
            );
        }
    }

    public void marcarComoConcluida(
            int id
    ) {

        String sql = """
                UPDATE tarefas
                SET concluida = TRUE
                WHERE id = ?
                """;

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao concluir tarefa: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Erro ao concluir tarefa.",
                    e
            );
        }
    }

    @Override
    public void remover(Integer id) {

        String sql =
                "DELETE FROM tarefas WHERE id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            ps.setInt(1, id);

            ps.executeUpdate();

            conn.commit();

        } catch (SQLException e) {

            System.err.println(
                    "[DAO] Erro ao remover tarefa: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Erro ao remover tarefa.",
                    e
            );
        }
    }

    private Tarefa mapearTarefa(
            ResultSet rs
    ) throws SQLException {

        return new Tarefa(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("prioridade"),

                rs.getDate(
                        "data_criacao"
                ).toLocalDate(),

                rs.getBoolean(
                        "concluida"
                )
        );
    }
}
