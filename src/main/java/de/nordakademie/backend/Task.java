package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Task - Blatt des Kompositum Designpatterns
 */
public class Task implements TodoListComponent {
    private final String name;
    private final LocalDate faelligkeitsdatum;
    private boolean erledigt;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.faelligkeitsdatum = dueDate;
    }

    public long berechneTageBisFaelligkeitsdatum() {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, faelligkeitsdatum);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getFaelligkeitsdatum() {
        return faelligkeitsdatum;
    }

    @Override
    public boolean isErledigt() {
        return erledigt;
    }

    @Override
    public void setErledigt(boolean erledigt) {
        this.erledigt = erledigt;
    }

    @Override
    public List<TodoListComponent> getChildren() {
        return List.of();
    }

    @Override
    public void addChild(TodoListComponent child) {
        throw new UnsupportedOperationException("Eine Aufgabe kann keine Einträge enthalten.");
    }

    @Override
    public void removeChild(TodoListComponent child) {
        throw new UnsupportedOperationException("Eine Aufgabe kann keine Einträge enthalten.");
    }

    @Override
    public boolean isProjekt() {
        return false;
    }

}