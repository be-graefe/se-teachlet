package de.nordakademie.backend;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Task {
    private final String name;
    private final LocalDate dueDate;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    public long calculateDaysUntilDue() {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, dueDate);
    }
}
