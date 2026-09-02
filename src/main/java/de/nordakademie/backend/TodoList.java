package de.nordakademie.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * TodoList - Component des Kompositum Designpatterns
 */
public class TodoList implements TodoListComponent {
    private final List<TodoListComponent> children;
    private final String name;

    public TodoList(String name) {
        this.children = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTask(TodoListComponent child) {
        children.add(child);
    }

    public void removeTask(TodoListComponent child) {
        children.remove(child);
    }

    public boolean isProjekt() {
        return true;
    }

    @Override
    public List<TodoListComponent> getChildren() {
        return children;
    }

}