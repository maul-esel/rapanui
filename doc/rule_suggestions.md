# Algorithmus

Gegeben: ein `FormulaTemplate t` (mindestens ein Term spezifiziert), eine `Rule r`.

Iteriere durch die aufgelösten Folgerungen `Formula c` von `r`.
Falls der Typ von `c` kompatibel ist mit dem Typ von `t`, d.h.
* entweder ist der Typ von `t` unspezifiziert,
* oder gleich dem Typ von `c`
* oder der Typ von `c` ist `EQUATION` und der von `t` ist `INCLUSION`

dann berechne den erkennbaren Teil der Übersetzung von `c` in `t`, d.h. versuche jeweils den linken Term von `c` auf den linken Term von `t`, und den rechten Term von `c` auf den rechten Term von `t` zu mappen (jeweils nur, falls der entsprechende Term von `t` spezifiziert ist).

Dies liefert entweder eine (potentiell unvollständige) Übersetzung, oder einen Fehler (strukturelle Inkompatibilität). Im letzteren Falle breche die Behandlung von `c` ab.

Andernfalls untersuche die aufgelösten Voraussetzungen `Formula p` von `r` und erstelle ein `FormulaTemplate s` für jede. Versuche nun, die Terme von `p` mithilfe der unvollständigen Übersetzung zu übersetzen. Falls das gelingt, trage sie in `s` ein.

**Alternative 1:** Andernfalls, lasse den entsprechenden Term in `s` unspezifiziert.

**Alternative 2:** Füge eine Klasse `TermTemplate` hinzu und ändere den Typ der Felder von `FormulaTemplate` dazu. Erstelle nun für die Terme von `p` solche Templates und fülle alles bekannte (d.h. übersetzbare) aus, und setze sie als linken und rechten Term von `s`. (Dies führt zu Änderungen in den anderen Algorithmen)

Versuche nun, `s` zu begründen. Falls das gelingt, überprüfe ob das entsprechende Ergebnis mit der bisherigen Übersetzung vereinbar ist. Falls ja, kopiere die unvollständige Übersetzung und trage die neuen Erkenntnisse ein. Dann fahre fort mit der nächsten Voraussetzung. Falls nein, breche ab.

Nachdem alle Voraussetzungen so behandelt worden sind, sollte die Übersetzung vollständig sein. Erstelle eine `RuleApplication a` und gebe sie aus.

Falls `c` eine Gleichung (`EQUATION`) ist, führe den Algorithmus für die umgekehrte Gleichung ebenfalls durch.

## Anmerkungen
* Bevorzugung der Voraussetzungen, bei denen ein größerer Teil der Übersetzung bekannt ist, sollte die Effizienz verbessern.