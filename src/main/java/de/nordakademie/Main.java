package de.nordakademie;

import de.nordakademie.backend.Task;
import de.nordakademie.backend.Project;
import de.nordakademie.ui.UI;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Project haushalt = new Project("Haushalt");
        haushalt.addChild(new Task("Lebensmittel einkaufen", LocalDate.now().plusDays(1)));
        haushalt.addChild(new Task("Hausaufgaben beenden", LocalDate.now().plusDays(3)));
        haushalt.addChild(new Task("Zahnarztermin vereinbaren", LocalDate.now().plusDays(7)));
        Project putzen = new Project("Putzen");
        putzen.addChild(new Task("Staubsaugen", LocalDate.now()));
        putzen.addChild(new Task("Boden wischen", LocalDate.now()));
        haushalt.addChild(putzen);

        SwingUtilities.invokeLater(() -> new UI(haushalt).show());
    }
}