package com.taskmanager;

import com.taskmanager.dao.TarefaDAO;
import com.taskmanager.model.Tarefa;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TarefaDAOTest {

    private Connection conexaoTeste;
    private TarefaDAO dao;

    private static final String SQL_CRIAR_TABELA = """
            CREATE TABLE IF NOT EXISTS tarefas (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo    VARCHAR(255) NOT NULL,
                descricao TEXT,
                prioridade VARCHAR(50) NOT NULL,
                data_criacao DATE NOT NULL,
                concluida BOOLEAN NOT NULL DEFAULT 0
            );
            """;

    @BeforeEach
    void setUp() throws SQLException {
        conexaoTeste = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = conexaoTeste.createStatement()) {
            stmt.execute(SQL_CRIAR_TABELA);
        }

        Connection conexaoProtegida = new ConnectionWrapper(conexaoTeste);

        dao = new TarefaDAO() {
            @Override
            protected Connection getConn() {
                return conexaoProtegida;
            }
        };
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conexaoTeste != null && !conexaoTeste.isClosed()) {
            conexaoTeste.close();
        }
    }


    @Test
    @DisplayName("Deve inserir tarefa e gerar ID automaticamente")
    void deveInserirTarefaEGerarId() {
        Tarefa tarefa = new Tarefa("Comprar livro", "Clean Code", "MEDIA");

        dao.adicionar(tarefa);

        assertTrue(tarefa.getId() > 0, "ID deve ser gerado após inserção");
    }

    @Test
    @DisplayName("Deve recuperar tarefa inserida por ID")
    void deveRecuperarTarefaPorId() {
        Tarefa tarefa = new Tarefa("Fazer exercícios", "30 minutos de caminhada", "BAIXA");
        dao.adicionar(tarefa);

        Optional<Tarefa> resultado = dao.buscarPorId(tarefa.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Fazer exercícios", resultado.get().getTitulo());
        assertEquals("BAIXA", resultado.get().getPrioridade());
    }

    @Test
    @DisplayName("buscarPorId com ID inexistente deve retornar Optional vazio")
    void buscarIdInexistenteDeveRetornarVazio() {
        Optional<Tarefa> resultado = dao.buscarPorId(9999);
        assertFalse(resultado.isPresent());
    }


    @Test
    @DisplayName("listarTodos deve retornar lista vazia quando não há tarefas")
    void listarTodosComBancoVazioDeveRetornarListaVazia() {
        List<Tarefa> lista = dao.listarTodos();
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("listarTodos deve retornar todas as tarefas inseridas")
    void listarTodosDeveRetornarTodasAsTarefas() {
        dao.adicionar(new Tarefa("Tarefa A", "", "ALTA"));
        dao.adicionar(new Tarefa("Tarefa B", "", "MEDIA"));
        dao.adicionar(new Tarefa("Tarefa C", "", "BAIXA"));

        List<Tarefa> lista = dao.listarTodos();

        assertEquals(3, lista.size());
    }


    @Test
    @DisplayName("Deve atualizar título e prioridade de uma tarefa existente")
    void deveAtualizarTarefa() {
        Tarefa tarefa = new Tarefa("Título original", "Desc", "BAIXA");
        dao.adicionar(tarefa);

        tarefa.setTitulo("Título atualizado");
        tarefa.setPrioridade("ALTA");
        dao.atualizar(tarefa);

        Optional<Tarefa> resultado = dao.buscarPorId(tarefa.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Título atualizado", resultado.get().getTitulo());
        assertEquals("ALTA", resultado.get().getPrioridade());
    }


    @Test
    @DisplayName("Deve marcar tarefa como concluída no banco")
    void deveMarcarTarefaComoConcluida() {
        Tarefa tarefa = new Tarefa("Estudar JDBC", "Capítulo 4", "ALTA");
        dao.adicionar(tarefa);

        dao.marcarComoConcluida(tarefa.getId());

        Optional<Tarefa> resultado = dao.buscarPorId(tarefa.getId());
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isConcluida());
    }


    @Test
    @DisplayName("Deve listar apenas tarefas pendentes")
    void deveListarSomentePendentes() {
        Tarefa pendente = new Tarefa("Pendente", "", "MEDIA");
        Tarefa concluida = new Tarefa("Concluída", "", "MEDIA");
        dao.adicionar(pendente);
        dao.adicionar(concluida);
        dao.marcarComoConcluida(concluida.getId());

        List<Tarefa> pendentes = dao.listarPorStatus(false);

        assertEquals(1, pendentes.size());
        assertEquals("Pendente", pendentes.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deve listar apenas tarefas concluídas")
    void deveListarSomenteConcluidas() {
        Tarefa pendente = new Tarefa("Pendente", "", "BAIXA");
        Tarefa concluida = new Tarefa("Concluída", "", "ALTA");
        dao.adicionar(pendente);
        dao.adicionar(concluida);
        dao.marcarComoConcluida(concluida.getId());

        List<Tarefa> concluidas = dao.listarPorStatus(true);

        assertEquals(1, concluidas.size());
        assertTrue(concluidas.get(0).isConcluida());
    }


    @Test
    @DisplayName("Deve remover tarefa pelo ID")
    void deveRemoverTarefa() {
        Tarefa tarefa = new Tarefa("Para remover", "", "BAIXA");
        dao.adicionar(tarefa);

        dao.remover(tarefa.getId());

        Optional<Tarefa> resultado = dao.buscarPorId(tarefa.getId());
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Após remoção, listarTodos não deve conter a tarefa removida")
    void listarTodosNaoDeveConterTarefaRemovida() {
        Tarefa t1 = new Tarefa("Manter", "", "MEDIA");
        Tarefa t2 = new Tarefa("Remover", "", "ALTA");
        dao.adicionar(t1);
        dao.adicionar(t2);

        dao.remover(t2.getId());

        List<Tarefa> lista = dao.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("Manter", lista.get(0).getTitulo());
    }

    private static class ConnectionWrapper implements Connection {

        private final Connection delegate;

        ConnectionWrapper(Connection delegate) {
            this.delegate = delegate;
        }

        @Override public void close() { /* intencional: não fecha */ }
        @Override public boolean isClosed() throws SQLException { return delegate.isClosed(); }
        @Override public void commit() throws SQLException { delegate.commit(); }
        @Override public void rollback() throws SQLException { delegate.rollback(); }
        @Override public void setAutoCommit(boolean a) throws SQLException { delegate.setAutoCommit(a); }
        @Override public boolean getAutoCommit() throws SQLException { return delegate.getAutoCommit(); }
        @Override public PreparedStatement prepareStatement(String s) throws SQLException { return delegate.prepareStatement(s); }
        @Override public PreparedStatement prepareStatement(String s, int i) throws SQLException { return delegate.prepareStatement(s, i); }
        @Override public PreparedStatement prepareStatement(String s, int[] c) throws SQLException { return delegate.prepareStatement(s, c); }
        @Override public PreparedStatement prepareStatement(String s, String[] c) throws SQLException { return delegate.prepareStatement(s, c); }
        @Override public PreparedStatement prepareStatement(String s, int r, int c) throws SQLException { return delegate.prepareStatement(s, r, c); }
        @Override public PreparedStatement prepareStatement(String s, int r, int c, int h) throws SQLException { return delegate.prepareStatement(s, r, c, h); }
        @Override public Statement createStatement() throws SQLException { return delegate.createStatement(); }
        @Override public Statement createStatement(int r, int c) throws SQLException { return delegate.createStatement(r, c); }
        @Override public Statement createStatement(int r, int c, int h) throws SQLException { return delegate.createStatement(r, c, h); }
        @Override public CallableStatement prepareCall(String s) throws SQLException { return delegate.prepareCall(s); }
        @Override public CallableStatement prepareCall(String s, int r, int c) throws SQLException { return delegate.prepareCall(s, r, c); }
        @Override public CallableStatement prepareCall(String s, int r, int c, int h) throws SQLException { return delegate.prepareCall(s, r, c, h); }
        @Override public String nativeSQL(String s) throws SQLException { return delegate.nativeSQL(s); }
        @Override public DatabaseMetaData getMetaData() throws SQLException { return delegate.getMetaData(); }
        @Override public void setReadOnly(boolean r) throws SQLException { delegate.setReadOnly(r); }
        @Override public boolean isReadOnly() throws SQLException { return delegate.isReadOnly(); }
        @Override public void setCatalog(String c) throws SQLException { delegate.setCatalog(c); }
        @Override public String getCatalog() throws SQLException { return delegate.getCatalog(); }
        @Override public void setTransactionIsolation(int l) throws SQLException { delegate.setTransactionIsolation(l); }
        @Override public int getTransactionIsolation() throws SQLException { return delegate.getTransactionIsolation(); }
        @Override public SQLWarning getWarnings() throws SQLException { return delegate.getWarnings(); }
        @Override public void clearWarnings() throws SQLException { delegate.clearWarnings(); }
        @Override public java.util.Map<String, Class<?>> getTypeMap() throws SQLException { return delegate.getTypeMap(); }
        @Override public void setTypeMap(java.util.Map<String, Class<?>> m) throws SQLException { delegate.setTypeMap(m); }
        @Override public void setHoldability(int h) throws SQLException { delegate.setHoldability(h); }
        @Override public int getHoldability() throws SQLException { return delegate.getHoldability(); }
        @Override public Savepoint setSavepoint() throws SQLException { return delegate.setSavepoint(); }
        @Override public Savepoint setSavepoint(String n) throws SQLException { return delegate.setSavepoint(n); }
        @Override public void rollback(Savepoint s) throws SQLException { delegate.rollback(s); }
        @Override public void releaseSavepoint(Savepoint s) throws SQLException { delegate.releaseSavepoint(s); }
        @Override public Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int t) throws SQLException { return delegate.isValid(t); }
        @Override public void setClientInfo(String n, String v) throws java.sql.SQLClientInfoException { try { delegate.setClientInfo(n, v); } catch (Exception e) {} }
        @Override public void setClientInfo(java.util.Properties p) throws java.sql.SQLClientInfoException { try { delegate.setClientInfo(p); } catch (Exception e) {} }
        @Override public String getClientInfo(String n) throws SQLException { return delegate.getClientInfo(n); }
        @Override public java.util.Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public Array createArrayOf(String t, Object[] e) throws SQLException { return delegate.createArrayOf(t, e); }
        @Override public Struct createStruct(String t, Object[] a) throws SQLException { return delegate.createStruct(t, a); }
        @Override public void setSchema(String s) throws SQLException { delegate.setSchema(s); }
        @Override public String getSchema() throws SQLException { return delegate.getSchema(); }
        @Override public void abort(java.util.concurrent.Executor e) throws SQLException { delegate.abort(e); }
        @Override public void setNetworkTimeout(java.util.concurrent.Executor e, int ms) throws SQLException { delegate.setNetworkTimeout(e, ms); }
        @Override public int getNetworkTimeout() throws SQLException { return delegate.getNetworkTimeout(); }
        @Override public <T> T unwrap(Class<T> i) throws SQLException { return delegate.unwrap(i); }
        @Override public boolean isWrapperFor(Class<?> i) throws SQLException { return delegate.isWrapperFor(i); }
    }
}
