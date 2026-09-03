package de.nordakademie.ui;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.function.Supplier;

/**
 * Tabellenmodell über einer flachen Liste von {@link Row}s.
 */
public class TodoTableModel extends AbstractTableModel {

    private final Supplier<List<Row>> rowSupplier;
    private List<Row> rows;

    public TodoTableModel(Supplier<List<Row>> rowSupplier) {
        this.rowSupplier = rowSupplier;
        this.rows = rowSupplier.get();
    }

    /**
     * Liest die Zeilen neu aus dem Backend und aktualisiert die Anzeige.
     *
     * <p>Solange sich die Anzahl der Zeilen nicht ändert, wird nur ein Aktualisieren
     * gemeldet - so bleibt die Auswahl des Benutzers erhalten.</p>
     */
    public void refresh() {
        int previousRowCount = rows.size();
        rows = rowSupplier.get();

        if (previousRowCount == rows.size() && !rows.isEmpty()) {
            fireTableRowsUpdated(0, rows.size() - 1);
        } else {
            fireTableDataChanged();
        }
    }

    public Row getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return Column.at(columnIndex).title();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.at(columnIndex).type();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).isEditable(getRow(rowIndex));
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueOf(getRow(rowIndex));
    }

    /**
     * Übernimmt eine Änderung (z. B. das Abhaken) und liest die Zeilen anschließend neu.
     *
     * <p>Eine {@link Row} ist eine Momentaufnahme des Backends. Nach jeder Änderung wird
     * deshalb komplett neu aufgebaut - dadurch sieht man sofort auch Auswirkungen auf
     * andere Zeilen, etwa wenn später ein abgehaktes Projekt seine Aufgaben mit abhakt.</p>
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Column.at(columnIndex).setValue(getRow(rowIndex), value);
        refresh();
    }
}
