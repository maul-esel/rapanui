# Entwurf einer Anwendung zur Unterstützung beim Führen von relationenalgebraischen Beweisen

## Zielsetzung
Aufgabe ist die Programmierung eines Tools zur Hilfestellung beim Führen von
relationenalgebraischen Beweisen. Das intuitiv zu bedienende Tool erlaubt dem
Nutzer die Eingabe von Voraussetzungen und schlägt dann – basierend auf einem
dynamisch geladenen Regelwerk und ausgehend von einem vom Nutzer eingegebenen
Startterm – mögliche Umformungen des aktuellen Terms vor. Wählt der Nutzer einen
solchen Vorschlag aus, so wird diese Umformung angewendet und es werden für den
neuen Term wiederum Vorschläge gemacht, solange bis der Nutzer seinen Zielterm
erreicht.

## Begriffe
### Unterstützte Terme und Formeln
Um sowohl die Nutzung als auch die Erstellung des Tools einfach zu halten, wird
nur eine eingeschränkte Menge von Formeln und Termen unterstützt; und zwar nur
Gleichheit oder Inklusion zwischen relationenalgebraischen Ausdrücken, hingegen
keine logischen Operatoren ($¬$, $\wedge$, $\vee$) und auch keine Punkt- oder
Tupelnotation. Vieles davon lässt sich auch durch Nutzung von einer oder
mehreren anderen, unterstützten Formeln ausdrücken: z.B. $R \cup S \subseteq T$
oder $R \subseteq T, S \subseteq T$ statt $R \subseteq T \wedge S \subseteq T$.
Zusätzlich werden nur homogene Relationen über einer nicht genauer
spezifizierten (d.h. insbesondere nicht konkret angegebenen) Menge betrachtet.

#### Terme
* Eine Relationskonstante ($I$, $Π$, $∅$) oder -variable ($A – H, J – Z$) ist
ein Term.
* Sind $t$, $t_1$, $t_2$ Terme, so auch $(t_1 \cap t_2)$, $(t_1 \cup t_2)$,
$(t_1 \setminus t_2)$, $(t_1;t_2)$, $\overline{t}$, $t^*$, $t^+$ und $t˘$.

#### Formeln
* Sind $t_1$, $t_2$ Terme, so sind $t_1 = t_2$ und $t_1 \subseteq t_2$ Formeln.

### Aussagen
Eine Aussage besteht aus einer endlichen Menge von Voraussetzungen (Formeln im
obigen Sinne) und einer endlichen, nichtleeren Menge von Konklusionen (ebenfalls
Formeln im obigen Sinne). Dabei wird eine Menge von Aussagen jeweils als
Konjunktion der Elemente verstanden.

Der Geltungsbereich einer Variable in einer Formel ist immer die die Formel
beinhaltende Aussage, d.h. falls mehrere Voraussetzungen oder Konklusionen eine
Variable mit dem selben Namen enthalten, so bezeichnen alle Vorkommen dieser
Variable dieselbe Relation.

### Definitionen
Eigenschaften einer Relation können definiert werden. Eine Definition besteht
aus einem Namen (z.B. $\text{reflexiv}$), einer Zielvariable (z.B. $R$) und
einer Menge von Voraussetzungen (z.B. $I \subseteq R$). Existiert eine solche
Definition, so kann ihr Name in einer anderen Aussage oder Definition als
Platzhalter für ihre Voraussetzungen benutzt werden (z.B.
$R;S \text{ ist reflexiv}$ für $I ⊆ R;S$).

## Komponenten der Anwendung
### Parser
Es wird eine Komponente zum Parsen von der unterstützten Formeln und Terme
benötigt, zum Einen für die Eingaben des Benutzers und zum anderen für die
Spezifizierung des Regelwerks. Für Letzteres werden neben Formeln auch noch
gewisse Strukturierungsschlüsselwörter benötigt, so dass das Regelwerk in einer
DSL verfasst werden kann.

#### Beispiel für die DSL
```
theorem "Antisymmetrie der Inklusion"
  let R ⊆ S
  and S ⊆ R
  then R = S

axiom "Neutralität von I"
  always R;I = R
  and I;R = R

define R as "reflexiv"
  iff I ⊆ R
```

### Vorschläge
Die zentrale Komponente macht die Umformungsvorschläge. Dazu erhält sie den
aktuellen Term und die eingegebenen Voraussetzungen sowie ein geparstes
Regelwerk. Der Rückgabewert soll eine Liste von Vorschlägen sein, wobei ein
Vorschlag besteht aus dem genutzten Satz, einer passenden Zuordnung seiner
Variablen auf Teilterme des Eingabeterms sowie dem Ergebnisterm.

### Regelwerk
Unter Nutzung des obigen Systems muss eine Grundbibliothek von Sätzen angelegt
werden. Dazu gehören Grundlagen der Mengenlehre sowie der relationalen Algebra.
Als Basis hierfür wird das Skript der Vorlesung Diskrete Strukturen verwendet.

### Benutzeroberfläche
Die Benutzeroberfläche soll erlauben, eine Menge von Voraussetzungen einzugeben
sowie einen Startterm. Dann sollen in jedem Schritt unter Nutzung der
Voraussetzungen mögliche Regelanwendungen auf den aktuellen Term vorgeschlagen
werden. Bei Auswahl wird diese auf den aktuellen Term angewendet.

Basierend auf den eingegebenen Voraussetzungen können mehrere Umformungsprozesse
gestartet werden. Dabei können bewiesene Formeln als Hilfsaussagen / Lemmata
für andere genutzt werden, d.h. sie werden der Vorschlagskomponente als gültige
Voraussetzungen mitgegeben.

Die Eingabe von Formeln und Termen wird durch die Anzeige einer
Bildschirmtastatur mit ausgewählten Sonderzeichen wie z.B. ∩, ∪, ⊆, Π, ˘ und ⁺
erleichtert. Eine Auswahl der verfügbaren Definitionen wird ebenfalls angezeigt.

## Technologie
Die Implementierung erfolgt in Java 8. Dabei wird für die Benutzeroberfläche
eine der eingebauten UI-Bibliotheken (AWT / Swing) genutzt. Für das Parsen der
Formeln, Terme und DSL wird das Framework *Eclipse Xtext* genutzt, welches das
Generieren von Lexer, Parser und AST aus einer Grammatik erlaubt. Zusätzlich
wird dadurch auch das IDE-unterstützte Verfassen des Regelwerks in Eclipse
möglich.

## Vergleich mit anderen Tools zur unterstützten Beweisführung

Ein kurzer Vergleich mit anderen Projekten zur interaktiven Beweisunterstützung,
insbesondere mit den Projekten KIV, Isabelle und Coq ergibt Folgendes:

Bei allen drei Projekten handelt es sich natürlich um wesentlich komplexere
Plattformen. Sie haben keinen spezifischen Fokus auf relationale Algebra,
sondern unterstützen umfangreichere Formeln aus verschiedenen Teilgebieten der
Mathematik, die Arbeit mit verschiedenen Datentypen sowie die Betrachtung
konkreter Mengen, Funktionen etc. Dieses Projekt hingegen beschränkt sich auf
die relationale Algebra. Darüber hinaus soll die einfache Bedienbarkeit auch
für Anfänger (wie z.B. Erstsemester) im Vordergrund stehen.

Alle 3 Systeme nutzen ebenfalls eine DSL zur Spezifikation. Allerdings sind
diese entsprechend umfangreicher. Insbesondere bei Isabelle scheint es sich um
eine sehr anpassbare Sprache zu handeln, die dem Nutzer neben Definitionen wie
oben beschrieben auch die Definition neuer Notation innerhalb der DSL erlaubt.
Auch ansonsten scheint Isabelle sehr modular ausgelegt zu sein, was die
Organisation der Funktionalität in vielen verschiedenen Pakten inklusive
zugehöriger Datentypen, Axiome und teilweise auch Beweisstrategien erlaubt.

Wie auch dieses Projekt bringen die betrachteten Programme umfangreiche
Bibliotheken gültiger Aussagen mit sich, die zur Grundlage der Beweisführung
dienen. Zumindest im Fallevon KIV handelt es sich dabei aber um Sätze inklusive Beweisführung (in der DSL), während hier auf die Beweise verzichtet wird und die
definierten Aussagen eher als "Axiome" zu verstehen sind.

Neben der interaktiven Beweisführung zu mathematischen Aussagen sind jeweils
auch die Verifizierung von in der jeweiligen DSL notierten Beweisen und
Algorithmen unter den Features zu finden.

### Quellen:
  * "KIV: overview and VerifyThis competition" (2012), Gidon Ernst u.A.
  * Isabelle Website (isabelle.in.tum.de)
  * Coq Webiste (coq.inria.fr)

## Projektplanung
Grober Entwurf:

* Phase 1: Entwicklung und Testen der Parser-Komponente
* Phase 2: Prototyp der Benutzeroberfläche, erste minimale Version des
Regelwerks
* Phase 3: Entwicklung und Testen der Vorschlagskomponente, Ausbau des
Regelwerks
* Phase 4: Ausgestaltung der Benutzeroberfläche, Ausbau des Regelwerks
