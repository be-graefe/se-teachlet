package de.nordakademie.ui;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

public class UI {
    private static final double SCALE = 2.0;

    private final TodoList todoList;

    public UI(TodoList todoList) {
        this.todoList = todoList;
    }

    public void show() {
        JFrame frame = new JFrame("Todo List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        TaskTableModel model = new TaskTableModel(todoList.getTasks());
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
        JButton addButton = createButton("Add", scaledFont);
        JButton removeButton = createButton("Remove selected", scaledFont);
        JButton createProjectButton = createButton("Create project", scaledFont);

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

        String dueDateInput = showScaledInputDialog(frame, title, "Due date (yyyy-MM-dd):", scaledFont);
        if (dueDateInput == null || dueDateInput.isBlank()) {
            return null;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateInput.trim());
            return new ItemInput(name.trim(), dueDate);
        } catch (DateTimeParseException ex) {
            showScaledErrorDialog(frame, "Invalid date. Please use yyyy-MM-dd.", scaledFont);
            return null;
        }
    }

    private void handleAddTask(JFrame frame, TaskTableModel model, Font scaledFont) {
        ItemInput input = promptItemInput(frame, scaledFont, "Add task");
        if (input == null) return;

        todoList.addTask(new Task(input.name(), input.dueDate()));
        model.sortByDueDate();
        model.fireTableDataChanged();
    }

    private void handleCreateProject(JFrame frame, TaskTableModel model, Font scaledFont) {
        // TODO: Implement project creation logic here
    }

    private void handleRemoveSelectedTask(JTable table, TaskTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Task task = model.getTaskAt(modelRow);
        todoList.removeTask(task);
        model.fireTableDataChanged();
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

    private static class TaskTableModel extends AbstractTableModel {
        private final String[] columns = {"Done", "Task", "Due Date"};
        private final List<Task> tasks;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        TaskTableModel(List<Task> tasks) {
            this.tasks = tasks;
            sortByDueDate();
        }

        void sortByDueDate() {
            tasks.sort(Comparator.comparing(Task::getDueDate));
        }

        Task getTaskAt(int rowIndex) {
            return tasks.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return tasks.size();
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
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Task task = tasks.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> task.isChecked();
                case 1 -> task.getName();
                case 2 -> task.getDueDate().format(dateFormatter);
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0 && aValue instanceof Boolean checked) {
                tasks.get(rowIndex).setChecked(checked);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    private static class DueDateColorTextRenderer extends DefaultTableCellRenderer {
        private final TaskTableModel model;

        DueDateColorTextRenderer(TaskTableModel model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            applyRowColor(component, table, model.getTaskAt(table.convertRowIndexToModel(row)), isSelected);
            return component;
        }
    }

    private static class DueDateColorCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        private final TaskTableModel model;

        DueDateColorCheckBoxRenderer(TaskTableModel model) {
            this.model = model;
            setHorizontalAlignment(CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            setSelected(Boolean.TRUE.equals(value));
            applyRowColor(this, table, model.getTaskAt(table.convertRowIndexToModel(row)), isSelected);
            return this;
        }
    }

    private static void applyRowColor(Component component, JTable table, Task task, boolean isSelected) {
        if (isSelected) {
            component.setBackground(table.getSelectionBackground());
            component.setForeground(table.getSelectionForeground());
            return;
        }

        long daysUntilDue = task.calculateDaysUntilDue();
        if (daysUntilDue < 0) {
            component.setBackground(new Color(255, 199, 206));
        } else if (daysUntilDue == 1) {
            component.setBackground(new Color(255, 242, 204));
        } else {
            component.setBackground(Color.WHITE);
        }
        component.setForeground(Color.BLACK);
    }
}