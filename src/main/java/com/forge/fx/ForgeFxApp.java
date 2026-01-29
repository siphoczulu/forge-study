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

        var title = new javafx.scene.control.Label("FORGE DASHBOARD");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        var todayLabel = new javafx.scene.control.Label("What to study today");
        todayLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var todayText = new javafx.scene.control.Label(
                today.isEmpty() ? "(no data)" :
                        today.stream()
                                .map(i -> "• " + i.courseName() + " → " + i.topicName())
                                .reduce("", (a, b) -> a + b + "\n")
        );
        todayText.setStyle("-fx-font-family: monospace;");

        var weeklyLabel = new javafx.scene.control.Label("Weekly target");
        weeklyLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var weeklyText = new javafx.scene.control.Label(
                weekly.isEmpty() ? "(no courses)" :
                        weekly.stream()
                                .map(w -> (w.studiedThisWeek() ? "✅ " : "❌ ") + w.courseName())
                                .reduce("", (a, b) -> a + b + "\n")
        );
        weeklyText.setStyle("-fx-font-family: monospace;");

        var root = new javafx.scene.layout.VBox(
                12,
                title,
                todayLabel,
                todayText,
                weeklyLabel,
                weeklyText
        );
        root.setStyle("-fx-padding: 16;");

        var scene = new javafx.scene.Scene(root, 640, 480);
        stage.setTitle("Forge");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}