package com.forge.fx.view;

import com.forge.model.Course;
import com.forge.model.StudySession;
import com.forge.model.Topic;
import com.forge.storage.ForgeData;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;

public class SessionHistoryView {

    public Parent build(ForgeData data) {
        var title = new Label("Study Session History");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

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

        table.getItems().setAll(
                data.getStudySessions().stream()
                        .sorted(java.util.Comparator.comparing(StudySession::getDate).reversed())
                        .toList()
        );

        var root = new VBox(10, title, table);
        root.setStyle("-fx-padding: 16;");
        return root;
    }
}