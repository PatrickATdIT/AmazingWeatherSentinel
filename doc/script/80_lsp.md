Der Sentinel läuft, aber aktuell verstoßen wir gegen zwei Regeln des Liskov’schen Substitutionsprinzips:
1.	Wir haben zwei Methoden, die eine Exception werfen, die nicht im Vertrag steht
Wenn eine neue Prüfung auf den Niederschlag nötig werden würde, würde die ganze Applikation abbrechen. Dazu unter 5. ISP mehr.
2.	Der PalantAir liefert doublewerte und gewollt sind ints. Das ist eine invarianz bzw. ein verstoß gegen die kovarianzregel von rückgabewerten.
Beispiel: der service liefert als temperatur 30,9 C. Das ist mehr als 30 grad, es wird aber abgeschnitten, weil wir einen int zurückliefern.
Lösungswege: Serviceinterface umdefinieren auf double oder Number, das würde aber erfordern, dass der WeatherOracle einen Adapter benötigt. Wäre der saubere Weg. Der pragmatische wäre Runden.

