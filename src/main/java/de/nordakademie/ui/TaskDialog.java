package de.nordakademie.ui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Eingabedialog für eine neue Aufgabe.
 *
 * <p>Ein zusätzliches Eingabefeld (z. B. Bearbeitungszeit) ist eine weitere Zeile im
 * Formular und eine weitere Komponente in {@link Input}.</p>
 */
public final class TaskDialog {

    /** Die eingegebenen Werte. */
    public record Input(String name, LocalDate dueDate) {
    }

    private TaskDialog() {
    }

    /** Zeigt den Dialog, bis die Eingabe gültig ist oder abgebrochen wird. */
    public static Optional<Input> show(Component parent, String title) {
        JTextField nameField = new JTextField(18);
        JTextField dueDateField = new JTextField(Dates.format(LocalDate.now()), 18);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Fällig am (" + Dates.PATTERN + "):"));
        form.add(dueDateField);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(
                    parent, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError(parent, "Bitte einen Namen eingeben.");
                continue;
            }

            Optional<LocalDate> dueDate = Dates.parse(dueDateField.getText());
            if (dueDate.isEmpty()) {
                showError(parent, "Bitte ein gültiges Datum im Format " + Dates.PATTERN + " eingeben.");
                continue;
            }

            return Optional.of(new Input(name, dueDate.get()));
        }
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
    }
}
