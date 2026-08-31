package de.nordakademie;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;
import de.nordakademie.ui.UI;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        todoList.addTask(new Task("Buy groceries", LocalDate.now().plusDays(1)));
        todoList.addTask(new Task("Finish homework", LocalDate.now().plusDays(3)));
        todoList.addTask(new Task("Call dentist", LocalDate.now().plusDays(7)));

        SwingUtilities.invokeLater(() -> new UI(todoList).show());
    }
}