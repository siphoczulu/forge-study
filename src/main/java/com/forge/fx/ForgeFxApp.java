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

        var deadlinesLabel = new javafx.scene.control.Label("Upcoming deadlines (next 14 days)");
        deadlinesLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        if (weekly.isEmpty()) {
            weeklyList.getItems().add("(no courses)");
        } else {
            for (var w : weekly) {
                weeklyList.getItems().add((w.studiedThisWeek() ? "✅ " : "❌ ") + w.courseName());
            }
        }

        // --- Upcoming deadlines table (next 14 days) ---
        var courseNameById = new java.util.HashMap<String, String>();
        for (var c : data.getCourses()) {
            courseNameById.put(c.getId(), c.getName());
        }

        var todayDate = java.time.LocalDate.now();
        var cutoff = todayDate.plusDays(14);

        var deadlineTable = new javafx.scene.control.TableView<DeadlineRow>();

        var colDue = new javafx.scene.control.TableColumn<DeadlineRow, String>("Due");
        colDue.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().dueDate()));

        var colCourse = new javafx.scene.control.TableColumn<DeadlineRow, String>("Course");
        colCourse.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().course()));

        var colType = new javafx.scene.control.TableColumn<DeadlineRow, String>("Type");
        colType.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().type()));

        var colTitle = new javafx.scene.control.TableColumn<DeadlineRow, String>("Title");
        colTitle.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().title()));

        var colWeight = new javafx.scene.control.TableColumn<DeadlineRow, String>("Weight");
        colWeight.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().weight()));

        deadlineTable.getColumns().addAll(colDue, colCourse, colType, colTitle, colWeight);
        deadlineTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

        var upcoming = data.getDeadlines().stream()
                .filter(d -> !d.getDueDate().isBefore(todayDate) && !d.getDueDate().isAfter(cutoff))
                .sorted(java.util.Comparator.comparing(com.forge.model.Deadline::getDueDate))
                .toList();

        if (upcoming.isEmpty()) {
            deadlineTable.getItems().add(new DeadlineRow("-", "-", "-", "No deadlines in next 14 days", "-"));
        } else {
            for (var d : upcoming) {
                String courseName = courseNameById.getOrDefault(d.getCourseId(), "(unknown)");
                String weight = (d.getWeight() == null) ? "-" : d.getWeight().toString();
                deadlineTable.getItems().add(new DeadlineRow(
                        d.getDueDate().toString(),
                        courseName,
                        d.getType().toString(),
                        d.getTitle(),
                        weight
                ));
            }
        }

        var root = new javafx.scene.layout.VBox(
                12,
                title,
                todayLabel,
                todayList,
                weeklyLabel,
                weeklyList,
                deadlinesLabel,
                deadlineTable
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