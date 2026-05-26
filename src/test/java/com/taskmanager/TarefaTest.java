package com.taskmanager;

import com.taskmanager.model.Tarefa;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TarefaTest {

    private Tarefa tarefa;

    @BeforeEach
    void setUp() {
        tarefa = new Tarefa("Estudar JavaFX", "Revisar layouts e controllers", "ALTA");
    }

    @AfterEach
    void tearDown() {
        tarefa = null;
    }


    @Test
    @DisplayName("Deve criar tarefa com título, descrição e prioridade")
    void deveCriarTarefaComCamposCorretos() {
        assertEquals("Estudar JavaFX", tarefa.getTitulo());
        assertEquals("Revisar layouts e controllers", tarefa.getDescricao());
        assertEquals("ALTA", tarefa.getPrioridade());
    }

    @Test
    @DisplayName("Tarefa recém-criada não deve estar concluída")
    void tarefaNovaDeveEstarPendente() {
        assertFalse(tarefa.isConcluida());
    }

    @Test
    @DisplayName("Tarefa recém-criada deve ter data de criação preenchida")
    void tarefaNovaDeveTermDataCriacao() {
        assertNotNull(tarefa.getDataCriacao());
    }

    @Test
    @DisplayName("Construtor padrão deve inicializar com concluida=false")
    void construtorPadraoDeveInicializarConcluida() {
        Tarefa t = new Tarefa();
        assertFalse(t.isConcluida());
        assertNotNull(t.getDataCriacao());
    }


    @Test
    @DisplayName("Deve marcar tarefa como concluída ao chamar concluir()")
    void deveConcluirTarefa() {
        tarefa.concluir();
        assertTrue(tarefa.isConcluida());
    }

    @Test
    @DisplayName("Deve marcar como concluída via setConcluida(true)")
    void deveMarcarConcluidaViaSetter() {
        tarefa.setConcluida(true);
        assertTrue(tarefa.isConcluida());
    }

    @Test
    @DisplayName("Deve desmarcar conclusão via setConcluida(false)")
    void deveDesmarcaConcluidaViaSetter() {
        tarefa.concluir();
        tarefa.setConcluida(false);
        assertFalse(tarefa.isConcluida());
    }


    @Test
    @DisplayName("Tarefa com título e prioridade preenchidos deve ser válida")
    void tarefaCompletoDeveSerValida() {
        assertTrue(tarefa.isValida());
    }

    @Test
    @DisplayName("Tarefa sem título deve ser inválida")
    void tarefaSemTituloDeveSerInvalida() {
        tarefa.setTitulo(null);
        assertFalse(tarefa.isValida());
    }

    @Test
    @DisplayName("Tarefa com título em branco deve ser inválida")
    void tarefaComTituloEmBrancoDeveSerInvalida() {
        tarefa.setTitulo("   ");
        assertFalse(tarefa.isValida());
    }

    @Test
    @DisplayName("Tarefa sem prioridade deve ser inválida")
    void tarefaSemPrioridadeDeveSerInvalida() {
        tarefa.setPrioridade(null);
        assertFalse(tarefa.isValida());
    }

    @Test
    @DisplayName("Tarefa com prioridade em branco deve ser inválida")
    void tarefaComPrioridadeEmBrancoDeveSerInvalida() {
        tarefa.setPrioridade("");
        assertFalse(tarefa.isValida());
    }

    @Test
    @DisplayName("Deve atualizar o título via setter")
    void deveAtualizarTitulo() {
        tarefa.setTitulo("Novo título");
        assertEquals("Novo título", tarefa.getTitulo());
    }

    @Test
    @DisplayName("Deve atualizar a prioridade via setter")
    void deveAtualizarPrioridade() {
        tarefa.setPrioridade("BAIXA");
        assertEquals("BAIXA", tarefa.getPrioridade());
    }

    @Test
    @DisplayName("toString deve conter o título e a prioridade")
    void toStringDeveConterCamposPrincipais() {
        String resultado = tarefa.toString();
        assertTrue(resultado.contains("Estudar JavaFX"));
        assertTrue(resultado.contains("ALTA"));
    }
}
