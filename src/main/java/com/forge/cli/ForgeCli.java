package com.forge.cli;

import com.forge.model.Course;
import com.forge.storage.ForgeData;

import java.util.Scanner;

public class ForgeCli {
    private final ForgeData data;
    private final Scanner scanner = new Scanner(System.in);

    public ForgeCli(ForgeData data) {
        this.data = data;
    }

    public void run() {
        while (true) {
            System.out.println();
            System.out.println("=== FORGE v0.1 ===");
            System.out.println("1) List courses");
            System.out.println("2) Add course");
            System.out.println("3) Delete course");
            System.out.println("4) Manage topics for a course");
            System.out.println("5) Log study session");
            System.out.println("6) Deadlines");
            System.out.println("7) Dashboard");
            System.out.println("0) Save & Exit");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listCourses();
                case "2" -> addCourse();
                case "3" -> deleteCourse();
                case "4" -> manageTopics();
                case "5" -> logStudySession();
                case "6" -> deadlinesMenu();
                case "7" -> showDashboard();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listCourses() {
        if (data.getCourses().isEmpty()) {
            System.out.println("No courses yet.");
            return;
        }
        for (int i = 0; i < data.getCourses().size(); i++) {
            Course c = data.getCourses().get(i);
            System.out.println((i + 1) + ") " + c.getName() + " (topics: " + c.getTopics().size() + ")");
        }
    }

    private void addCourse() {
        System.out.print("Course name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name can't be empty.");
            return;
        }
        data.getCourses().add(new Course(name));
        System.out.println("Added course: " + name);
    }

    private void deleteCourse() {
        listCourses();
        if (data.getCourses().isEmpty()) return;

        System.out.print("Delete which course number? ");
        String raw = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(raw) - 1;
            if (idx < 0 || idx >= data.getCourses().size()) {
                System.out.println("Out of range.");
                return;
            }
            Course removed = data.getCourses().remove(idx);
            System.out.println("Deleted: " + removed.getName());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
        }
    }

    private void manageTopics() {
        Course course = pickCourse();
        if (course == null) return;

        while (true) {
            System.out.println();
            System.out.println("=== TOPICS for " + course.getName() + " ===");
            System.out.println("1) List topics");
            System.out.println("2) Add topic");
            System.out.println("0) Back");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listTopics(course);
                case "2" -> addTopic(course);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private Course pickCourse() {
        listCourses();
        if (data.getCourses().isEmpty()) return null;

        System.out.print("Select course number: ");
        String raw = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(raw) - 1;
            if (idx < 0 || idx >= data.getCourses().size()) {
                System.out.println("Out of range.");
                return null;
            }
            return data.getCourses().get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
            return null;
        }
    }

    private void listTopics(Course course) {
        if (course.getTopics().isEmpty()) {
            System.out.println("No topics yet.");
            return;
        }
        for (int i = 0; i < course.getTopics().size(); i++) {
            var t = course.getTopics().get(i);
            String last = (t.getLastStudied() == null) ? "never" : t.getLastStudied().toString();
            System.out.println((i + 1) + ") " + t.getName() + " (lastStudied: " + last + ")");
        }
    }

    private void addTopic(Course course) {
        System.out.print("Topic name (e.g., Topic 1): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name can't be empty.");
            return;
        }
        course.addTopic(new com.forge.model.Topic(name));
        System.out.println("Added topic: " + name);
    }

    private void logStudySession() {
        Course course = pickCourse();
        if (course == null) return;

        if (course.getTopics().isEmpty()) {
            System.out.println("This course has no topics yet. Add topics first.");
            return;
        }

        // pick topic
        listTopics(course);
        System.out.print("Select topic number: ");
        String raw = scanner.nextLine().trim();

        int topicIdx;
        try {
            topicIdx = Integer.parseInt(raw) - 1;
            if (topicIdx < 0 || topicIdx >= course.getTopics().size()) {
                System.out.println("Out of range.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
            return;
        }

        var topic = course.getTopics().get(topicIdx);

        // date (default today)
        java.time.LocalDate date = java.time.LocalDate.now();
        System.out.print("Date (YYYY-MM-DD) or blank for today: ");
        String dateRaw = scanner.nextLine().trim();
        if (!dateRaw.isEmpty()) {
            try {
                date = java.time.LocalDate.parse(dateRaw);
            } catch (Exception e) {
                System.out.println("Invalid date format.");
                return;
            }
        }

        // duration
        System.out.print("Duration minutes: ");
        String durRaw = scanner.nextLine().trim();
        int mins;
        try {
            mins = Integer.parseInt(durRaw);
            if (mins <= 0) {
                System.out.println("Minutes must be > 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
            return;
        }

        // notes
        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine();

        // create session
        com.forge.model.StudySession session =
                new com.forge.model.StudySession(date, course.getId(), topic.getId(), mins, notes);

        data.getStudySessions().add(session);

        // update lastStudied
        topic.setLastStudied(date);

        System.out.println("Logged " + mins + " min for " + course.getName() + " — " + topic.getName());
    }
    private void deadlinesMenu() {
        while (true) {
            System.out.println();
            System.out.println("=== DEADLINES ===");
            System.out.println("1) List deadlines");
            System.out.println("2) Add deadline");
            System.out.println("0) Back");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listDeadlines();
                case "2" -> addDeadline();
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listDeadlines() {
        if (data.getDeadlines().isEmpty()) {
            System.out.println("No deadlines yet.");
            return;
        }

        data.getDeadlines().stream()
                .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
                .forEach(d -> {
                    String courseName = findCourseName(d.getCourseId());
                    String weight = (d.getWeight() == null) ? "-" : d.getWeight().toString();
                    System.out.println(d.getDueDate() + " | " + d.getType() + " | " + courseName + " | " + d.getTitle() + " | weight: " + weight);
                });
    }

    private void addDeadline() {
        Course course = pickCourse();
        if (course == null) return;

        System.out.print("Type (ASSIGNMENT/QUIZ/EXAM): ");
        String typeRaw = scanner.nextLine().trim().toUpperCase();

        com.forge.model.Deadline.Type type;
        try {
            type = com.forge.model.Deadline.Type.valueOf(typeRaw);
        } catch (Exception e) {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Title (e.g., Quiz 1): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Title can't be empty.");
            return;
        }

        System.out.print("Due date (YYYY-MM-DD): ");
        String dueRaw = scanner.nextLine().trim();

        java.time.LocalDate due;
        try {
            due = java.time.LocalDate.parse(dueRaw);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }

        System.out.print("Weight (optional, blank if none): ");
        String wRaw = scanner.nextLine().trim();
        Double weight = null;
        if (!wRaw.isEmpty()) {
            try {
                weight = Double.parseDouble(wRaw);
            } catch (NumberFormatException e) {
                System.out.println("Invalid weight.");
                return;
            }
        }

        com.forge.model.Deadline d = new com.forge.model.Deadline(type, course.getId(), title, due, weight);
        data.getDeadlines().add(d);

        System.out.println("Added deadline: " + title + " (" + due + ")");
    }

    private String findCourseName(String courseId) {
        for (Course c : data.getCourses()) {
            if (c.getId().equals(courseId)) return c.getName();
        }
        return "(unknown course)";
    }
    private void showDashboard() {
        System.out.println();
        System.out.println("=== DASHBOARD ===");

        printWhatToStudyToday();
        System.out.println();
        printWeeklyTarget();
    }

    private void printWhatToStudyToday() {
        System.out.println("-- What to study today --");

        if (data.getCourses().isEmpty()) {
            System.out.println("No courses yet.");
            return;
        }

        for (Course course : data.getCourses()) {
            if (course.getTopics().isEmpty()) {
                System.out.println(course.getName() + ": (no topics yet)");
                continue;
            }

            com.forge.model.Topic best = null;

            for (com.forge.model.Topic t : course.getTopics()) {
                if (best == null) {
                    best = t;
                    continue;
                }

                // Prefer never-studied topics
                if (best.getLastStudied() != null && t.getLastStudied() == null) {
                    best = t;
                    continue;
                }

                // If both have dates, pick the oldest date
                if (best.getLastStudied() != null && t.getLastStudied() != null) {
                    if (t.getLastStudied().isBefore(best.getLastStudied())) {
                        best = t;
                    }
                }

                // If best is never-studied, keep it (never-studied already wins)
            }

            String last = (best.getLastStudied() == null) ? "never" : best.getLastStudied().toString();
            System.out.println(course.getName() + " -> " + best.getName() + " (lastStudied: " + last + ")");
        }
    }

    private void printWeeklyTarget() {
        System.out.println("-- This week's target (>= 1 topic per course) --");

        if (data.getCourses().isEmpty()) {
            System.out.println("No courses yet.");
            return;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        for (Course course : data.getCourses()) {
            boolean studiedThisWeek = data.getStudySessions().stream()
                    .anyMatch(s ->
                            s.getCourseId().equals(course.getId())
                                    && !s.getDate().isBefore(monday)
                                    && !s.getDate().isAfter(today)
                    );

            String mark = studiedThisWeek ? "✅" : "❌";
            System.out.println(mark + " " + course.getName());
        }

        System.out.println("Week window: " + monday + " -> " + today);
    }
}