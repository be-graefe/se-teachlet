package de.nordakademie.backend;

import java.time.LocalDate;
import java.util.List;

/**
 * Component des Kompositum Designpatterns - gemeinsame Schnittstelle von Task (Blatt)
 * und Projekt (Kompositum). Die Oberfläche arbeitet ausschließlich mit diesem Typ.
 * 
 * Transparente Variante gewählt -> alle Methoden im gemeinsamen Interface
 * 
 * @author Flavio Prösch
 */
public interface ITaskComponent {

    /** Gibt den Namen des Objektes zurück */
    String getName();
    
    /** Gibt die Kinder des Objektes zurück */
    List<ITaskComponent> getChildren();

    /** Rückgabe, ob es ein Projekt (true) ist */
    boolean isProject();

    /** Gibt das Fälligkeitsdatum zurück */
    LocalDate getDueDate();

    /** Gibt an, ob das Objekt erledigt ist */
    boolean isChecked();

    /** Setzt das Objekt auf erledigt / nicht erledigt */
    void setChecked(boolean checked);

    /** Fügt dem Objekt ein Kind hinzu */
    void addChild(ITaskComponent child);

    /** Entfernt ein Kind vom Objekt */
    void removeChild(ITaskComponent child);
}