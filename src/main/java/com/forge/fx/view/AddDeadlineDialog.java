package com.forge.fx.view;

import com.forge.model.Course;
import com.forge.model.Deadline;
import com.forge.storage.ForgeData;
import com.forge.storage.JsonStore;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class AddDeadlineDialog {

    public static boolean show(ForgeData data) {
        var dialog = new Dialog<ButtonType>();
        dialog.setTitle("Add Deadline");
        dialog.setHeaderText("Create a new deadline");

        var courseBox = new ComboBox<Course>();
        courseBox.setItems(FXCollections.observableArrayList(data.getCourses()));
        courseBox.setPrefWidth(240);
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

        var typeBox = new ComboBox<Deadline.Type>();
        typeBox.setItems(FXCollections.observableArrayList(Deadline.Type.values()));
        typeBox.getSelectionModel().selectFirst();

        var titleField = new TextField();
        titleField.setPromptText("e.g. Quiz 1");

        var dueDatePicker = new DatePicker(LocalDate.now());

        var weightField = new TextField();
        weightField.setPromptText("optional");

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseBox, 1, 0);

        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(new Label("Title:"), 0, 2);
        grid.add(titleField, 1, 2);

        grid.add(new Label("Due date:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);

        grid.add(new Label("Weight:"), 0, 4);
        grid.add(weightField, 1, 4);

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
        var selectedType = typeBox.getValue();
        var title = titleField.getText() == null ? "" : titleField.getText().trim();
        var dueDate = dueDatePicker.getValue();

        if (selectedCourse == null || selectedType == null || title.isEmpty() || dueDate == null) {
            return false;
        }

        Double weight = null;
        var weightRaw = weightField.getText() == null ? "" : weightField.getText().trim();
        if (!weightRaw.isEmpty()) {
            try {
                weight = Double.parseDouble(weightRaw);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        var deadline = new Deadline(
                selectedType,
                selectedCourse.getId(),
                title,
                dueDate,
                weight
        );

        data.getDeadlines().add(deadline);
        new JsonStore("forge_data.json").save(data);
        return true;
    }
}