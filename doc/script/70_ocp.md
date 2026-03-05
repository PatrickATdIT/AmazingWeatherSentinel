## Das Open Closed Principle

Nach der Dekomposition mittels Andwendung des SRP und der Dependency Inversion sind Sie nun bereit, den Wetterdienst
´WeatherOracle´, dessen ~~gratis Probeabo~~ Lizenz ausgelaufen ist, durch den neuen, durch günstige Akquise dem
Firmenbesitz von Elon Bezos hinzugefügten PalantAir-Wetterdienst zu ersetzen. Doch es kommt so oft anders als geplant:

Gerade wollten Sie mit der Implementierung beginnen, da kommt Elon Bezos höchstselbst an Ihrem Tisch in der
Abstellkammer vorbei und wütet, dass trotz des Amazing Weather Sentinels immer noch Flüge verschoben werden müssten.
Ursache seien Probleme mit der Außentemperatur. Sobald diese 30 Grad Celsius übersteige, begännen sich einige
Komponenten der Raumschiffhülle bei Belastung so stark zu verformen, dass das Schiff nicht stabil gehalten werden könne.
Daher müsse der AWS nun nunbedingt eine Wetterwarnung bei Temperaturen über 30 Grad Celsius ausgeben.   
Außerdem hätte Claus Bezos gewarnt, dass auch laue Lüftchen über 30 km/h gefährlich sein könnten, allerdings nur bei
Temperaturen unter dem Gefrierpunkt. Auch in dem Fall soll eine Warnung gemeldet werden.  
Sie sollen sich nicht nur mit höchster, sondern mit Allerhöchster Priorität darum kümmern, dass der Amazing Weather
Sentinel ertüchtigt wird, diese Warnungen zu liefern, andernfalls drohe eine Beschimpfung, da man Ihnen das Gehalt ja
nicht mehr kürzen können, da sie bereits unter Mindestlohn arbeiten.

Was ist hier also zu tun? Prinzipiell ist die bis dato einzige Wetterprüfung im WindCheck implementiert, dank SRP.
Vielleicht könnte man diese Prüfung einfach erweitern? Dazu müsste man natürlich die `check`-Methode aufbohren, da
allerwenigstens die Temperatur voranden sein müsste.  
Im Ergebnis sähe das so aus: Die Schnittstelle `WeatherCheck` muss angepasst werden, so dass die Methode Check sowohl
Wind
als auch Temperatur empfängt.  
Die Implementierung `WindCheck` müsste ebenfalls entsprechend angepasst werden: Schnittstellenmethode, interne
Delegatemethode, falls man die weiter nutzen wollte, und natürlich die Prüflogik. Diese könnte aus einer ElseIf-Kette
bestehen, die die einzelnen Prüfungen durchführt und entweder eine Warnung oder eben keine Warnung ausgibt.

```java
public interface WeatherCheck {
  String check( Number wind, Number temperature );
}

public class WindCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( Number wind, Number temperature ) {
    return checkWeather( wind.intValue( ), temperature.intValue( ) );
  }

  private String checkWeather( int wind, int temperature ) {
    String result;
    if( wind > 40 ) {
      result = "Wind warning: %d km/h".formatted( wind );
    } else if( temperature > 30 ) {
      result = "Temperature warning: %d °C".formatted( temperature );
    } else if( wind > 30 && temperature < 0 ) {
      result = "Wind/Temperature warning: %d km/h at %d °C".formatted( wind, temperature );
    } else {
      result = "All parameters within limits: Wind %d km/h, Temperature %d °C";
    }
    log.info( result );
    return result;
  }
}
```

Warum ist das kein guter Ansatz?

1. Trivialgrund, aber nicht unbedeutsam: Die Impelemtierung heißt WindCheck. Es ist unerwartet, dass diese dann mit der
   Temperatur arbeitet. Der Name ist nicht beschreibend.
2. Die Schnittstelle `WeatherCheck` musste aufgebohrt werden. Das ist immer eine schlechte Idee, da mit einer Änderung
   der Schnittstelle plötzlich alle Implementierungen fehlerhaft werden und einer Anpassung bedürfen. Bei öffentlichen
   API sind Schnittstellenänderungen gar nicht erlaubt. Das wäre eine sogenannte inkompatible Änderung, da sie
   inkompatibel zu ihren Abhängigkeiten geändert werden würde. Diese müssten nicht nur Ihre Realisierung der
   Schnittstelle und ggf. die Implementierung selbst, sondern auch die vorhandenen Unittests anpassen.
3. Die Logik der Klasse hat sich geändert. Das hat auswirkungen auf ihren Vertrag, also ihr Versprechen, was sie kann.
   Das hat zwei Effekte:
    1. Eventuelle Nutzer wären ggf. Überrascht von der Temperaturwarnung; natürlich wären die eh schon stutzig geworden,
       weil Punkt 2 viel stärker zuschlägt.
    2. Die Unittests der Klasse, sofern vorhanden, müssten angepasst werden, da sich die Klasse nun anders verhält und
       damit Assertions obsolet sein könnten. Im spezifischen Fall wären die Unittests sogar syntaktisch zerstört
       worden.
4. Zukunftssicherheit. Die ist überhaupt nicht gegeben. Wenn Elon Bezos als nächstes mit einem Wusch um die Ecke kommt,
   die Luftfeuchtigkeit zu berücksichtigen, beginnt dasselbe Spiel von vorn.

Insgesamt sind dies Auswirkungen, die durch den Verstoß gegen das Open Closed-Prinzip erzeugt worden sind. Und das
schauen wir uns jetzt an, bevor wir im Anschluss eine sichere Lösung für den Weltraummilliardär erstellen.

Die Kernaussage des Open Closed Principles ist:  
**Ein Modul (z.B. eine Klasse) sollte offen sein für Erweiterungen, aber verschlossen gegenüber Modifikationen.**  
Der Grundgedanke dahinter ist, dass ein Modul zwar um neue Funktionen und Features erweiterbar sein soll, aber eben
genau so, dass bei einer Erweiterung bereits vorhandene Funktionalität (und daraus folgend auch Abhängige Module wie
Unittests und Verwendermodule) nicht angepasst werden muss. Sobald ein Modul fertiggestellt und getestet ist, sollte es
nicht mehr verändert werden müssen, um ein neues Feature zu implementieren. Stattdessen sollten neue Anforderungen in
neuen Klassen implementiert werden, die dann an das vorhandene System angeschlossen werden, möglichst natürlich über
Abstraktionen. Das hilft beim Reduzieren der zyklomatische Komplexität (also der Pfade durch den Code eines Moduls, auch
Branches genannt), führt zu kleineren Codeartefakten und schützt damit vor Regressionsfehlern.

Um die neuen Anforderungen von Elon Bezos umzusetzen, wäre es nach Lehrbuch also grundsätzlich die beste Vorgehensweise,
wenn das Vorhandene Module `WindCheck` und die zugehörige Schnittstelle `WeatherCheck` unangetastet blieben. Das ist
hier allerdings nicht nötig, da es keine Verwender für die Schnittstelle und auch nicht für die Implementierung gibt
außer unseres eigenen Programms; das wissen wir mit Sicherheit. Deshalb ist es unnötig, eine neue Schnittstelle zu
definieren. Eine Anpassung des Sentinel-Unittests benötigen wir sowieso, ob mit neuer oder geänderter Schnittstelle.  
Das ist auch nicht unser Fehler, denn um den Code OCP-konform zu machen, müssen wir ihn anpassen. Die Anpassung an das
OCP ist mit Sicherheit kein Verstoß gegen das OCP. Es ist eine Investition in die Zukunftssicherheit der Applikation.

Wie gehen wir nun vor? Das dürfen Sie zunächst selbst überlegen.

### Aufgabe

Sie kennen die Anforderungen des Milliardärs bereits. Versuchen Sie zunächst, die vorhandene Codebase (Version 3) des
Amazing Weather Sentinels so anzupassen, dass die spätere Implementierung dieser Anforderung nur durch die
Erstellung weiterer Klassen erreicht werden kann. Nutzen Sie dabei das bereits erlangte Wissen über das Single
Responsibility- und das Dependency Inversion-Prinzip.  
Wenn die Codebase vorbereitet ist, implementieren Sie die beiden neuen Wetterprüfungen. Zur Erinnerung: Eine
Wetterwarnung soll zusätzlich zur bereits bestehenden Windwarnung auch dann erfolgen, wenn die Temperatur 30 Grad
Celsius übersteigt, oder wenn sie unter den Gefrierpunkt von 0 Grad liegt und eine Windgeschwindigkeit von 30 km/h
vorhergesagt wird.

### Lösungsvorschlag

Da wir wissen, dass es die Anforderung nach neuen Prüfungen gibt, ist der erste Schritt die Änderung des
WeatherCheck-Interfaces. Die Schnittstelle soll weiterhin eine Nachricht zurückgeben, aber die Parameter sollen noch
generischer übergeben werden. Der Wunsch ist, dass diese Schnittstelle nie wieder angepasst werden muss. Am besten geht
dies durch die Definition einen Wrappertypen um die eigentlich gewollten Parameter. Was heißt das?  
Gewollt ist aktuell noch der Parameter wind vom Typ Number, später soll auch Temperature vom Typ Number hinzukommen,
vielleicht viel später noch ein dritter. Würden die Parameter einfach in der Definition der Schnittstellenmethode
aufgelistet, würde mit jedem neuen Parameter die Schnittstelle aufgebohrt werden müssen. Das ist, wie oben beschrieben,
absolut nicht wünschenswert, da vorhandener Code kaputgehen würde.
Stattdessen definiert man in solchen Situationen eine Klasse, die die gewollten Parameter als Attribute hält, aktuell
also den Wind und die Temperatur. Die Klasse soll WeatherParameters heißen und hat getter für beide Attribute.

```java
public final class WeatherParameters {
  private final Number wind;
  private final Number temperature;

  public WeatherParameters( Number wind, Number temperature ) {
    this.wind = wind;
    this.temperature = temperature;
  }

  public Number wind( ) {
    return wind;
  }

  public Number temperature( ) {
    return temperature;
  }
}
```

Seit Java 16 gibt es records, die genau dasselbe machen, was diese Klasse macht. Attribute vorhalten, Getter liefern,
dazu bekommen wir noch toString(), hash() und equals() geschenkt, aber ohne diesen Boilerplate code. Und unveränderbar
(immutable) sind die Objekte auch.

```java
public record WeatherParameters( Number wind, Number temperature ) { }
```

Der Record soll dazu dienen, die Daten in die Schnittstellenmethode `Check` zu liefern. Der Vorteil ist, sollte es ein
neues Wetterdatum geben, das relevant ist, kann der Record einfach erweitert werden, ohne dass vorhandene Benutzer
darunter zubruch gehen.

In der Schnittstelle sieht das dann wie folgt aus. Den Transporttyp `WeatherParameters` kann man direkt in der
Schnittstelle
selbst definieren. Das halte ich für sehr guten Stil, denn es zeigt deutlich, dass dieser Transporttyp nur für diese
Schnittstellen gebaut worden ist.

```java
public interface WeatherCheck {
  record WeatherParameters( Number wind, Number temperature ) { }

  String check( WeatherParameters weatherParameters );
}
```

Entsprechend der Schnittstelle muss zunächst die Implementierung `WindCheck` angepasst werden. Das ist trivial und
erfolgt daher kommentarlos:

```java
public class WindCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Wind check" );
    return checkWind( weatherParameters.wind( ).intValue( ) );
  }

  private String checkWind( int wind ) {
    String result;
    if( wind > 40 ) {
      result = "Wind warning: %d km/h".formatted( wind );
    } else {
      result = "Wind within limits: %d km/h".formatted( wind );
    }
    log.info( result );
    return result;
  }
}
```

Der Sentinel, Main und der Unittest haben noch Fehler, aber implementieren wir zunächst trotzdem schon einmal die neuen
Anforderungen, also die zusätzlichen Prüfungen, nach denen Elon Bezos verlangt.
Zuerst der Temperaturcheck:

```java
public class TemperatureCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Temperature check" );

    String result;
    final var temp = weatherParameters.temperature( ).intValue( );

    if( temp > 30 )
      result = "Temperature warning: %d °C".formatted( temp );
    else
      return "Temperature within limits: %d °C".formatted( temp );

    log.info( result );
    return result;
  }
}
```

Und dann die Prüfung auf frostigen Wind:

```java
public class WindAndFrostCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Wind/Frost check" );

    final var wind = weatherParameters.wind( ).intValue( );
    final var temp = weatherParameters.temperature( ).intValue( );
    String result;

    if( wind > 30 && temp < 0 ) {
      result = "Wind/Frost warning: %d km/h and %d °C ".formatted( wind, temp );
    } else {
      result = "Wind/Frost indicators within limits: %d km/h and %d °C ".formatted( wind, temp );
    }
    log.info( result );

    return result;
  }
}
```

Nun muss der Sentinel angepasst werden. Das sind insgesamt drei Schritte.

1. Der Sentinel muss mehrere Prüfungen ausführen. Dazu muss er zunächst in der Lage sein, mehrere Implementierungen der
   `WeatherChecks` zu empfangen. Dazu wird dem Constructor nun eine Liste mit `WeatherChecks` anstelle eines einzelnen
   mitgegeben.
2. Die einzelnen Wetterprüfungen empfangen jetzt nicht mehr nur einen Windwert, sondern ein komplexes Objekt vom Typ
   WeatherParameters. das aktuell Wind und Temperatur enthält. Das muss natürlich entsprechend aufgebaut werden.
3. Statt des Aufrufs des einen einzelnen `WeatherChecks` muss die Liste aller `WeatherChecks` iteriert werden und jede
   Wetterprüfung einzeln aufgerufen werden. Die Ergebnisse jeder Prüfung, also die Nachrichten, soll zeilenweise
   untereinander ausgegeben werden.

Die Klasse sieht nach den Codeänderungen wie folgt aus:

```java
public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherService;
  private final WeatherReport weatherReport;
  private final List<WeatherCheck> weatherChecks;

  public Sentinel( WeatherOracle weatherService, WeatherReport weatherReport, List<WeatherCheck> weatherChecks ) {
    this.weatherService = weatherService;
    this.weatherReport = weatherReport;
    this.weatherChecks = weatherChecks;
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var weather = new WeatherCheck.WeatherParameters(
      weatherService.getWind( ),
      weatherService.getTemperature( ) );
    var message = executeWeatherChecks( weather );
    weatherReport.report( message );
  }

  private String executeWeatherChecks( WeatherCheck.WeatherParameters weather ) {
    List<String> results = new LinkedList<>( );
    for( var wc : weatherChecks ) {
      var checkResult = wc.check( weather );
      results.add( checkResult );
    }
    return String.join( System.lineSeparator( ), results );
  }
}
```

Bleibt noch Composition Root, bzw. die Main-Klasse. Diese muss natürlich eine Liste von `WeatherCheck`s definieren, die
der Sentinel dann einzeln ausführen soll, anstelle des einzelnen `WindCheck`s zuvor.

```java
public class Main {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  static void main( ) {
    log.info( "Starting Amazing Weather Sentinel" );
    var sentinel = new Sentinel(
      new WeatherOracleFactoryProduction( ).get( ),
      new TrayReport( ),
      List.of( new WindCheck( ), new TemperatureCheck( ), new WindAndFrostCheck( ) ) );
    sentinel.run( );
  }
}
```

Damit ist der Sentinel wieder lauf- und funktionsfähig. Das Ergebnis ist wie gewünscht, über die Nachrichtendetails, die
das PRogramm in den Systembenachrichtigungen ausgibt, kann man natürlich streiten, aber diese Details standen nicht zur
Debatte, weswegen diese einfache Lösung hier genügt.

Lediglich der Sentinel-Unittest muss noch korrigiert werden. Auch hier stehen mehrere schnelle Änderungen an:

1. Der WeatherOracleStub muss eine Temperatur zurückliefern.
2. Der WeatherCheckSpy muss neben Wind auch die Temperatur tracken. Besser wäre es aber, direkt die kompletten
   WeatherPArameters zu tracken; die kann man später gemeinsam prüfen, da eine equalsmethode geschenkt mitgeliefert
   wird.
3. Es sollten mindestens zwei WeatherCheckSpys teilnehmen, um zu beweisen, dass beide durchlaufen werden.
4. Die Code Under Test-Instanziierung muss beide Checks an den zu prüfenden Sentinel übergeben.
5. Die Assertions müssen prüfen, dass beide empfangenen WetterParameter der vorherigen Definition entsprechend.
   Außerderm muss die finale Nachricht mindestes zwei Zeilen der vordefinierten Nachricht enthalten.

Das sieht nach der Anpassung so aus.

```java
public class SentinelTest {
  @Test
  void verifySentinelOrchestration( ) {
    //assemble
    //festwerte und doubles definieren
    final var weatherParameters = new WeatherCheck.WeatherParameters( 60, 17 );
    final String message = "XXX";
    final String expectedMessage = message + System.lineSeparator( ) + message;

    //erstellen eines stubs für das WeatherOracle, der bei getWind immer den oben definierten wert *wind* liefert.
    var weatherOracleStub = new WeatherOracle( ) {
      @Override
      public int getTemperature( ) {
        return weatherParameters.temperature( ).intValue( );
      }

      @Override
      public int getWind( ) {
        return weatherParameters.wind( ).intValue( );
      }

      @Override
      public int getHumidity( ) {
        throw new UnsupportedOperationException( "Method not implemented" );
      }

      @Override
      public int getPrecipitation( ) {
        throw new UnsupportedOperationException( "Method not implemented" );
      }
    };

    //erstellen eines spys für WeatherCheck, der den empfangenen windwert protokolliert
    //und die oben definierte *message* zurückliefert
    class WeatherCheckSpy implements WeatherCheck {
      private WeatherParameters receivedWeatherParameters;

      @Override
      public String check( WeatherParameters weatherParameters ) {
        receivedWeatherParameters = weatherParameters;
        return message;
      }
    }
    var weatherCheckSpy1 = new WeatherCheckSpy( );
    var weatherCheckSpy2 = new WeatherCheckSpy( );

    //erstellen eines spys für WeatherReport, der die empfangene nachricht protokolliert
    var weatherReportSpy = new WeatherReport( ) {
      private String receivedMessage;

      @Override
      public void report( String msg ) {
        receivedMessage = msg;
      }
    };

    //act
    //einen Sentinel mit den doubles erstellen und laufen lassen
    var cut = new Sentinel(
      weatherOracleStub,
      weatherReportSpy,
      List.of( weatherCheckSpy1, weatherCheckSpy2 ) );
    cut.run( );

    //assert
    //die protokollierten werte müssen den definierten festwerten entsprechen
    Assertions.assertEquals( weatherParameters, weatherCheckSpy1.receivedWeatherParameters );
    Assertions.assertEquals( weatherParameters, weatherCheckSpy2.receivedWeatherParameters );
    Assertions.assertEquals( expectedMessage, weatherReportSpy.receivedMessage );
  }
}
```

Mit diesem Ergebnis können Sie und vor allem unser aller liebster Milliardär zufrieden sein. Die Qualität des Amazing
Weather Sentinels ist erneut gestiegen, denn jetzt können neue Prüfungen mit einfachen Schritten implementiert werden,
ohne die Gefahr zu laufen, dass vorhandener inkompatibel verändert werden muss. Die Investition hat sich also gelohnt.