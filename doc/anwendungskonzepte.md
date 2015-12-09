# Anwendungskonzepte

Neben den mathematischen Begriffen `Term` und Aussage (`Formula`) gibt es 4 grundlegende Konzepte in rapanui, die sich in den mathemtischen Grundlagen, den Datenklassen und der Benutzeroberfläche wiederfinden. Das sind:

* Beweisumgebungen (`ProofEnvironment`)
* Folgerungen (`ConclusionProcess`)
* Umformungen (`Transformation`)
* Begründungen (`Justification`)

Sie werden im Folgenden erläutert.

## Beweisumgebung (`ProofEnvironment`)

Hierbei handelt es sich um das grundlegende Modell für die Beweisführung: eine Beweisumgebung besteht aus einer Menge an Voraussetzungen (`premises`) und einer Menge von daraus gezogenen Folgerungen (`conclusions`). Bei den Voraussetzungen handelt es sich einfach um Aussagen (`Formula`), die Folgerungen werden im nächsten Abschnitt genauer erläutert.

Auch in der Benutzeroberfläche finden sich die Beweisumgebungen wieder, und zwar auf der linken Seite des Fensters. Hier werden Voraussetzungen und Folgerungen angezeigt und es können neue erstellt werden. Über eine Auswahl im oberen Teil kann zwischen verschiedenen Umgebungen umgeschaltet werden, mit den Steuerelementen rechts davon können neue angelegt oder die aktuelle gelöscht werden.

## Folgerung (`ConclusionProcess`)
Anders als die Voraussetzungen sind die Folgerungen nicht nur einfache Aussagen, denn sie sollen ja Schritt für Schritt erzeugt werden und manipulierbar sein. Eine Folgerung besteht dabei aus einem festen Startterm und einer Kette von auf ihn angewendeten Umformungen (siehe unten). Dabei wird in jedem Schritt durch die Auswahl eines Vorschlags eine neue Umformung angehängt.

Eine Folgerung wird in der Benutzeroberfläche als Block unter der entsprechenden Überschrift angezeigt. Dabei steht in der ersten Zeile eine Art Zusammenfassung, die den Startterm und den aktuell letzten Term enthält. Ist eine Folgerung ausgewählt, so werden in der rechten Fensterhälfte die anwendbaren Vorschläge angezeigt.

## Umformung (`Transformation`)
Eine Umformung formt einen Eingabeterm in einen gleichwertigen (`=`) oder den Eingabeterm umfassenden (`⊆`) Ausgabeterm um und gibt dabei eine Begründung an, weshalb diese Umformung gültig ist. Die Umformungen innerhalb einer Folgerung finden sich in der Benutzeroberfläche als zeilenweise Gleichheits-/Inklusionskette wieder. Gleichzeitig ist auch ein dem Nutzer gemachter *Vorschlag* nichts anderes als eine `Transformation`, die eben noch nicht angewendet wurde.

## Begründungen
Am untersten Ende der Konzepthierarchie stehen die Begründungen (`Justification`). Sie dienen dazu, dem Nutzer zu erklären, warum eine Umformung erlaubt ist.

Entsprechend den üblichen Konventionen in der mathematischen Beweisführung teilen sie sich in 4 Arten auf:

* *"nach Voraussetzung"* (`EnvironmentPremiseJustification`) – eine Aussage kann gültig sein, weil sie direkt in den Voraussetzungen enthalten ist
* *Lemmata* (`ProofJustification`) – eine Aussage ist gültig, weil sie zuvor bewiesen wurde, in einer anderen oder derselben Folgerung.
* *Regelanwendung* – eine Aussage ergibt sich aus der Anwendung einer der im dynamischen Regelwerk spezifizierten Regeln. Dazu wird eine Variablenbelegung der Variablen der Regel mit Termen aus der Beweisumgebung angegeben. Hat die Regel Voraussetzungen, so müssen diese wiederum begründet werden.
* *Ersetzung eines Teilterms* – Gleichheit zwischen zwei Termen gilt, wenn der Eine aus dem Anderen durch die Ersetzung eines Teilterms mit einem gleichwertigen Term hervorgeht. Wiederum muss begründet werden, warum die Teilterme gleich sind.