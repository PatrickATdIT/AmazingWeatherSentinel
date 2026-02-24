Der AWSentinel ist komplett abhängig vom Service. Wenn der Service nicht verfügbar ist, funktioniert die App nicht mehr. Ein Austausch kann nur in der App erfolgen (wäre Verstoß gegen OCP und SRP).

Lösung: Orchestrierung in der Main-Klasse.
1.	Interface für den Service ableiten
2.	Interface in Main instanziieren und an den Sentinel geben
3.	Sentinel arbeitet nur mit dem Interface

Jetzt ist der Sentinel bereit, mit dem neuen Service zu arbeiten.

Wir fügen den neuen Service PalantAir ein und stellen fest, dass er der Schnittstelle nicht genügt. Da kann man nichts machen, außer einen Adapter zu bauen. Der Adapter hat aber ein Problem: Er muss 4 Methoden mappen, der Service hat aber nur 2 (Temperatur und Wind). Die anderen Methoden könnten NotImplemented implementieren. Das wäre ein Verstoß gegen das Liskov’sche Substitutionsprinzip, ist hier aber erst mal egal. Kommt später.
Wir bauen also die Klasse die dann als Service agiert:
1.	PalantAirAdapter implements ServiceInterface
2.	Konstrukter erzeugt sich eine Instanz von PalantAir (Achtung: DIP!!!)
3.	Temperatur und Windmethoden delegieren mit cast
4.	Feuchtigkeit und Niederschlag: NotImplementedException
