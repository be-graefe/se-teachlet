# Aufgabe 

Fachliche Erweiterung: 
- Abhaken von ganzen Projekten 
- Fristen rekursiv bestimmen

## Umsetzung Kompositum-Pattern

### Backend

- **Component eingeführt**: neue Schnittstelle `TodoListComponent` als gemeinsamer Typ von Aufgabe und Projekt
  - `getName()`, `getFaelligkeitsdatum()`, `isErledigt()`, `setErledigt(boolean)` - die Werte, die jeder Eintrag hat
  - `getChildren()`, `addChild(...)`, `removeChild(...)` - die Kindverwaltung
  - `isProjekt()` - damit die Oberfläche Projekte als Überschrift darstellen kann
- **`Task` ist das Blatt**: implementiert `TodoListComponent`
  - `getChildren()` liefert eine leere Liste, dadurch braucht die Rekursion keine Fallunterscheidung
  - `addChild` / `removeChild` werfen `UnsupportedOperationException`
- **`TodoList` ist das Kompositum**: implementiert `TodoListComponent`
  - hält `List<TodoListComponent>` statt `List<Task>` - dadurch sind beliebig tiefe Verschachtelungen möglich
  - `addTask`/`removeTask`/`getTasks` zu `addChild`/`removeChild`/`getChildren` umbenannt
- **Abhaken von ganzen Projekten**
  - `setErledigt(...)` reicht den Status an alle Einträge weiter - rekursiv bis in die Unterprojekte
  - `isErledigt()` ist abgeleitet: `true`, wenn das Projekt Einträge enthält und alle erledigt sind
  - ein leeres Projekt gilt bewusst als *nicht* erledigt
- **Fristen rekursiv bestimmen**
  - `getFaelligkeitsdatum()` eines Projekts = früheste Frist seiner Einträge
  - erledigte Einträge werden ausgefiltert, zählen also nicht mehr mit
  - `null`, wenn kein offener Eintrag mit Frist übrig ist

### Oberfläche

- **Nur eine Klasse kennt das Backend**: `RowBuilder` steigt rekursiv durch das Kompositum und erzeugt daraus eine flache Liste von Anzeigezeilen
  - arbeitet ausschließlich gegen `TodoListComponent`, kein `instanceof` auf konkrete Klassen
  - Tabelle, Spalten, Renderer und Dialoge kennen das Backend überhaupt nicht
- **`Row` als Anzeigezeile**: trägt die anzuzeigenden Werte (Tiefe, Name, Frist, Status) und die erlaubten Aktionen
  - `onDone` zum Abhaken, `onDelete` zum Löschen, `target` als Ziel für neue Einträge
  - ob eine Zeile abhakbar oder löschbar ist, entscheidet sich daran, *ob* eine Aktion vorhanden ist - nicht am Typ
  - dadurch sind Projekte automatisch genauso abhak- und löschbar wie Aufgaben
- **Darstellung der Struktur**: Einrückung nach Verschachtelungstiefe, Projekte fett und grau hinterlegt
- **Farben nach Zustand**: erledigt (grün), heute fällig oder überfällig (rot), morgen fällig (gelb) - greift durch die rekursive Frist auch bei Projekten
- **Neue Einträge landen am ausgewählten Element**
  - Projekt ausgewählt → der neue Eintrag kommt in dieses Projekt
  - Aufgabe ausgewählt → der neue Eintrag kommt in deren Liste
  - nichts ausgewählt → oberste Liste
  - die Auswahl bleibt nach dem Anlegen erhalten, damit man mehrere Einträge nacheinander einfügen kann
- **Sofortige Aktualisierung**: nach jeder Änderung werden die Zeilen neu aus dem Backend gelesen, damit sich ein abgehaktes Projekt samt seiner Aufgaben unmittelbar aktualisiert

### Entwurfsentscheidungen

- **Transparente Variante nach GoF**: `addChild` / `removeChild` stehen in der Component, nicht nur im Kompositum
  - Vorteil: die Oberfläche kommt ohne Typprüfungen aus und kennt keine konkrete Backend-Klasse
  - Nachteil: `Task` muss beide Methoden implementieren und wirft dort eine Ausnahme
  - Alternative wäre die Sicherheitsvariante (Kindverwaltung nur in `TodoList`) mit einem `instanceof` im `RowBuilder`
- **`isProjekt()` statt `instanceof`**: die Oberfläche fragt nach der Rolle, nicht nach der Klasse - ein zweites Kompositum würde ohne Änderung an der Oberfläche funktionieren
- **Trennung der Zuständigkeiten**: „welche Frist ist fachlich relevant" steht im Backend, „was wird angezeigt" in der Spaltendefinition

### Offen

- Für das rekursive Abhaken und die rekursiven Fristen gibt es noch keine Unit-Tests
- Der Dialog „Projekt-Liste hinzufügen" fragt nach einem Fälligkeitsdatum, das verworfen wird - ein Projekt bestimmt seine Frist selbst
- Eine erledigte Aufgabe zeigt ihre eigene Frist weiterhin an, ein erledigtes Projekt dagegen nicht mehr