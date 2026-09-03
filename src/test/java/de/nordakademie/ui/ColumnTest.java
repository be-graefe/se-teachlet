package de.nordakademie.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Column")
class ColumnTest {

    @Nested
    @DisplayName("Spaltenmetadaten")
    class Metadata {

        @Test
        @DisplayName("at() liefert die Spalte an der jeweiligen Ordinalposition")
        void testAtReturnsColumnByIndex_positive() {
            assertEquals(Column.DONE, Column.at(0));
            assertEquals(Column.NAME, Column.at(1));
            assertEquals(Column.DUE_DATE, Column.at(2));
        }

        @Test
        @DisplayName("DONE ist vom Typ Boolean, NAME und DUE_DATE sind vom Typ String")
        void testColumnTypesMatchTableRenderingExpectations_positive() {
            assertEquals(Boolean.class, Column.DONE.type());
            assertEquals(String.class, Column.NAME.type());
            assertEquals(String.class, Column.DUE_DATE.type());
        }
    }

    @Nested
    @DisplayName("Werte lesen (valueOf)")
    class ReadingValues {

        @Test
        @DisplayName("NAME.valueOf() liefert den Namen der Zeile")
        void testNameColumnReturnsRowName_positive() {
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> { }, null, null);

            assertEquals("Aufgabe", Column.NAME.valueOf(row));
        }

        @Test
        @DisplayName("DONE.valueOf() liefert den Erledigt-Status der Zeile unverändert")
        void testDoneColumnReturnsRowDoneState_positive() {
            Row row = Row.forTask(0, "Aufgabe", null, true, value -> { }, null, null);

            assertEquals(Boolean.TRUE, Column.DONE.valueOf(row));
        }

        @Test
        @DisplayName("DUE_DATE.valueOf() formatiert das Datum über Dates.format")
        void testDueDateColumnFormatsDate_positive() {
            LocalDate dueDate = LocalDate.of(2026, 12, 24);
            Row row = Row.forTask(0, "Aufgabe", dueDate, false, value -> { }, null, null);

            assertEquals(Dates.format(dueDate), Column.DUE_DATE.valueOf(row));
        }

        @Test
        @DisplayName("DUE_DATE.valueOf() liefert einen leeren String, wenn die Zeile kein Datum hat")
        void testDueDateColumnWithoutDate_negative() {
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> { }, null, null);

            assertEquals("", Column.DUE_DATE.valueOf(row));
        }
    }

    @Nested
    @DisplayName("Editierbarkeit und Schreiben (isEditable/setValue)")
    class Editing {

        @Test
        @DisplayName("DONE ist nur editierbar, wenn die Zeile abhakbar ist")
        void testDoneColumnEditableOnlyWhenRowIsToggleable_positive() {
            Row abhakbareZeile = Row.forTask(0, "Aufgabe", null, false, value -> { }, null, null);
            Row nichtAbhakbareZeile = new Row(0, false, "Info", null, null, null, null, null);

            assertTrue(Column.DONE.isEditable(abhakbareZeile));
            assertFalse(Column.DONE.isEditable(nichtAbhakbareZeile));
        }

        @Test
        @DisplayName("NAME und DUE_DATE sind standardmäßig nie editierbar")
        void testNameAndDueDateColumnsAreNeverEditable_negative() {
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> { }, null, null);

            assertFalse(Column.NAME.isEditable(row));
            assertFalse(Column.DUE_DATE.isEditable(row));
        }

        @Test
        @DisplayName("DONE.setValue() hakt die Zeile über row.toggle() ab bzw. hakt sie ab")
        void testDoneColumnSetValueTogglesRow_positive() {
            AtomicBoolean erhaltenerWert = new AtomicBoolean(false);
            Row row = Row.forTask(0, "Aufgabe", null, false, erhaltenerWert::set, null, null);

            Column.DONE.setValue(row, Boolean.TRUE);

            assertTrue(erhaltenerWert.get());
        }

        @Test
        @DisplayName("NAME.setValue() ist ein No-Op, da die Spalte reine Anzeige ist")
        void testNameColumnSetValueIsNoOp_negative() {
            AtomicBoolean wurdeAufgerufen = new AtomicBoolean(false);
            Row row = Row.forTask(0, "Aufgabe", null, false, value -> wurdeAufgerufen.set(true), null, null);

            Column.NAME.setValue(row, "Neuer Name");

            assertFalse(wurdeAufgerufen.get());
        }
    }

    @Test
    @DisplayName("Alle drei erwarteten Spalten sind in fester Reihenfolge definiert")
    void testColumnOrderIsStable_positive() {
        Column[] columns = Column.values();

        assertEquals(3, columns.length);
        assertEquals(Column.DONE, columns[0]);
        assertEquals(Column.NAME, columns[1]);
        assertEquals(Column.DUE_DATE, columns[2]);
    }
}