package com.forge.fx;

import javafx.application.Application;
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

        var todayList = new javafx.scene.control.ListView<String>();

        if (today.isEmpty()) {
            todayList.getItems().add("(no data)");
        } else {
            for (var item : today) {
                todayList.getItems().add(
                        item.courseName() + " → " + item.topicName() + " (last: " + item.lastStudied() + ")"
                );
            }
        }

        var title = new javafx.scene.control.Label("FORGE DASHBOARD");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        var todayLabel = new javafx.scene.control.Label("What to study today");
        todayLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var weeklyLabel = new javafx.scene.control.Label("Weekly target");
        weeklyLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var weeklyList = new javafx.scene.control.ListView<String>();

        if (weekly.isEmpty()) {
            weeklyList.getItems().add("(no courses)");
        } else {
            for (var w : weekly) {
                weeklyList.getItems().add((w.studiedThisWeek() ? "✅ " : "❌ ") + w.courseName());
            }
        }

        var root = new javafx.scene.layout.VBox(
                12,
                title,
                todayLabel,
                todayList,
                weeklyLabel,
                weeklyList
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