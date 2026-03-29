package com.forge.fx.view;

import com.forge.core.dto.TodayPlanItem;
import com.forge.core.dto.WeeklyStatusItem;
import com.forge.core.service.DashboardService;
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

    private Parent buildFreshDashboard(ForgeData data) {
        var dashboardService = new DashboardService();
        var today = dashboardService.buildTodayPlan(data);
        var weekly = dashboardService.buildWeeklyStatus(data);
        return new DashboardView().build(data, today, weekly);
    }

    public Parent build(ForgeData data,
                        List<TodayPlanItem> today,
                        List<WeeklyStatusItem> weekly) {

        // Top nav
        Button dashboardBtn = new Button("Dashboard");
        Button coursesBtn = new Button("Courses");
        Button deadlinesBtn = new Button("Deadlines");
        Button logSessionBtn = new Button("Log Session");

        HBox nav = new HBox(8, dashboardBtn, coursesBtn, deadlinesBtn, logSessionBtn);
        nav.setStyle("-fx-padding: 10; -fx-background-color: #f3f3f3;");
        root.setTop(nav);

        // Other views
        Parent coursesView = new CoursesView().build(data);
        Parent deadlinesPlaceholder = new StackPane(new Label("Deadlines (coming next)"));

        // Default view
        root.setCenter(buildFreshDashboard(data));

        // Nav actions
        dashboardBtn.setOnAction(e -> root.setCenter(buildFreshDashboard(data)));
        coursesBtn.setOnAction(e -> root.setCenter(coursesView));
        deadlinesBtn.setOnAction(e -> root.setCenter(deadlinesPlaceholder));
        logSessionBtn.setOnAction(e -> {
            var placeholder = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            placeholder.setTitle("Log Session");
            placeholder.setHeaderText("Next step");
            placeholder.setContentText("This button will open the study session dialog next.");
            placeholder.showAndWait();
        });

        return root;
    }
}