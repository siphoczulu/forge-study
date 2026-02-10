package com.forge.fx;

import javafx.application.Application;
import javafx.stage.Stage;

public class ForgeFxApp extends Application {

    public record DeadlineRow(
            String dueDate,
            String course,
            String type,
            String title,
            String weight
    ) {}

    @Override
    public void start(Stage stage) {
        var store = new com.forge.storage.JsonStore("forge_data.json");
        var data = store.load();

        var dashboard = new com.forge.core.service.DashboardService();
        var today = dashboard.buildTodayPlan(data);
        var weekly = dashboard.buildWeeklyStatus(data);

        var root = new com.forge.fx.view.AppShell().build(data, today, weekly);

        var scene = new javafx.scene.Scene(root, 800, 600);
        stage.setTitle("Forge");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}