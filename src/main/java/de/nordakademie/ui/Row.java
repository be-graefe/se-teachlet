package de.nordakademie.ui;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Eine einzelne Zeile der Anzeige.
 *
 * <p>Die Zeile ist bewusst unabhängig von den Backend-Klassen: sie enthält nur die
 * anzuzeigenden Werte und - falls erlaubt - die Aktionen "abhaken" und "löschen".
 * Tabelle, Spalten und Renderer arbeiten ausschließlich mit dieser Zeile und müssen
 * deshalb nicht angepasst werden, wenn sich die Struktur im Backend ändert.</p>
 *
 * <p>Erweiterung um ein weiteres Feld (z. B. Bearbeitungszeit): hier eine Komponente
 * ergänzen, sie in {@link RowBuilder} füllen und in {@link Column} als Spalte anzeigen.</p>
 *
 * @param depth     Einrückungstiefe (0 = oberste Ebene)
 * @param group     {@code true} für die Kopfzeile einer Liste / eines Projekts
 * @param name      angezeigter Name
 * @param dueDate   Fälligkeitsdatum, darf {@code null} sein
 * @param done      Erledigt-Status, {@code null} wenn die Zeile keinen Status hat
 * @param onDone    Aktion zum Abhaken, {@code null} wenn die Zeile nicht abhakbar ist
 * @param onDelete  Aktion zum Löschen, {@code null} wenn die Zeile nicht löschbar ist
 */
public record Row(
        int depth,
        boolean group,
        String name,
        LocalDate dueDate,
        Boolean done,
        Consumer<Boolean> onDone,
        Runnable onDelete
) {

    /** Kopfzeile einer Liste / eines Projekts ohne eigene Aktionen. */
    public static Row forList(int depth, String name) {
        return forList(depth, name, null, null, null, null);
    }

    /** Kopfzeile einer Liste / eines Projekts mit Datum und Aktionen. */
    public static Row forList(int depth, String name, LocalDate dueDate, Boolean done,
                              Consumer<Boolean> onDone, Runnable onDelete) {
        return new Row(depth, true, name, dueDate, done, onDone, onDelete);
    }

    /** Zeile einer einzelnen Aufgabe. */
    public static Row forTask(int depth, String name, LocalDate dueDate, boolean done,
                              Consumer<Boolean> onDone, Runnable onDelete) {
        return new Row(depth, false, name, dueDate, done, onDone, onDelete);
    }

    public boolean canToggle() {
        return onDone != null;
    }

    public boolean canDelete() {
        return onDelete != null;
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
