package de.nordakademie;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;
import de.nordakademie.ui.UI;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TodoList todoList = new TodoList("Haushalt");
        todoList.addTask(new Task("Lebensmittel einkaufen", LocalDate.now().plusDays(1)));
        todoList.addTask(new Task("Hausaufgaben beenden", LocalDate.now().plusDays(3)));
        todoList.addTask(new Task("Zahnarztermin vereinbaren", LocalDate.now().plusDays(7)));

        SwingUtilities.invokeLater(() -> new UI(todoList).show());
    }
}