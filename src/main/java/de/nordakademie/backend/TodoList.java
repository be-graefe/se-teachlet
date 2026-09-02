package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TodoList - Kompositum des Kompositum Designpatterns
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
    public LocalDate getFaelligkeitsdatum() {
        return children.stream()
                .filter(child -> !child.isErledigt())
                .map(TodoListComponent::getFaelligkeitsdatum)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    /** Ein Projekt ist erledigt, wenn es Einträge enthält und diese alle erledigt sind. */
    @Override
    public boolean isErledigt() {
        return !children.isEmpty()
                && children.stream().allMatch(TodoListComponent::isErledigt);
    }

    /** Hakt alle enthaltenen Einträge mit ab - rekursiv bis in die Unterprojekte. */
    @Override
    public void setErledigt(boolean erledigt) {
        children.forEach(child -> child.setErledigt(erledigt));
    }

    @Override
    public boolean isProjekt() {
        return true;
    }
}
