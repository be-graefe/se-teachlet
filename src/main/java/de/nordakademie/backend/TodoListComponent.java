package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.List;

/**
 * Component des Kompositum Designpatterns - gemeinsame Schnittstelle von Task (Blatt)
 * und TodoList (Kompositum). Die Oberfläche arbeitet ausschließlich mit diesem Typ.
 */
public interface TodoListComponent {

    String getName();
    
    List<TodoListComponent> getChildren();

    boolean isProjekt();

    LocalDate getFaelligkeitsdatum();

    boolean isErledigt();

    void setErledigt(boolean erledigt);

    void addChild(TodoListComponent child);

    void removeChild(TodoListComponent child);
}