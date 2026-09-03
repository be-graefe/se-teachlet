package de.nordakademie.backend;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoListTest {

    @Test
    void todoListShouldAddTasks() {
        TodoList todoList = new TodoList("Test");
        Task task = new Task("Buy milk", LocalDate.now().plusDays(2));

        todoList.addChild(task);

        assertEquals(1, todoList.getChildren().size());
        assertTrue(todoList.getChildren().contains(task));
    }

    @Test
    void todoListShouldRemoveTasks() {
        TodoList todoList = new TodoList("Test");
        Task task = new Task("Call mom", LocalDate.now().plusDays(5));
        todoList.addChild(task);

        todoList.removeChild(task);

        assertTrue(todoList.getChildren().isEmpty());
    }

    @Test
    void todoListShouldStoreName() {
        TodoList todoList = new TodoList("Haushalt");

        assertEquals("Haushalt", todoList.getName());
    }

    @Test
    void todoListShouldBeProjekt() {
        TodoList todoList = new TodoList("Test");

        assertTrue(todoList.isProject());
    }

    @Test
    void todoListShouldKeepAddedTasksInOrderOfInsertion() {
        TodoList todoList = new TodoList("Test");
        Task first = new Task("First", LocalDate.now().plusDays(1));
        Task second = new Task("Second", LocalDate.now().plusDays(2));

        todoList.addChild(first);
        todoList.addChild(second);

        assertEquals(first, todoList.getChildren().get(0));
        assertEquals(second, todoList.getChildren().get(1));
        assertFalse(todoList.getChildren().isEmpty());
    }
}
