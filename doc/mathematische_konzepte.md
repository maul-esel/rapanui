# Mathematische Konzepte

rapanui arbeitet mit einer eingeschränkten Menge von methematischen Termen und Formeln. Diese werden hier genauer spezifiziert.

## Terme

Der Wert jedes Terms ist eine binäre homogene Relation auf einer festen, für alle Terme gleichen, aber beliebigen Grundmenge. Dabei gibt es drei Konstanten:

* die leere Menge `∅`
* die Identität `I`
* die Allrelation `Π`

Die Großbuchstaben `A - H` und `J - Z` bezeichnen Variablen. Anwendbare Operatoren sind:

* die Konverse `R˘`
* das Komplement `Rᶜ`
* die transitive Hülle `R⁺`
* die reflexiv-transitive Hülle `R*`
* der Schnitt `R ∩ S`
* die Vereinigung `R ∪ S`
* die Mengendifferenz `R \ S`
* die Komposition `R ; S `

## Formeln

Die einzigen erlaubten Formeln sind Gleichheit von Termen (`t1 = t2`) und Inklusion von Termen (`t1 ⊆ t2`).

## Einschränkungen

Diese Einschränkungen der erlaubten Formeln limitieren die beweisbaren Aussagen. In vielen Fällen jedoch lässt sich eine Aussage durch Umformulierung dennoch zeigen. So kann man z.B. zeigen, dass die Grundmenge unter gewissen Voraussetzungen leer sein muss, indem man zeigt `I = ∅` bzw. `Π = ∅`. Konjunktionen als Voraussetzungen oder Folgerungen lassen sich einfach aufspalten in zwei separate Voraussetzungen bzw. Folgerungen.

## Definitionen

Das dynamische Regelwerk erlaubt neben der Formulierung von Regeln auch noch die Definition von Eigenschaften einer Relation. Damit kann im Grunde ein Platzhalter für die Voraussetzung der Definition geschaffen werden: statt den Voraussetzungen `(R;S)˘ ⊆ R;S`, `(R;S);(R;S) ⊆ R;S`, `I ⊆ R;S` reicht bei entsprechender Definition die Aussage *`R;S` ist eine Äquivalenzrelation*.