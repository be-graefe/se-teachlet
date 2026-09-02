package de.nordakademie.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * TodoList - Component des Kompositum Designpatterns
 */
public class TodoList {
    private final List<Task> tasks;
    private final String name;

    public TodoList(String name) {
        this.tasks = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean isProjekt() {
        return true;
    }

}