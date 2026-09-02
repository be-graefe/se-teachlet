package de.nordakademie.ui;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Fenster der Anwendung: eine Tabelle mit allen Zeilen und darunter die Schaltflächen.
 *
 * <p>Die Anzeige selbst läuft vollständig über {@link Row} und {@link RowBuilder}; das
 * Fenster kennt das Backend nur, um neue Aufgaben anzulegen. Für eine Erweiterung
 * (z. B. Kompositum oder eine weitere Spalte) muss diese Klasse in der Regel nicht
 * angefasst werden.</p>
 */
public class UI {

    private static final double SCALE = 2.0;

    private static boolean fontsScaled;

    private final TodoList todoList;
    private final TodoTableModel model;
    private final JTable table;
    private final JFrame frame;

    public UI(TodoList todoList) {
        scaleFonts();
        this.todoList = todoList;
        this.model = new TodoTableModel(() -> RowBuilder.build(todoList));
        this.table = createTable();
        this.frame = createFrame();
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("Aufgabenliste");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(createButtonBar(), BorderLayout.SOUTH);
        frame.setSize(scaled(560), scaled(360));
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private JTable createTable() {
        JTable table = new JTable(model);
        table.setRowHeight(scaled(24));
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setDefaultRenderer(Object.class, new RowRenderer.Text(model));
        table.setDefaultRenderer(Boolean.class, new RowRenderer.CheckBox(model));
        table.setDefaultEditor(Boolean.class, createCheckBoxEditor());

        for (Column column : Column.values()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column.ordinal());
            tableColumn.setPreferredWidth(scaled(column.preferredWidth()));
        }
        return table;
    }

    /** Editor der Erledigt-Spalte: ein Klick hakt ab und übernimmt die Änderung sofort. */
    private DefaultCellEditor createCheckBoxEditor() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultCellEditor editor = new DefaultCellEditor(checkBox);
        editor.setClickCountToStart(1);
        checkBox.addActionListener(event -> editor.stopCellEditing());
        return editor;
    }

    private JComponent createButtonBar() {
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, scaled(8), scaled(8)));
        buttonBar.setBorder(BorderFactory.createEmptyBorder());
        buttonBar.add(createButton("Aufgabe hinzufügen", this::addTask));
        buttonBar.add(createButton("Auswahl löschen", this::deleteSelection));
        buttonBar.add(createButton("Projekt hinzufügen", this::addProject));
        return buttonBar;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void addTask() {
        TaskDialog.show(frame, "Aufgabe hinzufügen").ifPresent(input -> {
            todoList.addTask(new Task(input.name(), input.dueDate()));
            model.refresh();
        });
    }

    private void addProject() {
        // TODO: Hier Logik für Projekte hinzufügen (Kompositum).
    }

    /**
     * Löscht die ausgewählte Zeile über die Aktion, die {@link RowBuilder} mitgegeben hat.
     * Ob dabei eine Aufgabe oder später ein ganzes Projekt entfernt wird, ist hier egal.
     */
    private void deleteSelection() {
        selectedRow().filter(Row::canDelete).ifPresent(row -> {
            row.delete();
            model.refresh();
        });
    }

    private Optional<Row> selectedRow() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow < 0) {
            return Optional.empty();
        }
        return Optional.of(model.getRow(table.convertRowIndexToModel(selectedViewRow)));
    }

    /** Vergrößert alle Schriften der Oberfläche einmalig, damit die Anwendung gut ablesbar ist. */
    private static void scaleFonts() {
        if (fontsScaled) {
            return;
        }
        fontsScaled = true;

        for (Object key : new ArrayList<>(UIManager.getLookAndFeelDefaults().keySet())) {
            if (UIManager.get(key) instanceof Font font) {
                UIManager.put(key, font.deriveFont((float) (font.getSize2D() * SCALE)));
            }
        }
    }

    static int scaled(int value) {
        return (int) Math.round(value * SCALE);
    }
}
