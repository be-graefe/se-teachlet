package de.nordakademie.backend;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Task {
    private final String name;
    private final LocalDate dueDate;
    private boolean checked;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    public long calculateDaysUntilDue() {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, dueDate);
    }

    public String getName() {
        return name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
