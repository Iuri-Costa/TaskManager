package com.taskmanager.app;

import com.taskmanager.db.Conexao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Conexao.inicializarBanco();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/taskmanager/fxml/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 650);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/taskmanager/css/style.css")
                ).toExternalForm()
        );

        stage.setTitle("TaskManager — Gerenciador de Tarefas");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}