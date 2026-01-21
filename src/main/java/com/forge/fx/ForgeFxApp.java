package com.forge.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ForgeFxApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello Forge (JavaFX) — v0.2 boot OK");
        Scene scene = new Scene(new StackPane(label), 520, 260);

        stage.setTitle("Forge");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}