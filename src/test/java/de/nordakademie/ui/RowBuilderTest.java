package de.nordakademie.ui;

import de.nordakademie.backend.Project;
import de.nordakademie.backend.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für {@link RowBuilder}
 *
 * @author Lennart Hell
 */
@DisplayName("RowBuilder")
class Rowbuildertest {

    @Test
    @DisplayName("Ein einzelnes Blatt-Projekt ohne Kinder erzeugt genau eine Zeile auf Tiefe 0")
    void testBuildOnEmptyProjectReturnsSingleRootRow_positive() {
        Project wurzel = new Project("Leer");

        List<Row> rows = RowBuilder.build(wurzel);

        assertEquals(1, rows.size());
        assertEquals(0, rows.getFirst().depth());
        assertEquals("Leer", rows.getFirst().name());
    }

    @Test
    @DisplayName("Die Baumstruktur wird in eine flache Liste überführt")
    void testBuildFlattensTreeInDepthFirstPreOrder_positive() {
        Project haushalt = new Project("Haushalt");
        Task einkaufen = new Task("Einkaufen", LocalDate.now());
        Project putzen = new Project("Putzen");
        Task staubsaugen = new Task("Staubsaugen", LocalDate.now());
        putzen.addChild(staubsaugen);
        haushalt.addChild(einkaufen);
        haushalt.addChild(putzen);

        List<Row> rows = RowBuilder.build(haushalt);

        List<String> namenInReihenfolge = rows.stream().map(Row::name).toList();
        assertEquals(List.of("Haushalt", "Einkaufen", "Putzen", "Staubsaugen"), namenInReihenfolge);
    }

    @Test
    @DisplayName("Die Einrückungstiefe entspricht der Verschachtelungstiefe im Baum")
    void testBuildAssignsDepthAccordingToNestingLevel_positive() {
        Project haushalt = new Project("Haushalt");
        Project putzen = new Project("Putzen");
        Task staubsaugen = new Task("Staubsaugen", LocalDate.now());
        putzen.addChild(staubsaugen);
        haushalt.addChild(putzen);

        List<Row> rows = RowBuilder.build(haushalt);

        assertEquals(0, rows.get(0).depth()); // Haushalt
        assertEquals(1, rows.get(1).depth()); // Putzen
        assertEquals(2, rows.get(2).depth()); // Staubsaugen
    }

    @Nested
    @DisplayName("group-Flag und Zeilentyp")
    class RowKind {

        @Test
        @DisplayName("Projekte werden als Gruppenzeile abgebildet")
        void testProjectBecomesGroupRow_positive() {
            Project projekt = new Project("Test");

            List<Row> rows = RowBuilder.build(projekt);

            assertTrue(rows.getFirst().group());
        }

        @Test
        @DisplayName("Tasks werden als Nichtgruppenzeile abgebildet")
        void testTaskBecomesNonGroupRow_negative() {
            Project projekt = new Project("Test");
            projekt.addChild(new Task("Aufgabe", LocalDate.now()));

            List<Row> rows = RowBuilder.build(projekt);

            assertFalse(rows.get(1).group());
        }
    }

    @Nested
    @DisplayName("Löschen (onDelete)")
    class Deletion {

        @Test
        @DisplayName("Die Wurzelzeile hat keinen Elternknoten und ist deshalb nicht löschbar")
        void testRootRowIsNotDeletable_negative() {
            Project wurzel = new Project("Wurzel");

            List<Row> rows = RowBuilder.build(wurzel);

            assertFalse(rows.getFirst().canDelete());
        }

        @Test
        @DisplayName("Kind-Zeilen sind löschbar, und das Löschen entfernt sie beim Elternknoten")
        void testChildRowDeleteRemovesItFromParent_positive() {
            Project projekt = new Project("Test");
            Task task = new Task("Aufgabe", LocalDate.now());
            projekt.addChild(task);

            List<Row> rows = RowBuilder.build(projekt);
            Row taskZeile = rows.get(1);
            assertTrue(taskZeile.canDelete());

            taskZeile.delete();

            assertFalse(projekt.getChildren().contains(task));
        }
    }

    @Nested
    @DisplayName("Ziel für neue Einträge (target/canAdd)")
    class TargetForNewEntries {

        @Test
        @DisplayName("Bei einem Projekt ist das Projekt selbst das Ziel für neue Einträge")
        void testProjectRowTargetsItself_positive() {
            Project projekt = new Project("Test");

            List<Row> rows = RowBuilder.build(projekt);

            assertTrue(rows.getFirst().canAdd());
            assertEquals(projekt, rows.getFirst().target());
        }

        @Test
        @DisplayName("Bei einem Task ist die enthaltende Liste das Ziel für neue Einträge")
        void testTaskRowTargetsItsParentList_positive() {
            Project projekt = new Project("Test");
            Task task = new Task("Aufgabe", LocalDate.now());
            projekt.addChild(task);

            List<Row> rows = RowBuilder.build(projekt);
            Row taskZeile = rows.get(1);

            assertTrue(taskZeile.canAdd());
            assertEquals(projekt, taskZeile.target());
        }
    }

    @Test
    @DisplayName("toggle() einer erzeugten Zeile geht zum Backend durch")
    void testToggleOnBuiltRowUpdatesBackend_positive() {
        Project projekt = new Project("Test");
        Task task = new Task("Aufgabe", LocalDate.now());
        projekt.addChild(task);

        List<Row> rows = RowBuilder.build(projekt);
        rows.get(1).toggle(true);

        assertTrue(task.isChecked());
    }

    @Test
    @DisplayName("Fälligkeitsdatum und Erledigt-Status der Zeile entsprechen dem zugrunde liegenden Backend-Objekt")
    void testRowReflectsUnderlyingDueDateAndCheckedState_positive() {
        LocalDate faelligkeit = LocalDate.now().plusDays(4);
        Task task = new Task("Aufgabe", faelligkeit);
        task.setChecked(true);
        Project projekt = new Project("Test");
        projekt.addChild(task);

        List<Row> rows = RowBuilder.build(projekt);
        Row taskZeile = rows.get(1);

        assertEquals(faelligkeit, taskZeile.dueDate());
        assertEquals(Boolean.TRUE, taskZeile.done());
    }
}