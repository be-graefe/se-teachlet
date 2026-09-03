package de.nordakademie.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für {@link Project}
 *
 * @author  Lennart Hell
 */
@DisplayName("Project (Composite)")
class ProjectTest {

    @Nested
    @DisplayName("Konstruktion und einfache Zugriffe")
    class Basics {

        @Test
        @DisplayName("Der Name wird aus dem Konstruktor übernommen")
        void testConstructorStoresName_positive() {
            Project project = new Project("Haushalt");

            assertEquals("Haushalt", project.getName());
        }

        @Test
        @DisplayName("Ein Project ist immer ein Project")
        void testIsProjectReturnsTrue_positive() {
            Project project = new Project("Test");

            assertTrue(project.isProject());
        }

        @Test
        @DisplayName("Ein neu erzeugtes Project hat keine Kinder")
        void testNewProjectHasNoChildren_positive() {
            Project project = new Project("Test");

            assertTrue(project.getChildren().isEmpty());
        }
    }

    @Nested
    @DisplayName("Kinder hinzufügen und entfernen")
    class ChildManagement {

        @Test
        @DisplayName("addChild fügt sowohl Tasks als auch verschachtelte Projekte hinzu")
        void testAddChildAcceptsTasksAndProjects_positive() {
            Project haushalt = new Project("Haushalt");
            Task einkaufen = new Task("Einkaufen", LocalDate.now().plusDays(1));
            Project putzen = new Project("Putzen");

            haushalt.addChild(einkaufen);
            haushalt.addChild(putzen);

            assertEquals(2, haushalt.getChildren().size());
            assertTrue(haushalt.getChildren().contains(einkaufen));
            assertTrue(haushalt.getChildren().contains(putzen));
        }

        @Test
        @DisplayName("Kinder behalten die gleiche Reihenfolge beim Einfügen")
        void testAddChildPreservesInsertionOrder_positive() {
            Project project = new Project("Test");
            Task erste = new Task("Erste", LocalDate.now());
            Task zweite = new Task("Zweite", LocalDate.now());

            project.addChild(erste);
            project.addChild(zweite);

            List<TaskItem> children = project.getChildren();
            assertEquals(erste, children.get(0));
            assertEquals(zweite, children.get(1));
        }

        @Test
        @DisplayName("removeChild entfernt genau das übergebene Kind")
        void testRemoveChildRemovesGivenChild_positive() {
            Project project = new Project("Test");
            Task bleibt = new Task("Bleibt", LocalDate.now());
            Task wirdEntfernt = new Task("Wird entfernt", LocalDate.now());
            project.addChild(bleibt);
            project.addChild(wirdEntfernt);

            project.removeChild(wirdEntfernt);

            assertEquals(1, project.getChildren().size());
            assertTrue(project.getChildren().contains(bleibt));
            assertFalse(project.getChildren().contains(wirdEntfernt));
        }

        @Test
        @DisplayName("removeChild eines nicht enthaltenen Kindes ändert die Liste nicht und wirft nicht exception")
        void testRemoveChildOnAbsentChildIsNoOp_negative() {
            Project project = new Project("Test");
            Task vorhanden = new Task("Vorhanden", LocalDate.now());
            Task nieHinzugefuegt = new Task("Nie hinzugefügt", LocalDate.now());
            project.addChild(vorhanden);

            project.removeChild(nieHinzugefuegt);

            assertEquals(1, project.getChildren().size());
        }

        @Test
        @DisplayName("Kompositum kann rekursiv verschachtelt werden (Project in Project)")
        void testCompositeStructureSupportsNesting_positive() {
            Project haushalt = new Project("Haushalt");
            Project putzen = new Project("Putzen");
            Task staubsaugen = new Task("Staubsaugen", LocalDate.now());
            putzen.addChild(staubsaugen);
            haushalt.addChild(putzen);

            TaskItem wiederGefundenesUnterproject = haushalt.getChildren().get(0);

            assertTrue(wiederGefundenesUnterproject.isProject());
            assertEquals(1, wiederGefundenesUnterproject.getChildren().size());
            assertTrue(wiederGefundenesUnterproject.getChildren().contains(staubsaugen));
        }
    }

    @Nested
    @DisplayName("Fälligkeitsdatum")
    class DueDate {

        @Test
        @DisplayName("Ein Project ohne Kinder hat kein Fälligkeitsdatum")
        void testGetDueDateOnEmptyProjectReturnsNull_negative() {
            Project project = new Project("Leer");

            assertNull(project.getDueDate());
        }

        @Test
        @DisplayName("Ein Project, dessen Kinder allesamt kein Fälligkeitsdatum haben, liefert null")
        void testGetDueDateWithoutAnyDueDatesReturnsNull_negative() {
            Project project = new Project("Test");
            project.addChild(new Task("Ohne Frist 1", null));
            project.addChild(new Task("Ohne Frist 2", null));

            assertNull(project.getDueDate());
        }

        @Test
        @DisplayName("Bereits erledigte Kinder zählen bei der Fälligkeitsberechnung nicht mit")
        void testGetDueDateIgnoresCheckedChildren_positive() {
            Project project = new Project("Test");
            Task erledigtMitFrueherFrist = new Task("Erledigt", LocalDate.now());
            erledigtMitFrueherFrist.setChecked(true);
            Task offenMitSpaeterFrist = new Task("Offen", LocalDate.now().plusDays(10));
            project.addChild(erledigtMitFrueherFrist);
            project.addChild(offenMitSpaeterFrist);

            assertEquals(LocalDate.now().plusDays(10), project.getDueDate());
        }
    }

    @Nested
    @DisplayName("Erledigt-Status (rekursiv über die Struktur)")
    class CheckedState {

        @Test
        @DisplayName("Ein Project ohne Kinder gilt nicht als erledigt")
        void testEmptyProjectIsNotChecked_negative() {
            Project project = new Project("Leer");

            assertFalse(project.isChecked());
        }

        @Test
        @DisplayName("Ein Project ist erst dann erledigt, wenn alle Kinder erledigt sind")
        void testProjectIsCheckedOnlyWhenAllChildrenAreChecked_positive() {
            Project project = new Project("Test");
            Task erledigt = new Task("Erledigt", LocalDate.now());
            erledigt.setChecked(true);
            Task offen = new Task("Offen", LocalDate.now());
            project.addChild(erledigt);
            project.addChild(offen);

            assertFalse(project.isChecked());

            offen.setChecked(true);

            assertTrue(project.isChecked());
        }

        @Test
        @DisplayName("setChecked wirkt rekursiv auf alle Kinder, auch über Unterprojekte hinweg")
        void testSetCheckedPropagatesRecursivelyThroughSubprojects_positive() {
            Project haushalt = new Project("Haushalt");
            Task einkaufen = new Task("Einkaufen", LocalDate.now());
            Project putzen = new Project("Putzen");
            Task staubsaugen = new Task("Staubsaugen", LocalDate.now());
            Task wischen = new Task("Wischen", LocalDate.now());
            putzen.addChild(staubsaugen);
            putzen.addChild(wischen);
            haushalt.addChild(einkaufen);
            haushalt.addChild(putzen);

            haushalt.setChecked(true);

            assertTrue(einkaufen.isChecked());
            assertTrue(staubsaugen.isChecked());
            assertTrue(wischen.isChecked());
            assertTrue(putzen.isChecked());
            assertTrue(haushalt.isChecked());
        }

        @Test
        @DisplayName("setChecked(false) hebt den Erledigt-Status rekursiv wieder auf")
        void testSetCheckedFalsePropagatesRecursively_positive() {
            Project project = new Project("Test");
            Task task = new Task("Aufgabe", LocalDate.now());
            project.addChild(task);
            project.setChecked(true);

            project.setChecked(false);

            assertFalse(task.isChecked());
            assertFalse(project.isChecked());
        }
    }
}