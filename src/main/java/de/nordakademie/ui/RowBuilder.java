package de.nordakademie.ui;

import de.nordakademie.backend.ITaskComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Übersetzt das Backend in die flache Zeilenliste der Anzeige.
 * 
 * @author Flavio Prösch
 */
public final class RowBuilder {

    private RowBuilder() {
    }

    public static List<Row> build(ITaskComponent root) {
        List<Row> rows = new ArrayList<>();
        append(rows, root, null, 0);
        return rows;
    }

    /** Fügt die Zeile der Komponente hinzu und steigt anschließend in ihre Einträge ab. */
    private static void append(List<Row> rows, ITaskComponent component, ITaskComponent parent, int depth) {
        rows.add(toRow(component, parent, depth));

        for (ITaskComponent child : component.getChildren()) {
            append(rows, child, component, depth + 1);
        }
    }

    private static Row toRow(ITaskComponent component, ITaskComponent parent, int depth) {
        // Die Wurzel hat keinen Vater und ist deshalb nicht löschbar
        Runnable onDelete = parent == null ? null : () -> parent.removeChild(component);

        // Ziel für neue Einträge: ein Projekt nimmt sie selbst auf, eine Aufgabe
        // reicht an ihre Liste weiter
        if (component.isProject()) {
            return Row.forList(
                    depth,
                    component.getName(),
                    component.getDueDate(),
                    component.isChecked(),
                    component::setChecked,
                    onDelete,
                    component
            );
        }

        return Row.forTask(
                depth,
                component.getName(),
                component.getDueDate(),
                component.isChecked(),
                component::setChecked,
                onDelete,
                parent
        );
    }
}