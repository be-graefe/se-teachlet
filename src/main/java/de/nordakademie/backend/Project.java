package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Projekt - Kompositum des Kompositum Designpatterns
 * 
 * @author Flavio Prösch
 */
public class Project implements TaskItem {
    private final List<TaskItem> children;
    private final String name;

    public Project(String name) {
        this.children = new ArrayList<>();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addChild(TaskItem task) {
        children.add(task);
    }

    @Override
    public void removeChild(TaskItem task) {
        children.remove(task);
    }

    /** 
     * Gibt die Kinder des Objektes zurück 
     * 
     * @author Flavio Prösch, Reviewanforderungen von Lennart Hell
     */
    @Override
    public List<TaskItem> getChildren() {
        return List.copyOf(children);
    }

    /**
     * Die Frist eines Projekts ist die späteste Frist seiner offenen Einträge.
     * Erledigte Einträge zählen dabei nicht mehr mit.
     *
     * @return {@code null}, wenn das Projekt keinen offenen Eintrag mit Frist enthält
     * 
     * @author Flavio Prösch, Reviewanforderungen von Lennart Hell
     */
    @Override
    public LocalDate getDueDate() {
        return children.stream()
                .filter(child -> !child.isChecked())
                .map(TaskItem::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    /** Ein Projekt ist erledigt, wenn es Einträge enthält und diese alle erledigt sind. */
    @Override
    public boolean isChecked() {
        return !children.isEmpty()
                && children.stream().allMatch(TaskItem::isChecked);
    }

    /** Hakt alle enthaltenen Einträge mit ab -> rekursiv bis in die Unterprojekte. */
    @Override
    public void setChecked(boolean erledigt) {
        children.forEach(child -> child.setChecked(erledigt));
    }

    @Override
    public boolean isProject() {
        return true;
    }
}
