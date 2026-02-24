Wir stellen fest, dass der Wind nicht ausreicht. Wir brauchen auch die Temperatur. Bei über 30 Grad besteht die Gefahr, dass die Rakete sich selbst entzündet und zum falschen Zeitpunkt startet. Eine neue Prüfung im WeatherCheck wäre ein Verstoß gegen das SRP.
Deshalb: 
1.	Neue Klasse: TemperatureCheck
Muss temperatur entgegen nehmen, prüft > 30 Grad => exception
2.	ServiceCall muss auch die Temperatur abfragen. Das heißt, wir brauchen einen anderen returntyp
Noch besser: Checks in eine neue Klasse auslagern und die per Dependency Inversion füllen.
