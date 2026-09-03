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
public class Project implements ITaskComponent {
    private final List<ITaskComponent> children;
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
    public void addChild(ITaskComponent task) {
        children.add(task);
    }

    @Override
    public void removeChild(ITaskComponent task) {
        children.remove(task);
    }

    @Override
    public List<ITaskComponent> getChildren() {
        return children;
    }

    /**
     * Die Frist eines Projekts ist die früheste Frist seiner offenen Einträge.
     * Erledigte Einträge zählen dann nicht mehr mit.
     *
     * @return {@code null}, wenn das Projekt keinen offenen Eintrag mit Frist enthält
     */
    @Override
    public LocalDate getDueDate() {
        return children.stream()
                .filter(child -> !child.isChecked())
                .map(ITaskComponent::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    /** Ein Projekt ist erledigt, wenn es Einträge enthält und diese alle erledigt sind. */
    @Override
    public boolean isChecked() {
        return !children.isEmpty()
                && children.stream().allMatch(ITaskComponent::isChecked);
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
