package com.forge.core.dto;

public record TodayPlanItem(
        String courseId,
        String courseName,
        String topicId,
        String topicName,
        String lastStudied // "never" or ISO date string
) {}