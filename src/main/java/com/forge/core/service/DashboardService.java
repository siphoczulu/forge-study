package com.forge.core.service;

import com.forge.core.dto.TodayPlanItem;
import com.forge.core.dto.WeeklyStatusItem;
import com.forge.model.Course;
import com.forge.model.Topic;
import com.forge.storage.ForgeData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardService {

    public List<TodayPlanItem> buildTodayPlan(ForgeData data) {
        List<TodayPlanItem> out = new ArrayList<>();

        for (Course course : data.getCourses()) {
            if (course.getTopics().isEmpty()) {
                continue;
            }

            Topic best = course.getTopics().stream()
                    .min(Comparator.comparing(
                            (Topic t) -> t.getLastStudied() == null ? LocalDate.MIN : t.getLastStudied()
                    ))
                    .orElse(null);

            // Note: LocalDate.MIN means "never studied" sorts as oldest -> wins.
            if (best == null) continue;

            String last = (best.getLastStudied() == null) ? "never" : best.getLastStudied().toString();

            out.add(new TodayPlanItem(
                    course.getId(),
                    course.getName(),
                    best.getId(),
                    best.getName(),
                    last
            ));
        }

        return out;
    }

    public List<WeeklyStatusItem> buildWeeklyStatus(ForgeData data) {
        List<WeeklyStatusItem> out = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        for (Course course : data.getCourses()) {
            boolean studiedThisWeek = data.getStudySessions().stream()
                    .anyMatch(s ->
                            s.getCourseId().equals(course.getId())
                                    && !s.getDate().isBefore(monday)
                                    && !s.getDate().isAfter(today)
                    );

            out.add(new WeeklyStatusItem(course.getId(), course.getName(), studiedThisWeek));
        }

        return out;
    }
}