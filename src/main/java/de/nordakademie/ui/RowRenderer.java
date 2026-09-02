package de.nordakademie.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Darstellung einer Zeile: Einrückung nach Tiefe und Hintergrundfarbe nach Zustand.
 *
 * <p>Die Renderer entscheiden allein anhand der {@link Row} - ob diese aus einer Aufgabe,
 * einer Liste oder später aus einem Kompositum stammt, spielt keine Rolle.</p>
 */
public final class RowRenderer {

    private static final Color LIST = new Color(230, 230, 230);
    private static final Color OVERDUE = new Color(255, 199, 206);
    private static final Color DUE_SOON = new Color(255, 242, 204);
    private static final Color DONE = new Color(226, 239, 218);
    private static final Color NORMAL = Color.WHITE;

    private static final int INDENT = UI.scaled(18);
    private static final int PADDING = UI.scaled(6);

    private RowRenderer() {
    }

    private static void applyColors(Component component, JTable table, Row row, boolean isSelected) {
        if (isSelected) {
            component.setBackground(table.getSelectionBackground());
            component.setForeground(table.getSelectionForeground());
            return;
        }
        component.setBackground(backgroundFor(row));
        component.setForeground(Color.BLACK);
    }

    private static Color backgroundFor(Row row) {
        if (Boolean.TRUE.equals(row.done())) {
            return DONE;
        }
        if (row.group()) {
            return LIST;
        }
        return dueDateColor(row.dueDate());
    }

    private static Color dueDateColor(LocalDate dueDate) {
        if (dueDate == null) {
            return NORMAL;
        }
        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (daysUntilDue <= 0) {
            return OVERDUE;
        }
        if (daysUntilDue == 1) {
            return DUE_SOON;
        }
        return NORMAL;
    }

    /** Renderer für alle Text-Spalten. */
    public static class Text extends DefaultTableCellRenderer {

        private final TodoTableModel model;

        public Text(TodoTableModel model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int viewRow, int viewColumn) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, viewRow, viewColumn);

            Row row = model.getRow(table.convertRowIndexToModel(viewRow));
            applyColors(label, table, row, isSelected);
            label.setFont(label.getFont().deriveFont(row.group() ? Font.BOLD : Font.PLAIN));

            boolean nameColumn = Column.at(table.convertColumnIndexToModel(viewColumn)) == Column.NAME;
            int indent = nameColumn ? row.depth() * INDENT : 0;
            label.setBorder(BorderFactory.createEmptyBorder(0, PADDING + indent, 0, PADDING));
            return label;
        }
    }

    /** Renderer für die Erledigt-Spalte; Zeilen ohne Status bleiben leer. */
    public static class CheckBox extends JCheckBox implements TableCellRenderer {

        private final TodoTableModel model;
        private final JLabel emptyCell = new JLabel();

        public CheckBox(TodoTableModel model) {
            this.model = model;
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            emptyCell.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int viewRow, int viewColumn) {
            Row row = model.getRow(table.convertRowIndexToModel(viewRow));

            if (value == null) {
                applyColors(emptyCell, table, row, isSelected);
                return emptyCell;
            }

            setSelected(Boolean.TRUE.equals(value));
            applyColors(this, table, row, isSelected);
            return this;
        }
    }
}
