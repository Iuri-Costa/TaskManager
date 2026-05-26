package com.taskmanager;

import com.taskmanager.generic.RepositorioGenerico;
import com.taskmanager.model.Tarefa;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioGenericoTest {

    private RepositorioGenerico<Tarefa> repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioGenerico<>();
    }

    @AfterEach
    void tearDown() {
        repositorio = null;
    }

    @Test
    @DisplayName("Repositório vazio deve ter tamanho zero")
    void repositorioVazioDeveTerTamanhoZero() {
        assertEquals(0, repositorio.tamanho());
    }

    @Test
    @DisplayName("Deve adicionar item e aumentar o tamanho")
    void deveAdicionarItemEAumentarTamanho() {
        repositorio.adicionar(new Tarefa("T1", "", "ALTA"));
        assertEquals(1, repositorio.tamanho());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar item nulo")
    void deveLancarExcecaoAoAdicionarNulo() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.adicionar(null));
    }

    @Test
    @DisplayName("listarTodos deve retornar todos os itens adicionados")
    void listarTodosDeveRetornarTodosOsItens() {
        repositorio.adicionar(new Tarefa("T1", "", "ALTA"));
        repositorio.adicionar(new Tarefa("T2", "", "MEDIA"));
        repositorio.adicionar(new Tarefa("T3", "", "BAIXA"));

        List<Tarefa> lista = repositorio.listarTodos();
        assertEquals(3, lista.size());
    }

    @Test
    @DisplayName("listarTodos deve retornar cópia independente da lista interna")
    void listarTodosDeveRetornarCopiaIndependente() {
        repositorio.adicionar(new Tarefa("T1", "", "ALTA"));

        List<Tarefa> lista = repositorio.listarTodos();
        lista.clear();
        assertEquals(1, repositorio.tamanho());
    }

    @Test
    @DisplayName("Deve remover item existente")
    void deveRemoverItem() {
        Tarefa t = new Tarefa("Para remover", "", "BAIXA");
        repositorio.adicionar(t);
        repositorio.remover(t);
        assertEquals(0, repositorio.tamanho());
    }

    @Test
    @DisplayName("filtrar deve retornar somente tarefas pendentes")
    void filtrarDeveRetornarSomentePendentes() {
        Tarefa pendente = new Tarefa("Pendente", "", "ALTA");
        Tarefa concluida = new Tarefa("Concluída", "", "MEDIA");
        concluida.concluir();

        repositorio.adicionar(pendente);
        repositorio.adicionar(concluida);

        List<Tarefa> pendentes = repositorio.filtrar(t -> !t.isConcluida());
        assertEquals(1, pendentes.size());
        assertFalse(pendentes.get(0).isConcluida());
    }

    @Test
    @DisplayName("contar deve retornar quantidade de tarefas com prioridade ALTA")
    void contarDeveRetornarQuantidadeCorreta() {
        repositorio.adicionar(new Tarefa("T1", "", "ALTA"));
        repositorio.adicionar(new Tarefa("T2", "", "ALTA"));
        repositorio.adicionar(new Tarefa("T3", "", "BAIXA"));

        long total = repositorio.contar(t -> t.getPrioridade().equals("ALTA"));
        assertEquals(2, total);
    }

    @Test
    @DisplayName("copiar deve transferir itens de uma lista para outra")
    void copiarDeveTransferirItens() {
        List<Tarefa> origem = List.of(
                new Tarefa("T1", "", "ALTA"),
                new Tarefa("T2", "", "MEDIA")
        );
        List<Tarefa> destino = new ArrayList<>();

        RepositorioGenerico.copiar(origem, destino);

        assertEquals(2, destino.size());
    }
}
