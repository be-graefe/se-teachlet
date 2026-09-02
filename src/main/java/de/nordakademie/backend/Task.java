package de.nordakademie.backend;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class Task {
    private final String name;
    private final LocalDate faelligkeitsdatum;
    private boolean erledigt;

    public Task(String name, LocalDate dueDate) {
        this.name = name;
        this.faelligkeitsdatum = dueDate;
    }

    public long berechneTageBisFaelligkeitsdatum() {
        LocalDate today = LocalDate.now();
        return DAYS.between(today, faelligkeitsdatum);
    }

    public String getName() {
        return name;
    }

    public LocalDate getFaelligkeitsdatum() {
        return faelligkeitsdatum;
    }

    public boolean isErledigt() {
        return erledigt;
    }

    public void setErledigt(boolean erledigt) {
        this.erledigt = erledigt;
    }

}