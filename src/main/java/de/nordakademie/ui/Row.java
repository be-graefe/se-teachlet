package de.nordakademie.ui;

import de.nordakademie.backend.TaskItem;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Eine einzelne Zeile der Anzeige.
 *
 * @param depth     Einrückungstiefe (0 = oberste Ebene)
 * @param group     {@code true} für die Kopfzeile einer Liste / eines Projekts
 * @param name      angezeigter Name
 * @param dueDate   Fälligkeitsdatum, darf {@code null} sein
 * @param done      Erledigt-Status, {@code null} wenn die Zeile keinen Status hat
 * @param onDone    Aktion zum Abhaken, {@code null} wenn die Zeile nicht abhakbar ist
 * @param onDelete  Aktion zum Löschen, {@code null} wenn die Zeile nicht löschbar ist
 * @param target    Liste, in die ein neuer Eintrag gehört, wenn diese Zeile ausgewählt ist:
 *                  bei einem Projekt das Projekt selbst, bei einer Aufgabe deren Liste
 */
public record Row(
        int depth,
        boolean group,
        String name,
        LocalDate dueDate,
        Boolean done,
        Consumer<Boolean> onDone,
        Runnable onDelete,
        TaskItem target
) {

    /** Kopfzeile einer Liste / eines Projekts. */
    public static Row forList(int depth, String name, LocalDate dueDate, Boolean done,
                              Consumer<Boolean> onDone, Runnable onDelete, TaskItem target) {
        return new Row(depth, true, name, dueDate, done, onDone, onDelete, target);
    }

    /** Zeile einer einzelnen Aufgabe. */
    public static Row forTask(int depth, String name, LocalDate dueDate, boolean done,
                              Consumer<Boolean> onDone, Runnable onDelete, TaskItem target) {
        return new Row(depth, false, name, dueDate, done, onDone, onDelete, target);
    }

    public boolean canToggle() {
        return onDone != null;
    }

    public boolean canDelete() {
        return onDelete != null;
    }

    public boolean canAdd() {
        return target != null;
    }

    public void toggle(boolean value) {
        if (canToggle()) {
            onDone.accept(value);
        }
    }

    public void delete() {
        if (canDelete()) {
            onDelete.run();
        }
    }
}