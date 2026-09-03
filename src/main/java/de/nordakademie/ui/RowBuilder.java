package de.nordakademie.ui;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;

import java.util.ArrayList;
import java.util.List;

/**
 * Übersetzt das Backend in die flache Zeilenliste der Anzeige.
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
    }

    private static Row toRow(Task task, TodoList parent, int depth) {
        return Row.forTask(
                depth,
                task.getName(),
                task.getDueDate(),
                task.isChecked(),
                task::setChecked,
                () -> parent.removeTask(task)
        );
    }
}
