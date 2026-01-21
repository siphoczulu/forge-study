package com.forge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Course {
    private String id;
    private String name;
    private final List<Topic> topics = new ArrayList<>();

    public Course() {
    }

    public Course(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Topic> getTopics() { return topics; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }
}