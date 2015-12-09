
# rapanui – Relational Algebra Proof Assistant

rapanui dient zur Unterstützung beim Führen von relationenalgebraischen Beweisen. Dir Grundidde besteht darin, dass der Nutzer nach der Eingabevon festen Voraussetzungen interaktiv Beweise führt. Dazu wird dem Nutzer jeweils eine Liste mit mögliche nächsten Schritten angeboten, aus der er einen Vorschlag auswählt und anwendet. Dies wird wiederholt, bis die zu zeigende Aussage bewiesen wurde.

## Features

rapanui bietet eine übersichtliche graphische Benutzeroberfläche. Die Grundlagen für die Beweisführung sind nicht statisch verankert, sondern werden dynamisch geladen und sin leicht anpassbar. Dazu kann der Nutzer eine Menge von als wahr angenommenen Sätzen und Axiomen durch eine intuitive mathematische Syntax angeben. Während der Beweisführung sollen dem Nutzer die Grundlagen der gemachten Vorschläge ersichtlich und nachvollziehbar sein und so für die Hauptzielgruppe – auf dem Gebiet der relationalen Algebra eher unerfahrene Nutzer – auch als Übung dienen.

## Installation

Für Entwicklungszwecke muss das git-Repository geklont werden. Danach ist noch die Installation der Abhängigkeiten via [gradle](http://gradle.org) notwendig. Mit

    gradle build

werden die nötigen Abhängigkeiten geladen und der Code kompiliert.

Mit

    gradle shadowJar

kann die Anwendung kompiliert und in eine einzelen `jar`-Datei gepackt werden. Diese enthält alle Abhängigkeiten und kann auf jedem Desktop-System mit einer aktuellen Java-Umgebung gestartet werden.

## Entwicklung

Der Code ist größtenteils in Java geschrieben, an einzelnen Stellen kommen domänen-spezifische Sprachen wie Xtext, Xcore und Xtend zum Einsatz.

Die Anwendung besteht aus 4 Hauptkomponenten:

* Das Projekt `rapanui.dsl` enthält die Spezifikation der Syntax für Terme, Formeln und das dynamische Regelwerk, sowie Komponenten zur Unterstützung im Umgang damit. Für die Generierung des Parsers wird das [Xtext Framework](http://eclipse.org/Xtext/) genutzt.
* In `rapanui.core` werden die zentralen Komponenten der Logikschicht verwaltet. Hier werden auch die Vorschläge generiert.
* `rapanui.library` enthält das Standard-Regelwerk für die Anwendung.
* `rapanui.ui` hingegen enthält die graphische Benutzeroberfläche und die ausführbare Anwendung.