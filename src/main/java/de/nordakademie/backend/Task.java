package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.List;

/**
 * Task - Blatt des Kompositum Designpatterns
 * 
 * @author Flavio Prösch
 */
public class Task implements TodoListComponent {
    private final String name;
    private final LocalDate dueDate;
    private boolean checked;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public List<TodoListComponent> getChildren() {
        return List.of();
    }

    /** 
     * Eine Aufgabe (als Blatt) kann keine Kinder haben 
     * @throws UnsupportedOperationException 
     */
    @Override
    public void addChild(TodoListComponent child) {
        throw new UnsupportedOperationException("Eine Aufgabe kann keine Einträge enthalten.");
    }

    /** 
     * Eine Aufgabe hat (als Blatt) keine Kinder, kann also auch keine Kinder entfernen  
     * @throws UnsupportedOperationException 
     */
    @Override
    public void removeChild(TodoListComponent child) {
        throw new UnsupportedOperationException("Eine Aufgabe kann keine Einträge enthalten.");
    }

    @Override
    public boolean isProject() {
        return false;
    }

}