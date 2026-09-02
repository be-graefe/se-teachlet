package de.nordakademie.backend;

import java.util.List;

public interface TodoListComponent {
    String getName();
    List<TodoListComponent> getChildren();
    boolean isTodoListe();
}
