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
            System.out.println("0) Save & Exit");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listCourses();
                case "2" -> addCourse();
                case "3" -> deleteCourse();
                case "0" -> { return; }
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
}