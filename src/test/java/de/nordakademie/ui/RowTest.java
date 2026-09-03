package de.nordakademie.ui;

import de.nordakademie.backend.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für {@link Row}.
 *
 * @author  Lennart Hell
 */
@DisplayName("Row")
class RowTest {

    @Nested
    @DisplayName("Fähigkeiten (canToggle/canDelete/canAdd)")
    class Capabilities {

        @Test
        @DisplayName("Eine Zeile mit onDone/onDelete/target kann: abhaken, löschen, Ziel für neue Einträge")
        void testFullyCapableRow_positive() {
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> { }, () -> { }, new Project("Ziel"));

            assertTrue(row.canToggle());
            assertTrue(row.canDelete());
            assertTrue(row.canAdd());
        }

        @Test
        @DisplayName("Eine Zeile ohne onDelete (z. B. die Wurzel) kann nicht gelöscht werden")
        void testRowWithoutOnDeleteCannotBeDeleted_negative() {
            Row wurzelZeile = Row.forList(0, "Wurzel", null, false, value -> { }, null, new Project("Wurzel"));

            assertFalse(wurzelZeile.canDelete());
        }

        @Test
        @DisplayName("Eine Zeile ohne onDone kann nicht abgehakt werden")
        void testRowWithoutOnDoneCannotBeToggled_negative() {
            Row zeileOhneStatus = new Row(0, false, "Info", null, null, null, () -> { }, null);

            assertFalse(zeileOhneStatus.canToggle());
        }

        @Test
        @DisplayName("Eine Zeile ohne target ist kein gültiges Ziel für neue Einträge")
        void testRowWithoutTargetCannotReceiveNewEntries_negative() {
            Row zeileOhneZiel = Row.forTask(1, "Aufgabe", null, false, value -> { }, () -> { }, null);

            assertFalse(zeileOhneZiel.canAdd());
        }
    }

    @Nested
    @DisplayName("Aktionen (toggle/delete)")
    class Actions {

        @Test
        @DisplayName("toggle() ruft onDone mit dem übergebenen Wert auf, wenn abhakbar")
        void testToggleInvokesOnDoneWithGivenValue_positive() {
            AtomicReference<Boolean> erhaltenerWert = new AtomicReference<>();
            Row row = Row.forTask(0, "Aufgabe", null, false, erhaltenerWert::set, () -> { }, null);

            row.toggle(true);

            assertTrue(erhaltenerWert.get());
        }

        @Test
        @DisplayName("toggle() ist ein No-Op, wenn die Zeile nicht abhakbar ist, statt eine NullPointerException zu werfen")
        void testToggleOnNonToggleableRowIsNoOp_negative() {
            Row zeileOhneStatus = new Row(0, false, "Info", null, null, null, null, null);

            zeileOhneStatus.toggle(true);
            // sollte keinen Nullpointer werfen.
        }

        @Test
        @DisplayName("delete() ruft onDelete auf, wenn löschbar")
        void testDeleteInvokesOnDelete_positive() {
            AtomicBoolean wurdeAufgerufen = new AtomicBoolean(false);
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> { },
                    () -> wurdeAufgerufen.set(true), null);

            row.delete();

            assertTrue(wurdeAufgerufen.get());
        }

        @Test
        @DisplayName("delete() geht nicht, wenn die Zeile nicht löschbar ist")
        void testDeleteOnNonDeletableRowIsNoOp_negative() {
            Row wurzelZeile = Row.forList(0, "Wurzel", null, false, value -> { }, null, new Project("Wurzel"));

            wurzelZeile.delete();
            // Erwartung: kein Wurf einer NullPointerException - das Erreichen dieser Zeile genügt als Nachweis.
        }
    }

    @Test
    @DisplayName("forList erzeugt Gruppenzeile, forTask nicht")
    void testFactoryMethodsSetGroupFlagCorrectly_positive() {
        Row listenZeile = Row.forList(0, "Projekt", LocalDate.now(), false, value -> { }, null, new Project("P"));
        Row taskZeile = Row.forTask(1, "Task", LocalDate.now(), false, value -> { }, null, null);

        assertTrue(listenZeile.group());
        assertFalse(taskZeile.group());
    }
}