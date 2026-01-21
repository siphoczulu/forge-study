package com.forge.core.dto;

public record WeeklyStatusItem(
        String courseId,
        String courseName,
        boolean studiedThisWeek
) {}