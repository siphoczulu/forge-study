package com.forge.fx.view;

import com.forge.model.Course;
import com.forge.model.StudySession;
import com.forge.model.Topic;
import com.forge.storage.ForgeData;
import com.forge.storage.JsonStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class SessionHistoryView {

    public Parent build(ForgeData data) {
        var title = new Label("Study Session History");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        var courseFilter = new ComboBox<Course>();
        courseFilter.setPromptText("Filter by course");
        courseFilter.setPrefWidth(240);

        javafx.collections.ObservableList<Course> allCourses = FXCollections.observableArrayList();
        allCourses.add(null); // represents "All courses"
        allCourses.addAll(data.getCourses());
        courseFilter.setItems(allCourses);

        courseFilter.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else if (item == null) {
                    setText("All courses");
                } else {
                    setText(item.getName());
                }
            }
        });

        courseFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else if (item == null) {
                    setText("All courses");
                } else {
                    setText(item.getName());
                }
            }
        });

        var deleteBtn = new Button("Delete Selected");

        var table = new TableView<StudySession>();

        var courseNameById = new HashMap<String, String>();
        var topicNameById = new HashMap<String, String>();

        for (Course c : data.getCourses()) {
            courseNameById.put(c.getId(), c.getName());
            for (Topic t : c.getTopics()) {
                topicNameById.put(t.getId(), t.getName());
            }
        }

        var colDate = new TableColumn<StudySession, String>("Date");
        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().toString())
        );

        var colCourse = new TableColumn<StudySession, String>("Course");
        colCourse.setCellValueFactory(cell ->
                new SimpleStringProperty(courseNameById.getOrDefault(cell.getValue().getCourseId(), "(unknown)"))
        );

        var colTopic = new TableColumn<StudySession, String>("Topic");
        colTopic.setCellValueFactory(cell ->
                new SimpleStringProperty(topicNameById.getOrDefault(cell.getValue().getTopicId(), "(unknown)"))
        );

        var colMinutes = new TableColumn<StudySession, String>("Minutes");
        colMinutes.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getDurationMinutes()))
        );

        var colNotes = new TableColumn<StudySession, String>("Notes");
        colNotes.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getNotes() == null || cell.getValue().getNotes().isBlank()
                                ? "-"
                                : cell.getValue().getNotes()
                )
        );

        table.getColumns().addAll(colDate, colCourse, colTopic, colMinutes, colNotes);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Runnable refreshTable = () -> {
            Course selectedCourse = courseFilter.getValue();

            List<StudySession> sessions = data.getStudySessions().stream()
                    .filter(s -> selectedCourse == null || s.getCourseId().equals(selectedCourse.getId()))
                    .sorted(java.util.Comparator.comparing(StudySession::getDate).reversed())
                    .toList();

            table.getItems().setAll(sessions);
        };

        courseFilter.setOnAction(e -> refreshTable.run());

        refreshTable.run();

        deleteBtn.setOnAction(e -> {
            var selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            data.getStudySessions().remove(selected);
            recalculateTopicLastStudied(data, selected.getTopicId());
            new JsonStore("forge_data.json").save(data);
            refreshTable.run();
        });

        var root = new VBox(10, title, courseFilter, deleteBtn, table);
        root.setStyle("-fx-padding: 16;");
        return root;
    }

    private void recalculateTopicLastStudied(ForgeData data, String topicId) {
        LocalDate latest = data.getStudySessions().stream()
                .filter(s -> s.getTopicId().equals(topicId))
                .map(StudySession::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        for (Course course : data.getCourses()) {
            for (Topic topic : course.getTopics()) {
                if (topic.getId().equals(topicId)) {
                    topic.setLastStudied(latest);
                    return;
                }
            }
        }
    }
}