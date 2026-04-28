package com.forge.fx.view;

import com.forge.model.Course;
import com.forge.model.StudySession;
import com.forge.model.Topic;
import com.forge.storage.ForgeData;
import com.forge.storage.JsonStore;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class LogSessionDialog {

    public static boolean show(ForgeData data) {
        var dialog = new Dialog<ButtonType>();
        dialog.setTitle("Log Study Session");
        dialog.setHeaderText("Log a study session");

        var courseBox = new ComboBox<Course>();
        courseBox.setItems(FXCollections.observableArrayList(data.getCourses()));
        courseBox.setPrefWidth(250);
        courseBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        courseBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        var topicBox = new ComboBox<Topic>();
        topicBox.setPrefWidth(250);
        topicBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        topicBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        courseBox.getSelectionModel().selectedItemProperty().addListener((obs, oldC, newC) -> {
            if (newC == null) {
                topicBox.getItems().clear();
            } else {
                topicBox.setItems(FXCollections.observableArrayList(newC.getTopics()));
                if (!newC.getTopics().isEmpty()) {
                    topicBox.getSelectionModel().selectFirst();
                }
            }
        });

        var datePicker = new DatePicker(LocalDate.now());

        var minutesField = new TextField();
        minutesField.setPromptText("e.g. 45");

        var notesArea = new TextArea();
        notesArea.setPromptText("Optional notes");
        notesArea.setPrefRowCount(4);

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseBox, 1, 0);

        grid.add(new Label("Topic:"), 0, 1);
        grid.add(topicBox, 1, 1);

        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        grid.add(new Label("Minutes:"), 0, 3);
        grid.add(minutesField, 1, 3);

        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (!data.getCourses().isEmpty()) {
            courseBox.getSelectionModel().selectFirst();
        }

        var result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return false;
        }

        var selectedCourse = courseBox.getValue();
        var selectedTopic = topicBox.getValue();
        var selectedDate = datePicker.getValue();
        var notes = notesArea.getText() == null ? "" : notesArea.getText().trim();

        if (selectedCourse == null || selectedTopic == null || selectedDate == null) {
            return false;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(minutesField.getText().trim());
            if (minutes <= 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        var session = new StudySession(
                selectedDate,
                selectedCourse.getId(),
                selectedTopic.getId(),
                minutes,
                notes
        );

        data.getStudySessions().add(session);

// Keep lastStudied correct even if user logs an older/backdated session
        var currentLast = selectedTopic.getLastStudied();
        if (currentLast == null || selectedDate.isAfter(currentLast)) {
            selectedTopic.setLastStudied(selectedDate);
        }

        new JsonStore("forge_data.json").save(data);
        return true;
    }
}