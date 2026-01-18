package com.forge.storage;

import com.forge.model.Course;
import com.forge.model.Deadline;
import com.forge.model.StudySession;

import java.util.ArrayList;
import java.util.List;

public class ForgeData {
    private List<Course> courses = new ArrayList<>();
    private List<StudySession> studySessions = new ArrayList<>();
    private List<Deadline> deadlines = new ArrayList<>();

    public ForgeData() {}

    public List<Course> getCourses() { return courses; }
    public List<StudySession> getStudySessions() { return studySessions; }
    public List<Deadline> getDeadlines() { return deadlines; }

    public void setCourses(List<Course> courses) { this.courses = courses; }
    public void setStudySessions(List<StudySession> studySessions) { this.studySessions = studySessions; }
    public void setDeadlines(List<Deadline> deadlines) { this.deadlines = deadlines; }
}