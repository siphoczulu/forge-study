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
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

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

        var addCourseBtn = new Button("Add Course");
        var deleteCourseBtn = new Button("Delete Selected");
        var addTopicBtn = new Button("Add Topic");
        var deleteTopicBtn = new Button("Delete Topic");

        addCourseBtn.setOnAction(e -> {
            var dialog = new TextInputDialog();
            dialog.setTitle("Add Course");
            dialog.setHeaderText("Create a new course");
            dialog.setContentText("Course name:");

            var result = dialog.showAndWait();

            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var newCourse = new Course(name.trim());
                    data.getCourses().add(newCourse);
                    coursesList.getItems().add(newCourse);

                    // 🔥 Save immediately
                    new com.forge.storage.JsonStore("forge_data.json").save(data);
                }
            });
        });
        deleteCourseBtn.setOnAction(e -> {
            var selected = coursesList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            data.getCourses().remove(selected);
            coursesList.getItems().remove(selected);

            if (data.getCourses().isEmpty()) {
                topicsTitle.setText("Topics");
                topicsTable.getItems().clear();
            } else {
                coursesList.getSelectionModel().selectFirst();
            }

            new com.forge.storage.JsonStore("forge_data.json").save(data);
        });
        addTopicBtn.setOnAction(e -> {
            var selectedCourse = coursesList.getSelectionModel().getSelectedItem();
            if (selectedCourse == null) {
                return;
            }

            var dialog = new TextInputDialog();
            dialog.setTitle("Add Topic");
            dialog.setHeaderText("Add topic to " + selectedCourse.getName());
            dialog.setContentText("Topic name:");

            var result = dialog.showAndWait();

            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var newTopic = new Topic(name.trim());
                    selectedCourse.addTopic(newTopic);

                    topicsTable.getItems().setAll(selectedCourse.getTopics());

                    new com.forge.storage.JsonStore("forge_data.json").save(data);
                }
            });
        });
        deleteTopicBtn.setOnAction(e -> {
            var selectedCourse = coursesList.getSelectionModel().getSelectedItem();
            var selectedTopic = topicsTable.getSelectionModel().getSelectedItem();

            if (selectedCourse == null || selectedTopic == null) {
                return;
            }

            selectedCourse.getTopics().remove(selectedTopic);
            topicsTable.getItems().setAll(selectedCourse.getTopics());

            new com.forge.storage.JsonStore("forge_data.json").save(data);
        });

        var root = new BorderPane();
        var leftBox = new VBox(8, addCourseBtn, deleteCourseBtn, addTopicBtn, deleteTopicBtn, coursesList);
        leftBox.setStyle("-fx-padding: 10;");
        root.setLeft(leftBox);
        root.setCenter(right);
        root.setStyle("-fx-padding: 10;");

        return root;
    }
}
