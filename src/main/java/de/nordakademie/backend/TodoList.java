package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TodoList - Kompositum des Kompositum Designpatterns
 * 
 * @author Flavio Prösch
 */
public class TodoList implements TodoListComponent {
    private final List<TodoListComponent> children;
    private final String name;

    public TodoList(String name) {
        this.children = new ArrayList<>();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addChild(TodoListComponent task) {
        children.add(task);
    }

    @Override
    public void removeChild(TodoListComponent task) {
        children.remove(task);
    }

    @Override
    public List<TodoListComponent> getChildren() {
        return children;
    }

    /**
     * Die Frist eines Projekts ist die früheste Frist seiner offenen Einträge.
     * Erledigte Einträge zählen nicht mehr mit.
     *
     * @return {@code null}, wenn das Projekt keinen offenen Eintrag mit Frist enthält
     */
    @Override
    public LocalDate getDueDate() {
        return children.stream()
                .filter(child -> !child.isChecked())
                .map(TodoListComponent::getDueDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    /** Ein Projekt ist erledigt, wenn es Einträge enthält und diese alle erledigt sind. */
    @Override
    public boolean isChecked() {
        return !children.isEmpty()
                && children.stream().allMatch(TodoListComponent::isChecked);
    }

    /** Hakt alle enthaltenen Einträge mit ab - rekursiv bis in die Unterprojekte. */
    @Override
    public void setChecked(boolean erledigt) {
        children.forEach(child -> child.setChecked(erledigt));
    }

    @Override
    public boolean isProject() {
        return true;
    }
}
