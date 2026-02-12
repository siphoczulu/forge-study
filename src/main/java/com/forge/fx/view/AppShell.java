package com.forge.fx.view;

import com.forge.core.dto.TodayPlanItem;
import com.forge.core.dto.WeeklyStatusItem;
import com.forge.storage.ForgeData;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class AppShell {

    private final BorderPane root = new BorderPane();

    public Parent build(ForgeData data,
                        List<TodayPlanItem> today,
                        List<WeeklyStatusItem> weekly) {

        // Top nav
        Button dashboardBtn = new Button("Dashboard");
        Button coursesBtn = new Button("Courses");
        Button deadlinesBtn = new Button("Deadlines");

        HBox nav = new HBox(8, dashboardBtn, coursesBtn, deadlinesBtn);
        nav.setStyle("-fx-padding: 10; -fx-background-color: #f3f3f3;");
        root.setTop(nav);

        // Center views
        Parent dashboardView = new DashboardView().build(data, today, weekly);
        Parent coursesView = new CoursesView().build(data);
        Parent deadlinesPlaceholder = new StackPane(new Label("Deadlines (coming next)"));

        // Default view
        root.setCenter(dashboardView);

        // Nav actions
        dashboardBtn.setOnAction(e -> root.setCenter(dashboardView));
        coursesBtn.setOnAction(e -> root.setCenter(coursesView));
        deadlinesBtn.setOnAction(e -> root.setCenter(deadlinesPlaceholder));

        return root;
    }
}