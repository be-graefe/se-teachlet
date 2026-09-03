package de.nordakademie.backend;

import java.time.LocalDate;

/**
 * Task Klasse, enthält Name und Fälligkeitsdatum der Tasks
 * 
 * @author Bennett Graefe
 */
public class Task {
    private final String name;
    private final LocalDate dueDate;
    private boolean checked;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
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