package com.forge.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ForgeFxApp extends Application {

    @Override
    public void start(Stage stage) {
        // Load data
        var store = new com.forge.storage.JsonStore("forge_data.json");
        var data = store.load();

        // Compute dashboard
        var dashboard = new com.forge.core.service.DashboardService();
        var today = dashboard.buildTodayPlan(data);
        var weekly = dashboard.buildWeeklyStatus(data);

        StringBuilder text = new StringBuilder();
        text.append("FORGE DASHBOARD\n\n");

        text.append("What to study today:\n");
        if (today.isEmpty()) {
            text.append("  (no data)\n");
        } else {
            for (var item : today) {
                text.append("  • ")
                        .append(item.courseName())
                        .append(" → ")
                        .append(item.topicName())
                        .append(" (last: ")
                        .append(item.lastStudied())
                        .append(")\n");
            }
        }

        text.append("\nWeekly target:\n");
        if (weekly.isEmpty()) {
            text.append("  (no courses)\n");
        } else {
            for (var w : weekly) {
                text.append("  ")
                        .append(w.studiedThisWeek() ? "✅ " : "❌ ")
                        .append(w.courseName())
                        .append("\n");
            }
        }

        var label = new javafx.scene.control.Label(text.toString());
        label.setStyle("-fx-font-family: monospace; -fx-padding: 16;");

        var scene = new javafx.scene.Scene(new javafx.scene.layout.StackPane(label), 600, 420);
        stage.setTitle("Forge — Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}