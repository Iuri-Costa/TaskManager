package com.taskmanager.controller;

import com.taskmanager.dao.TarefaDAO;
import com.taskmanager.model.Tarefa;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FormController implements Initializable {

    @FXML private TextField   txtTitulo;
    @FXML private TextArea    txtDescricao;
    @FXML private ComboBox<String> cbPrioridade;
    @FXML private CheckBox    chkConcluida;
    @FXML private Label       lblErro;
    @FXML private Button      btnSalvar;
    @FXML private Button      btnCancelar;

    private Tarefa tarefa;
    private Runnable aoSalvar;
    private final TarefaDAO dao = new TarefaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbPrioridade.setItems(FXCollections.observableArrayList("ALTA", "MEDIA", "BAIXA"));
        cbPrioridade.setValue("MEDIA");
        lblErro.setVisible(false);
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            cbPrioridade.setValue(tarefa.getPrioridade());
            chkConcluida.setSelected(tarefa.isConcluida());
        }
    }

    public void setAoSalvar(Runnable aoSalvar) {
        this.aoSalvar = aoSalvar;
    }

    @FXML
    private void salvar() {
        String titulo = txtTitulo.getText().trim();
        String descricao = txtDescricao.getText().trim();
        String prioridade = cbPrioridade.getValue();

        if (titulo.isEmpty()) {
            lblErro.setText("O título é obrigatório.");
            lblErro.setVisible(true);
            return;
        }

        lblErro.setVisible(false);

        if (tarefa == null) {
            Tarefa nova = new Tarefa(titulo, descricao, prioridade);
            nova.setConcluida(chkConcluida.isSelected());
            dao.adicionar(nova);
        } else {
            tarefa.setTitulo(titulo);
            tarefa.setDescricao(descricao);
            tarefa.setPrioridade(prioridade);
            tarefa.setConcluida(chkConcluida.isSelected());
            dao.atualizar(tarefa);
        }

        if (aoSalvar != null) aoSalvar.run();
        fechar();
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}