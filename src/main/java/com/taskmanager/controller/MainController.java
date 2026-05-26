package com.taskmanager.controller;

import com.taskmanager.dao.TarefaDAO;
import com.taskmanager.model.Tarefa;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<Tarefa> tabelaTarefas;
    @FXML private TableColumn<Tarefa, Boolean>  colConcluida;
    @FXML private TableColumn<Tarefa, String>   colTitulo;
    @FXML private TableColumn<Tarefa, String>   colPrioridade;
    @FXML private TableColumn<Tarefa, String>   colData;

    @FXML private Button btnNovaTarefa;
    @FXML private Button btnEditar;
    @FXML private Button btnRemover;
    @FXML private Button btnConcluir;

    @FXML private Label lblStatus;
    @FXML private ComboBox<String> cbFiltro;

    private final TarefaDAO dao = new TarefaDAO();
    private final ObservableList<Tarefa> tarefasObservaveis = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarFiltro();
        carregarTarefas();
    }

    private void configurarColunas() {
        colConcluida.setCellValueFactory(cell ->
                new SimpleBooleanProperty(cell.getValue().isConcluida())
        );
        colConcluida.setCellFactory(CheckBoxTableCell.forTableColumn(colConcluida));

        // Coluna de título
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitulo())
        );

        colPrioridade.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPrioridade())
        );
        colPrioridade.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ALTA"  -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        case "MEDIA" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        case "BAIXA" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        default      -> setStyle("");
                    }
                }
            }
        });

        colData.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDataCriacao().toString())
        );

        tabelaTarefas.setItems(tarefasObservaveis);
    }

    private void configurarFiltro() {
        cbFiltro.setItems(FXCollections.observableArrayList("TODAS", "PENDENTES", "CONCLUÍDAS"));
        cbFiltro.setValue("TODAS");
        cbFiltro.setOnAction(e -> carregarTarefas());
    }

    private void carregarTarefas() {
        List<Tarefa> lista;

        switch (cbFiltro.getValue()) {
            case "PENDENTES"  -> lista = dao.listarPorStatus(false);
            case "CONCLUÍDAS" -> lista = dao.listarPorStatus(true);
            default           -> lista = dao.listarTodos();
        }

        tarefasObservaveis.setAll(lista);
        atualizarStatus();
    }

    private void atualizarStatus() {
        long total     = tarefasObservaveis.size();
        long concluidas = tarefasObservaveis.stream().filter(Tarefa::isConcluida).count();
        lblStatus.setText(String.format("Total: %d  |  Concluídas: %d  |  Pendentes: %d",
                total, concluidas, total - concluidas));
    }

    @FXML
    private void abrirNovaTarefa() {
        abrirFormulario(null);
    }

    @FXML
    private void editarTarefa() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Selecione uma tarefa para editar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(selecionada);
    }

    @FXML
    private void concluirTarefa() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Selecione uma tarefa para marcar como concluída.", Alert.AlertType.WARNING);
            return;
        }
        if (selecionada.isConcluida()) {
            mostrarAlerta("Essa tarefa já está concluída.", Alert.AlertType.INFORMATION);
            return;
        }
        dao.marcarComoConcluida(selecionada.getId());
        carregarTarefas();
    }

    @FXML
    private void removerTarefa() {
        Tarefa selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Selecione uma tarefa para remover.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Remover tarefa?");
        confirmacao.setContentText("Deseja realmente remover: \"" + selecionada.getTitulo() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            dao.remover(selecionada.getId());
            carregarTarefas();
        }
    }

    private void abrirFormulario(Tarefa tarefa) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/taskmanager/fxml/form-view.fxml")
            );

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/com/taskmanager/css/style.css")
                    ).toExternalForm()
            );

            FormController controller = loader.getController();
            controller.setTarefa(tarefa);
            controller.setAoSalvar(this::carregarTarefas);

            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, mensagem, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}