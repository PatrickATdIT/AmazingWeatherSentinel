Wichtig! Es geht nicht darum, jede Abhängigkeit auszutauschen. Feste Abhängigkeiten zu stabilen Modulen dürfen
bestehen (z.B. Java-Core-Klassen, Unit Test-Frameworks, Logging-Frameworks), aber trotzdem ist vorsicht geboten, denn
auch eine als Stabil erachteete Abhängigkeit könnte irgendwann zwangsweise zu ersetzen sein. Im Dezember 2021 wurde eine
Sicherheitslücke beim weitverbreiteten Protokollierungsframework Log4J aufgedeckt, unter derer Ausnutzung es möglich war
eigenen (schädlichen) Code auf einem nichteigenen Server auszuführen.
Wurde Log4J ohne Abstrahierung eingebunden, war das System potenziell gefährdet. Eine Nutzung des DIP hilft natürlich
nicht gegen die Seicherheitslücke, jedoch wäre die Protokollierungslösung relativ einfach austauschbar gewesen.
Typischerweise kann die Protokollierung über SLF4J abstrahiert werden. Diese Abstrahierung kommt aber mit dem
zwangsweisen Verzicht auf Frameworkspezifische Features einher.


Der AWSentinel ist komplett abhängig vom Service. Wenn der Service nicht verfügbar ist, funktioniert die App nicht mehr.
Ein Austausch kann nur in der App erfolgen (wäre Verstoß gegen OCP und SRP).

Lösung: Orchestrierung in der Main-Klasse.

1. Interface für den Service ableiten
2. Interface in Main instanziieren und an den Sentinel geben
3. Sentinel arbeitet nur mit dem Interface

Jetzt ist der Sentinel bereit, mit dem neuen Service zu arbeiten.

Wir fügen den neuen Service PalantAir ein und stellen fest, dass er der Schnittstelle nicht genügt. Da kann man nichts
machen, außer einen Adapter zu bauen. Der Adapter hat aber ein Problem: Er muss 4 Methoden mappen, der Service hat aber
nur 2 (Temperatur und Wind). Die anderen Methoden könnten NotImplemented implementieren. Das wäre ein Verstoß gegen das
Liskov’sche Substitutionsprinzip, ist hier aber erst mal egal. Kommt später.
Wir bauen also die Klasse die dann als Service agiert:

1. PalantAirAdapter implements ServiceInterface
2. Konstrukter erzeugt sich eine Instanz von PalantAir (Achtung: DIP!!!)
3. Temperatur und Windmethoden delegieren mit cast
4. Feuchtigkeit und Niederschlag: NotImplementedException
