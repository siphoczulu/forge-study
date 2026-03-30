package com.forge.fx.view;

import com.forge.model.Deadline;
import com.forge.storage.ForgeData;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;

public class DeadlinesView {

    public Parent build(ForgeData data) {
        var title = new Label("Deadlines");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        var addDeadlineBtn = new Button("Add Deadline");

        var table = new TableView<Deadline>();

        var colDue = new TableColumn<Deadline, String>("Due");
        colDue.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDueDate().toString())
        );

        var colCourse = new TableColumn<Deadline, String>("Course");
        var courseNameById = new HashMap<String, String>();
        for (var c : data.getCourses()) {
            courseNameById.put(c.getId(), c.getName());
        }
        colCourse.setCellValueFactory(cell ->
                new SimpleStringProperty(courseNameById.getOrDefault(cell.getValue().getCourseId(), "(unknown)"))
        );

        var colType = new TableColumn<Deadline, String>("Type");
        colType.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getType().toString())
        );

        var colTitle = new TableColumn<Deadline, String>("Title");
        colTitle.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle())
        );

        var colWeight = new TableColumn<Deadline, String>("Weight");
        colWeight.setCellValueFactory(cell -> {
            var w = cell.getValue().getWeight();
            return new SimpleStringProperty(w == null ? "-" : w.toString());
        });

        table.getColumns().addAll(colDue, colCourse, colType, colTitle, colWeight);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Runnable refreshTable = () -> {
            table.getItems().setAll(
                    data.getDeadlines().stream()
                            .sorted(java.util.Comparator.comparing(Deadline::getDueDate))
                            .toList()
            );
        };

        refreshTable.run();

        addDeadlineBtn.setOnAction(e -> {
            boolean saved = AddDeadlineDialog.show(data);
            if (saved) {
                refreshTable.run();
            }
        });

        var root = new VBox(10, title, addDeadlineBtn, table);
        root.setStyle("-fx-padding: 16;");
        return root;
    }
}