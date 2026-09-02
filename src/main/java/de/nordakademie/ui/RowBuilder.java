package de.nordakademie.ui;

import de.nordakademie.backend.TodoListComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Übersetzt das Backend in die flache Zeilenliste der Anzeige.
 *
 * <p>Dies ist die einzige Klasse der Oberfläche, die {@link TodoListComponent} kennt.
 * Sie steigt rekursiv durch das Kompositum und fragt jede Komponente nach denselben
 * Werten - ob dahinter eine einzelne Aufgabe oder ein Projekt mit Unterprojekten steckt,
 * entscheidet das Backend.</p>
 */
public final class RowBuilder {

    private RowBuilder() {
    }

    public static List<Row> build(TodoListComponent root) {
        List<Row> rows = new ArrayList<>();
        append(rows, root, null, 0);
        return rows;
    }

    /** Fügt die Zeile der Komponente hinzu und steigt anschließend in ihre Einträge ab. */
    private static void append(List<Row> rows, TodoListComponent component, TodoListComponent parent, int depth) {
        rows.add(toRow(component, parent, depth));

        for (TodoListComponent child : component.getChildren()) {
            append(rows, child, component, depth + 1);
        }
    }

    private static Row toRow(TodoListComponent component, TodoListComponent parent, int depth) {
        // Die Wurzel hat keinen Vater und ist deshalb nicht löschbar.
        Runnable onDelete = parent == null ? null : () -> parent.removeChild(component);

        // Ziel für neue Einträge: ein Projekt nimmt sie selbst auf, eine Aufgabe
        // reicht an ihre Liste weiter (siehe letztes Argument der Fabrikmethoden).
        if (component.isProjekt()) {
            return Row.forList(
                    depth,
                    component.getName(),
                    component.getFaelligkeitsdatum(),
                    component.isErledigt(),
                    component::setErledigt,
                    onDelete,
                    component
            );
        }

        return Row.forTask(
                depth,
                component.getName(),
                component.getFaelligkeitsdatum(),
                component.isErledigt(),
                component::setErledigt,
                onDelete,
                parent
        );
    }
}