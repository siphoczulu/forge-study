package com.forge.model;

import java.time.LocalDate;
import java.util.UUID;

public class StudySession {
    private String id;
    private LocalDate date;
    private String courseId;
    private String topicId;
    private int durationMinutes;
    private String notes;

    public StudySession() {
        // for JSON later
    }

    public StudySession(LocalDate date, String courseId, String topicId, int durationMinutes, String notes) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.courseId = courseId;
        this.topicId = topicId;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCourseId() { return courseId; }
    public String getTopicId() { return topicId; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getNotes() { return notes; }

    public void setId(String id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setNotes(String notes) { this.notes = notes; }
}