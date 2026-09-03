package de.nordakademie.ui;

import java.util.function.Function;

/**
 * Die Spalten der Tabelle.
 *
 */
public enum Column {

    /** Erledigt-Haken; nur abhakbare Zeilen sind editierbar. */
    DONE("Erledigt", Boolean.class, 110, Row::done) {
        @Override
        public boolean isEditable(Row row) {
            return row.canToggle();
        }

        @Override
        public void setValue(Row row, Object value) {
            row.toggle(Boolean.TRUE.equals(value));
        }
    },

    NAME("Aufgabe", String.class, 320, Row::name),

    DUE_DATE("Fällig am", String.class, 160, row -> Dates.format(row.dueDate()));

    private final String title;
    private final Class<?> type;
    private final int preferredWidth;
    private final Function<Row, Object> value;

    Column(String title, Class<?> type, int preferredWidth, Function<Row, Object> value) {
        this.title = title;
        this.type = type;
        this.preferredWidth = preferredWidth;
        this.value = value;
    }

    public static Column at(int index) {
        return values()[index];
    }

    public String title() {
        return title;
    }

    public Class<?> type() {
        return type;
    }

    public int preferredWidth() {
        return preferredWidth;
    }

    public Object valueOf(Row row) {
        return value.apply(row);
    }

    public boolean isEditable(Row row) {
        return false;
    }

    public void setValue(Row row, Object value) {
        // Spalten sind standardmäßig nur Anzeige.
    }
}
