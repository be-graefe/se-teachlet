package de.nordakademie.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Einheitliches Datumsformat für Anzeige und Eingabe. */
public final class Dates {

    public static final String PATTERN = "dd.MM.yyyy";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private Dates() {
    }

    public static String format(LocalDate date) {
        return date == null ? "" : date.format(FORMATTER);
    }

    public static Optional<LocalDate> parse(String text) {
        try {
            return Optional.of(LocalDate.parse(text.trim(), FORMATTER));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
