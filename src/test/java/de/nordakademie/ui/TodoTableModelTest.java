package de.nordakademie.ui;

import de.nordakademie.backend.Project;
import de.nordakademie.backend.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.swing.event.TableModelEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für {@link TodoTableModel}. Das Modell kriegt selbstgebauten
 * {@link java.util.function.Supplier} statt echtem Backend, um das Modell
 * ohne {@link RowBuilder} zu testen.
 *
 * @author Lennart Hell
 */
@DisplayName("TodoTableModel")
class TodoTableModelTest {

    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project("Test");
        task = new Task("Aufgabe", LocalDate.now().plusDays(2));
        project.addChild(task);
    }

    private TodoTableModel newModelBackedByProjekt() {
        return new TodoTableModel(() -> RowBuilder.build(project));
    }

    @Nested
    @DisplayName("Spaltenmetadaten")
    class ColumnMetadata {

        @Test
        @DisplayName("getColumnCount() entspricht der Anzahl der Column-Enum-Werte")
        void testColumnCountMatchesColumnEnum_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            assertEquals(Column.values().length, model.getColumnCount());
        }

        @Test
        @DisplayName("getColumnName() und getColumnClass() delegieren an die jeweilige Column")
        void testColumnNameAndClassDelegateToColumn_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            assertEquals(Column.NAME.title(), model.getColumnName(Column.NAME.ordinal()));
            assertEquals(Column.NAME.type(), model.getColumnClass(Column.NAME.ordinal()));
        }
    }

    @Nested
    @DisplayName("Zeilendaten")
    class RowData {

        @Test
        @DisplayName("getRowCount() entspricht der Anzahl der von RowBuilder gelieferten Zeilen")
        void testRowCountMatchesBuiltRows_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            assertEquals(2, model.getRowCount()); // Projekt-Kopfzeile + eine Aufgabe
        }

        @Test
        @DisplayName("getValueAt() liefert für die NAME-Spalte den Namen der jeweiligen Zeile")
        void testGetValueAtDelegatesToColumn_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            assertEquals("Aufgabe", model.getValueAt(1, Column.NAME.ordinal()));
        }

        @Test
        @DisplayName("getRow() liefert dieselbe Row-Instanz, die auch getValueAt() zugrunde liegt")
        void testGetRowReturnsUnderlyingRow_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            Row row = model.getRow(1);

            assertEquals("Aufgabe", row.name());
        }

        @Test
        @DisplayName("isCellEditable() delegiert an Column.isEditable() und berücksichtigt die konkrete Zeile")
        void testIsCellEditableDelegatesToColumn_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            // Zeile 0 (Projekt-Kopfzeile) ist abhakbar -> DONE editierbar
            assertTrue(model.isCellEditable(0, Column.DONE.ordinal()));
            // NAME ist nie editierbar
            assertFalse(model.isCellEditable(0, Column.NAME.ordinal()));
        }
    }

    @Nested
    @DisplayName("Schreiben (setValueAt)")
    class WritingValues {

        @Test
        @DisplayName("setValueAt() für DONE hakt den zugrunde liegenden Task ab")
        void testSetValueAtOnDoneColumnChecksUnderlyingTask_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            model.setValueAt(Boolean.TRUE, 1, Column.DONE.ordinal());

            assertTrue(task.isChecked());
        }

        @Test
        @DisplayName("setValueAt() liest die Zeilen anschließend neu ein, sodass der neue Status sichtbar ist")
        void testSetValueAtRefreshesModelAfterwards_positive() {
            TodoTableModel model = newModelBackedByProjekt();

            model.setValueAt(Boolean.TRUE, 1, Column.DONE.ordinal());

            assertEquals(Boolean.TRUE, model.getValueAt(1, Column.DONE.ordinal()));
        }
    }

    @Nested
    @DisplayName("Änderungsbenachrichtigung (refresh)")
    class RefreshNotifications {

        @Test
        @DisplayName("Bleibt die Zeilenanzahl gleich, wird ein auf die vorhandenen Zeilen begrenztes Update gemeldet")
        void testRefreshFiresBoundedRowsUpdatedWhenRowCountUnchanged_positive() {
            TodoTableModel model = newModelBackedByProjekt();
            List<TableModelEvent> empfangeneEreignisse = new ArrayList<>();
            model.addTableModelListener(empfangeneEreignisse::add);

            task.setChecked(true); // ändert nur den Inhalt, nicht die Zeilenanzahl
            model.refresh();

            assertEquals(1, empfangeneEreignisse.size());
            TableModelEvent event = empfangeneEreignisse.getFirst();
            assertEquals(0, event.getFirstRow());
            // Bei fireTableRowsUpdated(first, last) ist lastRow konkret (nicht Integer.MAX_VALUE) -
            // das unterscheidet ein begrenztes Update von einem vollständigen Datenwechsel.
            assertEquals(model.getRowCount() - 1, event.getLastRow());
        }

        @Test
        @DisplayName("Ändert sich die Zeilenanzahl, wird ein vollständiger Datenwechsel (unbegrenzter Zeilenbereich) gemeldet")
        void testRefreshFiresFullDataChangedWhenRowCountChanges_positive() {
            TodoTableModel model = newModelBackedByProjekt();
            List<TableModelEvent> empfangeneEreignisse = new ArrayList<>();
            model.addTableModelListener(empfangeneEreignisse::add);

            project.addChild(new Task("Neue Aufgabe", LocalDate.now())); // ändert die Zeilenanzahl
            model.refresh();

            assertEquals(1, empfangeneEreignisse.size());
            // fireTableDataChanged() meldet lastRow = Integer.MAX_VALUE als Signal für "gesamte Tabelle".
            assertEquals(Integer.MAX_VALUE, empfangeneEreignisse.getFirst().getLastRow());
        }
    }
}