package com.forge.model;

import java.time.LocalDate;
import java.util.UUID;

public class Deadline {
    public enum Type { ASSIGNMENT, QUIZ, EXAM }

    private String id;
    private Type type;
    private String courseId;
    private String title;
    private LocalDate dueDate;
    private Double weight; // nullable

    public Deadline() {
    }

    public Deadline(Type type, String courseId, String title, LocalDate dueDate, Double weight) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.weight = weight;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public Double getWeight() { return weight; }

    public void setId(String id) { this.id = id; }
    public void setType(Type type) { this.type = type; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setWeight(Double weight) { this.weight = weight; }
}