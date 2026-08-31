package de.nordakademie.backend;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoListTest {

    @Test
    void todoListShouldAddTasks() {
        TodoList todoList = new TodoList();
        Task task = new Task("Buy milk", LocalDate.now().plusDays(2));

        todoList.addTask(task);

        assertEquals(1, todoList.getTasks().size());
        assertTrue(todoList.getTasks().contains(task));
    }

    @Test
    void todoListShouldRemoveTasks() {
        TodoList todoList = new TodoList();
        Task task = new Task("Call mom", LocalDate.now().plusDays(5));
        todoList.addTask(task);

        todoList.removeTask(task);

        assertTrue(todoList.getTasks().isEmpty());
    }

    @Test
    void todoListShouldSumDaysUntilDue() {
        TodoList todoList = new TodoList();
        todoList.addTask(new Task("Task A", LocalDate.now().plusDays(2)));
        todoList.addTask(new Task("Task B", LocalDate.now().plusDays(5)));

        long total = todoList.calculateAllDaysUntilDue();

        assertEquals(7, total);
    }

    @Test
    void todoListShouldKeepAddedTasksInOrderOfInsertion() {
        TodoList todoList = new TodoList();
        Task first = new Task("First", LocalDate.now().plusDays(1));
        Task second = new Task("Second", LocalDate.now().plusDays(2));

        todoList.addTask(first);
        todoList.addTask(second);

        assertEquals(first, todoList.getTasks().get(0));
        assertEquals(second, todoList.getTasks().get(1));
        assertFalse(todoList.getTasks().isEmpty());
    }
}
