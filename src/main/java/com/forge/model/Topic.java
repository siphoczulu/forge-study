package com.forge.model;

import java.time.LocalDate;
import java.util.UUID;

public class Topic {
    private String id;
    private String name;
    private LocalDate lastStudied; // null = never studied

    public Topic() {
        // for JSON later
    }

    public Topic(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.lastStudied = null;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getLastStudied() { return lastStudied; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLastStudied(LocalDate lastStudied) { this.lastStudied = lastStudied; }
}