package de.nordakademie;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.TodoList;
import de.nordakademie.ui.UI;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TodoList haushalt = new TodoList("Haushalt");
        haushalt.addChild(new Task("Lebensmittel einkaufen", LocalDate.now().plusDays(1)));
        haushalt.addChild(new Task("Hausaufgaben beenden", LocalDate.now().plusDays(3)));
        haushalt.addChild(new Task("Zahnarztermin vereinbaren", LocalDate.now().plusDays(7)));
        TodoList putzen = new TodoList("Putzen");
        putzen.addChild(new Task("Zimmer saugen", LocalDate.now().plusDays(1)));
        putzen.addChild(new Task("Boden wischen", LocalDate.now().plusDays(1)));
        haushalt.addChild(putzen);

        SwingUtilities.invokeLater(() -> new UI(haushalt).show());
    }
}