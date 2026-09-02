package de.nordakademie;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;
import de.nordakademie.ui.UI;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TodoList haushalt = new TodoList("Haushalt");
        haushalt.addTask(new Task("Lebensmittel einkaufen", LocalDate.now().plusDays(1)));
        haushalt.addTask(new Task("Hausaufgaben beenden", LocalDate.now().plusDays(3)));
        haushalt.addTask(new Task("Zahnarztermin vereinbaren", LocalDate.now().plusDays(7)));
        TodoList putzen = new TodoList("Putzen");
        putzen.addTask(new Task("Zimmer saugen", LocalDate.now().plusDays(1)));
        putzen.addTask(new Task("Boden wischen", LocalDate.now().plusDays(1)));
        haushalt.addTask(putzen);

        SwingUtilities.invokeLater(() -> new UI(haushalt).show());
    }
}