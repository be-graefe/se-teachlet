package de.nordakademie.backend;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests für {@link Task}, das Blatt (Leaf) des Kompositum-Musters.
 *
 * @author Lennart Hell
 */
@DisplayName("Task (Leaf)")
class TaskTest {

    @Nested
    @DisplayName("Konstruktion und einfache Zugriffe")
    class Basics {

        @Test
        @DisplayName("Name und Fälligkeitsdatum werden aus dem Konstruktor übernommen")
        void testConstructorStoresNameAndDueDate_positive() {
            LocalDate dueDate = LocalDate.of(2026, 9, 10);

            Task task = new Task("Bericht schreiben", dueDate);

            assertEquals("Bericht schreiben", task.getName());
            assertEquals(dueDate, task.getDueDate());
        }

        @Test
        @DisplayName("Ein Task hat einen null-Fälligkeitsdatum, wenn keines übergeben wurde")
        void testConstructorAllowsNullDueDate_positive() {
            Task task = new Task("Ohne Frist", null);

            assertEquals(null, task.getDueDate());
        }

        @Test
        @DisplayName("Ein neu erzeugter Task ist standardmäßig nicht erledigt")
        void testNewTaskIsUncheckedByDefault_positive() {
            Task task = new Task("Neue Aufgabe", LocalDate.now());

            assertFalse(task.isChecked());
        }

        @Test
        @DisplayName("Ein Task ist niemals ein Projekt")
        void testIsProjectReturnsFalse_negative() {
            Task task = new Task("Aufgabe", LocalDate.now());

            assertFalse(task.isProject());
        }
    }

    @Nested
    @DisplayName("Erledigt-Status")
    class CheckedState {

        @Test
        @DisplayName("setChecked(true) markiert den Task als erledigt")
        void testSetCheckedTrue_positive() {
            Task task = new Task("Code reviewen", LocalDate.now().plusDays(1));

            task.setChecked(true);

            assertTrue(task.isChecked());
        }

        @Test
        @DisplayName("Ein bereits erledigter Task kann wieder auf offen gesetzt werden")
        void testSetCheckedFalseAfterTrue_positive() {
            Task task = new Task("Code reviewen", LocalDate.now().plusDays(1));
            task.setChecked(true);

            task.setChecked(false);

            assertFalse(task.isChecked());
        }
    }

    @Nested
    @DisplayName("Verhalten als Blatt des Kompositums")
    class LeafBehaviour {

        @Test
        @DisplayName("Ein Task hat immer eine leere Kinderliste")
        void testGetChildrenIsAlwaysEmpty_positive() {
            Task task = new Task("Aufgabe", LocalDate.now());

            List<ITodoList> children = task.getChildren();

            assertTrue(children.isEmpty());
        }

        @Test
        @DisplayName("addChild wirft UnsupportedOperationException, da ein Blatt keine Kinder aufnehmen kann")
        void testAddChildThrowsUnsupportedOperationException_negative() {
            Task task = new Task("Aufgabe", LocalDate.now());
            Task versuchtesKind = new Task("Kind", LocalDate.now());

            assertThrows(UnsupportedOperationException.class, () -> task.addChild(versuchtesKind));
        }

        @Test
        @DisplayName("removeChild wirft UnsupportedOperationException, da ein Blatt keine Kinder besitzt")
        void testRemoveChildThrowsUnsupportedOperationException_negative() {
            Task task = new Task("Aufgabe", LocalDate.now());
            Task fremderTask = new Task("Fremd", LocalDate.now());

            assertThrows(UnsupportedOperationException.class, () -> task.removeChild(fremderTask));
        }
    }
}

