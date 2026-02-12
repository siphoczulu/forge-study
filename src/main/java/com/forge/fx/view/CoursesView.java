package com.forge.fx.view;

import com.forge.model.Course;
import com.forge.model.Topic;
import com.forge.storage.ForgeData;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CoursesView {

    public Parent build(ForgeData data) {
        // Left: courses
        var coursesList = new ListView<Course>();
        coursesList.getItems().setAll(data.getCourses());
        coursesList.setPrefWidth(220);

        // Render course names nicely
        coursesList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // Right: topics table
        var topicsTitle = new Label("Topics");
        topicsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        var topicsTable = new TableView<Topic>();

        var colName = new TableColumn<Topic, String>("Topic");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));

        var colLast = new TableColumn<Topic, String>("Last studied");
        colLast.setCellValueFactory(cell -> {
            var d = cell.getValue().getLastStudied();
            return new SimpleStringProperty(d == null ? "never" : d.toString());
        });

        topicsTable.getColumns().addAll(colName, colLast);
        topicsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // When a course is selected, show its topics
        coursesList.getSelectionModel().selectedItemProperty().addListener((obs, oldC, newC) -> {
            if (newC == null) {
                topicsTitle.setText("Topics");
                topicsTable.getItems().clear();
            } else {
                topicsTitle.setText("Topics — " + newC.getName());
                topicsTable.getItems().setAll(newC.getTopics());
            }
        });

        // Default selection if any
        if (!data.getCourses().isEmpty()) {
            coursesList.getSelectionModel().select(0);
        } else {
            topicsTable.getItems().clear();
        }

        var right = new VBox(8, topicsTitle, topicsTable);
        right.setStyle("-fx-padding: 10;");

        var root = new BorderPane();
        root.setLeft(coursesList);
        root.setCenter(right);
        root.setStyle("-fx-padding: 10;");

        return root;
    }
}
