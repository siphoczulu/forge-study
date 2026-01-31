package com.forge.fx.view;

import com.forge.core.dto.TodayPlanItem;
import com.forge.core.dto.WeeklyStatusItem;
import com.forge.model.Deadline;
import com.forge.storage.ForgeData;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DashboardView {

    public record DeadlineRow(
            String dueDate,
            String course,
            String type,
            String title,
            String weight
    ) {}

    public Parent build(ForgeData data, List<TodayPlanItem> today, List<WeeklyStatusItem> weekly) {

        var title = new Label("FORGE DASHBOARD");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Today
        var todayLabel = new Label("What to study today");
        todayLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var todayList = new ListView<String>();
        if (today.isEmpty()) {
            todayList.getItems().add("(no data)");
        } else {
            for (var item : today) {
                todayList.getItems().add(item.courseName() + " → " + item.topicName() + " (last: " + item.lastStudied() + ")");
            }
        }

        // Weekly
        var weeklyLabel = new Label("Weekly target");
        weeklyLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var weeklyList = new ListView<String>();
        if (weekly.isEmpty()) {
            weeklyList.getItems().add("(no courses)");
        } else {
            for (var w : weekly) {
                weeklyList.getItems().add((w.studiedThisWeek() ? "✅ " : "❌ ") + w.courseName());
            }
        }

        // Deadlines
        var deadlinesLabel = new Label("Upcoming deadlines (next 14 days)");
        deadlinesLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var deadlineTable = buildDeadlineTable(data);

        var root = new VBox(
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

        return root;
    }

    private TableView<DeadlineRow> buildDeadlineTable(ForgeData data) {
        var courseNameById = new HashMap<String, String>();
        for (var c : data.getCourses()) {
            courseNameById.put(c.getId(), c.getName());
        }

        LocalDate todayDate = LocalDate.now();
        LocalDate cutoff = todayDate.plusDays(14);

        var table = new TableView<DeadlineRow>();

        var colDue = new TableColumn<DeadlineRow, String>("Due");
        colDue.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().dueDate()));

        var colCourse = new TableColumn<DeadlineRow, String>("Course");
        colCourse.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().course()));

        var colType = new TableColumn<DeadlineRow, String>("Type");
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().type()));

        var colTitle = new TableColumn<DeadlineRow, String>("Title");
        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().title()));

        var colWeight = new TableColumn<DeadlineRow, String>("Weight");
        colWeight.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().weight()));

        table.getColumns().addAll(colDue, colCourse, colType, colTitle, colWeight);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        var upcoming = data.getDeadlines().stream()
                .filter(d -> !d.getDueDate().isBefore(todayDate) && !d.getDueDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Deadline::getDueDate))
                .toList();

        if (upcoming.isEmpty()) {
            table.getItems().add(new DeadlineRow("-", "-", "-", "No deadlines in next 14 days", "-"));
        } else {
            for (var d : upcoming) {
                String courseName = courseNameById.getOrDefault(d.getCourseId(), "(unknown)");
                String weight = (d.getWeight() == null) ? "-" : d.getWeight().toString();
                table.getItems().add(new DeadlineRow(
                        d.getDueDate().toString(),
                        courseName,
                        d.getType().toString(),
                        d.getTitle(),
                        weight
                ));
            }
        }

        return table;
    }
}