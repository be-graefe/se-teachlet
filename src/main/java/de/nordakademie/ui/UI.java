package de.nordakademie.ui;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;
import de.nordakademie.backend.TodoListComponent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class UI {
    private static final double SCALE = 2.0;

    private final TodoList todoList;

    public UI(TodoList todoList) {
        this.todoList = todoList;
    }

    public void show() {
        JFrame frame = new JFrame("Aufgabenliste");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        TaskTableModel model = new TaskTableModel(todoList);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        Font scaledFont = buildScaledFont(table.getFont());
        applyTableScale(table, scaledFont);
        applyRowRenderers(table, model);

        JPanel buttonPanel = createButtonPanel(frame, table, model, scaledFont);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize((int) Math.round(600 * SCALE), (int) Math.round(380 * SCALE));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Font buildScaledFont(Font baseFont) {
        return baseFont.deriveFont((float) (baseFont.getSize2D() * SCALE));
    }

    private void applyTableScale(JTable table, Font scaledFont) {
        table.setFont(scaledFont);
        table.getTableHeader().setFont(scaledFont);
        table.setRowHeight((int) Math.round(24 * SCALE));
    }

    private void applyRowRenderers(JTable table, TaskTableModel model) {
        table.setDefaultRenderer(Object.class, new DueDateColorTextRenderer(model));
        table.setDefaultRenderer(Boolean.class, new DueDateColorCheckBoxRenderer(model));
    }

    private JPanel createButtonPanel(JFrame frame, JTable table, TaskTableModel model, Font scaledFont) {
        JButton addButton = createButton("Aufgabe hinzufügen", scaledFont);
        JButton removeButton = createButton("Ausgewähltes löschen", scaledFont);
        JButton createProjectButton = createButton("Projekt hinzufügen", scaledFont);

        addButton.addActionListener(e -> handleAddTask(frame, model, scaledFont));
        removeButton.addActionListener(e -> handleRemoveSelectedTask(table, model));
        createProjectButton.addActionListener(e -> handleCreateProject(frame, model, scaledFont));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(createProjectButton);
        return buttonPanel;
    }

    private JButton createButton(String label, Font font) {
        JButton button = new JButton(label);
        button.setFont(font);
        return button;
    }

    private record ItemInput(String name, LocalDate dueDate) {
    }

    private ItemInput promptItemInput(JFrame frame, Font scaledFont, String title) {
        String name = showScaledInputDialog(frame, title, "Name:", scaledFont);
        if (name == null || name.isBlank()) {
            return null;
        }

        String dueDateInput = showScaledInputDialog(frame, title, "Fälligkeitsdatum (YYYY-MM-DD):", scaledFont);
        if (dueDateInput == null || dueDateInput.isBlank()) {
            return null;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateInput.trim());
            return new ItemInput(name.trim(), dueDate);
        } catch (DateTimeParseException ex) {
            showScaledErrorDialog(frame, "Invalides Datum. Bitte benutze YYYY-MM-YY.", scaledFont);
            return null;
        }
    }

    private void handleAddTask(JFrame frame, TaskTableModel model, Font scaledFont) {
        ItemInput input = promptItemInput(frame, scaledFont, "Aufgabe hinzufügen");
        if (input == null) return;

        todoList.addTask(new Task(input.name(), input.dueDate()));
        model.refresh();
    }

    private void handleCreateProject(JFrame frame, TaskTableModel model, Font scaledFont) {
        // TODO: Hier Logik für Projekte hinzufügen
    }

    private void handleRemoveSelectedTask(JTable table, TaskTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Row row = model.getRowAt(modelRow);
        if (!row.isTask()) {
            return;
        }

        todoList.removeTask(row.asTask());
        model.refresh();
    }

    private String showScaledInputDialog(JFrame parent, String title, String labelText, Font font) {
        JLabel label = new JLabel(labelText);
        label.setFont(font);

        JTextField input = new JTextField(18);
        input.setFont(font);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        return input.getText();
    }

    private void showScaledErrorDialog(JFrame parent, String message, Font font) {
        JLabel label = new JLabel(message);
        label.setFont(font);
        JOptionPane.showMessageDialog(parent, label, "Input error", JOptionPane.ERROR_MESSAGE);
    }

    private record Row(Object item, int depth) {
        boolean isTodoList() {
            return item instanceof TodoList;
        }

        boolean isTask() {
            return item instanceof Task;
        }

        TodoList asTodoList() {
            return (TodoList) item;
        }

        Task asTask() {
            return (Task) item;
        }
    }

    private static class TaskTableModel extends AbstractTableModel {
        private final String[] columns = {"Erledigt", "Aufgabe", "Fälligkeitsdatum"};
        private final TodoList todoList;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        private List<Row> rows;

        TaskTableModel(TodoList todoList) {
            this.todoList = todoList;
            this.rows = buildRows(todoList, 0);
        }

        private static List<Row> buildRows(TodoListComponent component, int depth) {
            List<Row> result = new java.util.ArrayList<>();
            result.add(new Row(component, depth));
            for (TodoListComponent child : component.getChildren()) {
                result.addAll(buildRows(child, depth + 1));
            }
            return result;
        }

        void refresh() {
            rows = buildRows(todoList, 0);
            fireTableDataChanged();
        }

        Row getRowAt(int rowIndex) {
            return rows.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Boolean.class;
                case 1, 2 -> String.class;
                default -> Object.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 && rows.get(rowIndex).isTask();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Row row = rows.get(rowIndex);
            if (row.isTodoList()) {
                return switch (columnIndex) {
                    case 1 -> row.asTodoList().getName();
                    default -> null;
                };
            }

            Task task = row.asTask();
            return switch (columnIndex) {
                case 0 -> task.isErledigt();
                case 1 -> task.getName();
                case 2 -> task.getFaelligkeitsdatum().format(dateFormatter);
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Row row = rows.get(rowIndex);
            if (columnIndex == 0 && row.isTask() && aValue instanceof Boolean checked) {
                row.asTask().setErledigt(checked);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    private static class DueDateColorTextRenderer extends DefaultTableCellRenderer {
        private static final int INDENT_PER_LEVEL = (int) Math.round(16 * SCALE);

        private final TaskTableModel model;

        DueDateColorTextRenderer(TaskTableModel model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Row modelRow = model.getRowAt(table.convertRowIndexToModel(row));
            applyRowColor(component, table, modelRow, isSelected);

            if (component instanceof JLabel label) {
                int indent = column == 1 ? modelRow.depth() * INDENT_PER_LEVEL : 0;
                label.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));
                label.setFont(modelRow.isTodoList()
                        ? label.getFont().deriveFont(Font.BOLD)
                        : label.getFont().deriveFont(Font.PLAIN));
            }
            return component;
        }
    }

    private static class DueDateColorCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        private final TaskTableModel model;
        private final JLabel blankCell = new JLabel();

        DueDateColorCheckBoxRenderer(TaskTableModel model) {
            this.model = model;
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            blankCell.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Row modelRow = model.getRowAt(table.convertRowIndexToModel(row));
            if (modelRow.isTodoList()) {
                applyRowColor(blankCell, table, modelRow, isSelected);
                return blankCell;
            }

            setSelected(Boolean.TRUE.equals(value));
            applyRowColor(this, table, modelRow, isSelected);
            return this;
        }
    }

    private static void applyRowColor(Component component, JTable table, Row row, boolean isSelected) {
        if (isSelected) {
            component.setBackground(table.getSelectionBackground());
            component.setForeground(table.getSelectionForeground());
            return;
        }

        if (row.isTodoList()) {
            component.setBackground(new Color(230, 230, 230));
            component.setForeground(Color.BLACK);
            return;
        }

        long daysUntilDue = row.asTask().berechneTageBisFaelligkeitsdatum();
        if (daysUntilDue <= 0) {
            component.setBackground(new Color(255, 199, 206));
        } else if (daysUntilDue == 1) {
            component.setBackground(new Color(255, 242, 204));
        } else {
            component.setBackground(Color.WHITE);
        }
        component.setForeground(Color.BLACK);
    }
}