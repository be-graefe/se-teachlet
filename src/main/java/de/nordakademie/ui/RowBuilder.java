package de.nordakademie.ui;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;

import java.util.ArrayList;
import java.util.List;

/**
 * Übersetzt das Backend in die flache Zeilenliste der Anzeige.
 *
 * <p>Dies ist die einzige Klasse der Oberfläche, die {@link TodoList} und {@link Task} kennt.</p>
 *
 * <p><b>Erweiterung Kompositum:</b> sobald eine Liste weitere Listen enthalten kann, wird hier
 * nur zusätzlich rekursiv abgestiegen (siehe markierte Stelle in {@code appendList}).
 * Tabelle, Spalten, Renderer und Dialoge bleiben unverändert.</p>
 */
public final class RowBuilder {

    private RowBuilder() {
    }

    public static List<Row> build(TodoList todoList) {
        List<Row> rows = new ArrayList<>();
        appendList(rows, todoList, 0);
        return rows;
    }

    private static void appendList(List<Row> rows, TodoList list, int depth) {
        rows.add(Row.forList(depth, list.getName()));

        for (Task task : list.getTasks()) {
            rows.add(toRow(task, list, depth + 1));
        }

        // Kompositum: hier später die enthaltenen Unterlisten ergänzen, z. B.
        // for (TodoList child : list.getChildren()) appendList(rows, child, depth + 1);
    }

    private static Row toRow(Task task, TodoList parent, int depth) {
        return Row.forTask(
                depth,
                task.getName(),
                task.getFaelligkeitsdatum(),
                task.isErledigt(),
                task::setErledigt,
                () -> parent.removeTask(task)
        );
    }
}
