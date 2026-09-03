package de.nordakademie.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für {@link Dates}
 *
 * @author  Lennart Hell
 */
@DisplayName("Dates")
class DatesTest {

    @Test
    @DisplayName("format() gibt ein Datum im Muster dd.MM.yyyy zurück")
    void testFormatValidDate_positive() {
        LocalDate date = LocalDate.of(2026, 9, 3);

        assertEquals("03.09.2026", Dates.format(date));
    }

    @Test
    @DisplayName("format(null) gibt den leeren String statt einer Exception")
    void testFormatNullDate_negative() {
        assertEquals("", Dates.format(null));
    }

    @Test
    @DisplayName("parse() liest gültiges Datum im Muster richtig ein")
    void testParseValidDate_positive() {
        Optional<LocalDate> parsed = Dates.parse("03.09.2026");

        assertTrue(parsed.isPresent());
        assertEquals(LocalDate.of(2026, 9, 3), parsed.get());
    }

    @Test
    @DisplayName("parse() entfernt umgebende Leerzeichen vor dem Parsen")
    void testParseTrimsWhitespace_positive() {
        Optional<LocalDate> parsed = Dates.parse("  03.09.2026  ");

        assertTrue(parsed.isPresent());
        assertEquals(LocalDate.of(2026, 9, 3), parsed.get());
    }

    @ParameterizedTest(name = "Ungültige Eingabe \"{0}\" liefert Optional.empty()")
    @ValueSource(strings = {"", "keindatum", "31.13.2026", "03/09/2026", "2026-09-03"})
    @DisplayName("parse() liefert für ungültige oder falsch formatierte Eingaben Optional.empty()")
    void testParseInvalidInput_negative(String invalidInput) {
        Optional<LocalDate> parsed = Dates.parse(invalidInput);

        assertTrue(parsed.isEmpty());
    }

    @Test
    @DisplayName("format() und parse() sind für gültige Daten umgekehrt.")
    void testFormatAndParseAreInverse_positive() {
        LocalDate original = LocalDate.of(2027, 1, 31);

        Optional<LocalDate> roundTripped = Dates.parse(Dates.format(original));

        assertEquals(original, roundTripped.orElseThrow());
    }
}