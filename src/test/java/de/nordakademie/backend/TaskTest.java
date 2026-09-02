package de.nordakademie.backend;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void taskShouldStoreNameAndDueDate() {
        LocalDate dueDate = LocalDate.now().plusDays(3);

        Task task = new Task("Write report", dueDate);

        assertEquals("Write report", task.getName());
        assertEquals(dueDate, task.getFaelligkeitsdatum());
        assertFalse(task.isErledigt());
    }

    @Test
    void taskShouldCalculateDaysUntilDue() {
        LocalDate dueDate = LocalDate.now().plusDays(4);

        Task task = new Task("Prepare lecture", dueDate);

        assertEquals(4, task.berechneTageBisFaelligkeitsdatum());
    }

    @Test
    void taskShouldToggleCheckedState() {
        Task task = new Task("Review code", LocalDate.now().plusDays(1));

        task.setErledigt(true);

        assertTrue(task.isErledigt());
    }
}
