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
 * Tests für {@link Projekt}
 *
 * @author  Lennart Hell
 */
@DisplayName("Projekt (Composite)")
class ProjektTest {

    @Nested
    @DisplayName("Konstruktion und einfache Zugriffe")
    class Basics {

        @Test
        @DisplayName("Der Name wird aus dem Konstruktor übernommen")
        void testConstructorStoresName_positive() {
            Projekt projekt = new Projekt("Haushalt");

            assertEquals("Haushalt", projekt.getName());
        }

        @Test
        @DisplayName("Ein Projekt ist immer ein Projekt")
        void testIsProjectReturnsTrue_positive() {
            Projekt projekt = new Projekt("Test");

            assertTrue(projekt.isProject());
        }

        @Test
        @DisplayName("Ein neu erzeugtes Projekt hat keine Kinder")
        void testNewProjectHasNoChildren_positive() {
            Projekt projekt = new Projekt("Test");

            assertTrue(projekt.getChildren().isEmpty());
        }
    }

    @Nested
    @DisplayName("Kinder hinzufügen und entfernen")
    class ChildManagement {

        @Test
        @DisplayName("addChild fügt sowohl Tasks als auch verschachtelte Projekte hinzu")
        void testAddChildAcceptsTasksAndProjects_positive() {
            Projekt haushalt = new Projekt("Haushalt");
            Task einkaufen = new Task("Einkaufen", LocalDate.now().plusDays(1));
            Projekt putzen = new Projekt("Putzen");

            haushalt.addChild(einkaufen);
            haushalt.addChild(putzen);

            assertEquals(2, haushalt.getChildren().size());
            assertTrue(haushalt.getChildren().contains(einkaufen));
            assertTrue(haushalt.getChildren().contains(putzen));
        }

        @Test
        @DisplayName("Kinder behalten die gleiche Reihenfolge beim Einfügen")
        void testAddChildPreservesInsertionOrder_positive() {
            Projekt projekt = new Projekt("Test");
            Task erste = new Task("Erste", LocalDate.now());
            Task zweite = new Task("Zweite", LocalDate.now());

            projekt.addChild(erste);
            projekt.addChild(zweite);

            List<ITodoList> children = projekt.getChildren();
            assertEquals(erste, children.get(0));
            assertEquals(zweite, children.get(1));
        }

        @Test
        @DisplayName("removeChild entfernt genau das übergebene Kind")
        void testRemoveChildRemovesGivenChild_positive() {
            Projekt projekt = new Projekt("Test");
            Task bleibt = new Task("Bleibt", LocalDate.now());
            Task wirdEntfernt = new Task("Wird entfernt", LocalDate.now());
            projekt.addChild(bleibt);
            projekt.addChild(wirdEntfernt);

            projekt.removeChild(wirdEntfernt);

            assertEquals(1, projekt.getChildren().size());
            assertTrue(projekt.getChildren().contains(bleibt));
            assertFalse(projekt.getChildren().contains(wirdEntfernt));
        }

        @Test
        @DisplayName("removeChild eines nicht enthaltenen Kindes ändert die Liste nicht und wirft nicht exception")
        void testRemoveChildOnAbsentChildIsNoOp_negative() {
            Projekt projekt = new Projekt("Test");
            Task vorhanden = new Task("Vorhanden", LocalDate.now());
            Task nieHinzugefuegt = new Task("Nie hinzugefügt", LocalDate.now());
            projekt.addChild(vorhanden);

            projekt.removeChild(nieHinzugefuegt);

            assertEquals(1, projekt.getChildren().size());
        }

        @Test
        @DisplayName("Kompositum kann rekursiv verschachtelt werden (Projekt in Projekt)")
        void testCompositeStructureSupportsNesting_positive() {
            Projekt haushalt = new Projekt("Haushalt");
            Projekt putzen = new Projekt("Putzen");
            Task staubsaugen = new Task("Staubsaugen", LocalDate.now());
            putzen.addChild(staubsaugen);
            haushalt.addChild(putzen);

            ITodoList wiederGefundenesUnterprojekt = haushalt.getChildren().get(0);

            assertTrue(wiederGefundenesUnterprojekt.isProject());
            assertEquals(1, wiederGefundenesUnterprojekt.getChildren().size());
            assertTrue(wiederGefundenesUnterprojekt.getChildren().contains(staubsaugen));
        }
    }

    @Nested
    @DisplayName("Fälligkeitsdatum")
    class DueDate {

        @Test
        @DisplayName("Ein Projekt ohne Kinder hat kein Fälligkeitsdatum")
        void testGetDueDateOnEmptyProjectReturnsNull_negative() {
            Projekt projekt = new Projekt("Leer");

            assertNull(projekt.getDueDate());
        }

        @Test
        @DisplayName("Ein Projekt, dessen Kinder allesamt kein Fälligkeitsdatum haben, liefert null")
        void testGetDueDateWithoutAnyDueDatesReturnsNull_negative() {
            Projekt projekt = new Projekt("Test");
            projekt.addChild(new Task("Ohne Frist 1", null));
            projekt.addChild(new Task("Ohne Frist 2", null));

            assertNull(projekt.getDueDate());
        }

        @Test
        @DisplayName("Bereits erledigte Kinder zählen bei der Fälligkeitsberechnung nicht mit")
        void testGetDueDateIgnoresCheckedChildren_positive() {
            Projekt projekt = new Projekt("Test");
            Task erledigtMitFrueherFrist = new Task("Erledigt", LocalDate.now());
            erledigtMitFrueherFrist.setChecked(true);
            Task offenMitSpaeterFrist = new Task("Offen", LocalDate.now().plusDays(10));
            projekt.addChild(erledigtMitFrueherFrist);
            projekt.addChild(offenMitSpaeterFrist);

            assertEquals(LocalDate.now().plusDays(10), projekt.getDueDate());
        }
    }

    @Nested
    @DisplayName("Erledigt-Status (rekursiv über die Struktur)")
    class CheckedState {

        @Test
        @DisplayName("Ein Projekt ohne Kinder gilt nicht als erledigt")
        void testEmptyProjectIsNotChecked_negative() {
            Projekt projekt = new Projekt("Leer");

            assertFalse(projekt.isChecked());
        }

        @Test
        @DisplayName("Ein Projekt ist erst dann erledigt, wenn alle Kinder erledigt sind")
        void testProjectIsCheckedOnlyWhenAllChildrenAreChecked_positive() {
            Projekt projekt = new Projekt("Test");
            Task erledigt = new Task("Erledigt", LocalDate.now());
            erledigt.setChecked(true);
            Task offen = new Task("Offen", LocalDate.now());
            projekt.addChild(erledigt);
            projekt.addChild(offen);

            assertFalse(projekt.isChecked());

            offen.setChecked(true);

            assertTrue(projekt.isChecked());
        }

        @Test
        @DisplayName("setChecked wirkt rekursiv auf alle Kinder, auch über Unterprojekte hinweg")
        void testSetCheckedPropagatesRecursivelyThroughSubprojects_positive() {
            Projekt haushalt = new Projekt("Haushalt");
            Task einkaufen = new Task("Einkaufen", LocalDate.now());
            Projekt putzen = new Projekt("Putzen");
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
            Projekt projekt = new Projekt("Test");
            Task task = new Task("Aufgabe", LocalDate.now());
            projekt.addChild(task);
            projekt.setChecked(true);

            projekt.setChecked(false);

            assertFalse(task.isChecked());
            assertFalse(projekt.isChecked());
        }
    }
}