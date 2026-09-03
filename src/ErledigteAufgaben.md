# Aufgabe 

- Das Programm um Projekte / Unterlisten erweitern.  
- Abhaken von ganzen Projekten 
- Fristen rekursiv bestimmen

# Umsetzung Kompositum-Pattern

**BACKEND**

- neue Schnittstelle "ITodoList" als gemeinsamer Typ von Projekt und Task
- alle Methoden vom Task und Projekt ins Interface gepackt
- absichtlich auch addChild / removeChild im Interface, um die transparente Variante darzustellen
- Task und Projekt vom Interface implementieren lassen und Methoden angepasst

**FRONTEND**

- nur Klasse RowBuilder kennt das Backend -> hier Anpassungen vollzogen
- Methoden arbeiten ausschließlich mit `IToDoList` 
- Unterprojekte werden durch rekursive Einrückung dargestellt
- neue Elemente werden danach hinzugefügt, welches Objekt ausgewählt ist.
- Farben nach Zustand: erledigt (grün), heute fällig oder frist vorbei (rot), morgen fällig (gelb) -> greift durch die rekursive Frist auch bei Projekten


# Entwurfsentscheidungen

- **Transparente Variante nach GoF gewählt**: `addChild` / `removeChild` stehen in der Component, nicht nur im Kompositum
  - Vorteil: die Oberfläche kommt ohne Typprüfungen aus und kennt keine konkrete Backend-Klasse
  - Nachteil: `Task` muss beide Methoden implementieren und wirft dort eine Ausnahme
- Alternative wäre die Sicherheitsvariante 
- **`isProjekt()` statt `instanceof`**: die Oberfläche fragt nach der Rolle, nicht nach der Klasse, dadurch kann die Anwendung ohne Probleme mit weiteren Kompositums erweitert werden

# Offen / Designentscheidung

Der Dialog „Projekt-Liste hinzufügen" fragt nach einem Fälligkeitsdatum, was aber nie gespeichert wird -> Projekt bestimmt Frist rekursiv.
Hauptaugenmerk bei der Programmierung war die Implementierung des Kompositums Pattern, das und weitere Kleinigkeiten sind nicht per se Teil des Patterns, wurden deshalb nicht weiter verfolgt.


