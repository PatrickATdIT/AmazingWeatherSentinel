## Das Single Responsibility Principle

Robert C. Martin definiert das SRP nicht einfach über die Anzahl der Aufgaben, sondern über die Zuständigkeit: Eine
Klasse sollte nur einen einzigen Grund haben, sich zu ändern. Ein Grund kann technisch bedingt sein, etwa abgeleitet aus
den Softwareebenen (z. B. UI, Prozesslogik, Persistenz), oder logisch durch Prozessänderungen. Diese möglichen Gründe
frühzeitig zu erkennen, ist der Schlüssel zu einem guten, SRP-konformen Design, in dem Verantwortlichkeiten durch
Dekomposition klar voneinander getrennt sind und eine hohe Kohäsion besteht.

Betrachten Sie noch einmal die Sentinel-Klasse in Version 1. Welche Zuständigkeiten hat sie?

```java
public class Sentinel {

  private static final Logger log = LoggerFactory.getLogger( Sentinel.class );

  static void main( ) throws AWTException {
    log.info( "Starting Amazing Weather Sentinel" );

    // get WeatherOracle service
    log.debug( "Connecting to WeatherOracle Service" );
    WeatherOracle weatherOracle = new WeatherOracleFactoryProduction( ).get( );

    // prepare tray for output
    log.debug( "preparing tray for message display" );
    SystemTray tray = SystemTray.getSystemTray( );
    URL imageURL = Sentinel.class.getResource( "/AWSentinelLogo.png" );
    @SuppressWarnings( "DataFlowIssue" ) Image image = new ImageIcon( imageURL ).getImage( );
    TrayIcon trayIcon = new TrayIcon( image, "AWSentinel" );
    trayIcon.setImageAutoSize( true );
    tray.add( trayIcon );

    // check for wind warning
    log.debug( "Check for wind warning" );
    if( weatherOracle.getWind( ) > 40 ) {
      log.debug( "Wind warning identified: {} ", weatherOracle.getWind( ) );
      String windWarning = "Wind: %d km/h".formatted( weatherOracle.getWind( ) );

      // issue warning
      log.debug( "displaying wind warning message" );
      trayIcon.displayMessage( "Wind Warning", windWarning, TrayIcon.MessageType.WARNING );
    }
  }
}
```

Offensichtlich ist zunächst, dass der Sentinel den Prozess des Erstellens eines Wetterreports oder einer Wetterwarnung
implementiert. Außerdem agiert die Klasse als Einstiegspunkt für die Applikation. Das wird tatsächlich erst später ein
SRP-Problem, wenn wir uns mit Dependency Inversion und Dependency Injection beschäftigen, denn üblicherweise ist die
Einstiegsklasse auch die Composition Root. Folgen Sie weiterhin den Kommentaren in der Klasse, erkennen Sie, dass die
Sentinel-Klasse noch weitere Zuständigkeiten hat. Zusammengefasst:

* Die Klasse agiert als Einstiegspunkt in die Applikation UND
* sie implementiert den Prozess, einen Wetterbericht zu erstellen UND
* sie implementiert Regeln, die zu einer Wetterwarnung führen UND
* sie erzeugt ein Ausgabeformat für die potenzielle Wetterwarnung.

Das einzige Aufgabe, die Sie nicht übernimmt, ist die Wettervorschau. Das macht das `WeatherOracle`.

Ändert sich also einer dieser Aspekte, bedingt dies eine Änderung der Klasse. Die Klasse hat also mindestens vier
Gründe, sich zu ändern. Diese könnten sein:

| Aspekt         | Anforderung                               |
|:---------------|:------------------------------------------|
| Einstiegspunkt | Args-Parameter, Dependency Composition    |
| Prozess        | Zusätzliche Benachrichtigungen per E-Mail |
| Regeln         | Temperaturwarnung                         |
| Ausgabe        | Anderes UI-Framework                      |

Die Umsetzung solcher Änderungen kann, bedingt durch die schlechte Trennung der Zuständigkeiten, zu ungewollten
Seiteneffekten führen und das Programm korrumpieren.

Um dies zu vermeiden, müssen die Zuständigkeiten neu geregelt werden. Dies geschieht über die Herausbildung neuer
Klassen, also Dekomposition. Alle Methoden und Felder innerhalb von Klassen sollen fachlich eng zusammengehören (hohe
Kohäsion) und an demselben Ziel arbeiten. Wenn eine Klasse nur für eine Sache zuständig ist, hängen andere Systemteile
auch nur von dieser spezifischen Funktionalität ab. Schauen Sie daher auch von der anderen Seite auf die Klasse: Was
will ein Konsument von der Klasse? Die Konsumentensicht erleichtert auch die Einhaltung der anderen SOLID-Kriterien.

Ein Hilfsmittel beim Refactoring ist es, die Aufgabe der Klasse zu beschreiben. Die Beschreibung sollte unter keinen
Umständen ein „und“ enthalten. Tut sie das, ist das SRP mit hoher Wahrscheinlichkeit verletzt. Vergleichen Sie dazu auch
noch einmal die Ausführungen oben.

### Aufgabe

Helfen Sie nun Elon Bezos, die Zuständigkeiten abzutrennen, indem Sie neue Klassen herausbilden, die sich um die
einzelnen Bereiche kümmern – und zwar exklusiv. Beachten Sie folgende Guardrails, um Ihnen bei der Umsetzung zu helfen.

### Lösungsvorschlag

Die Zuständigkeiten der Sentinel-Klasse wurden bereits identifiziert. Diese sollen nun über die Erzeugung neuer Klassen
herausgelöst werden. Dabei ist das Ziel, dass der Sentinel nur noch den Prozess ausführt:

1. Windgeschwindigkeit vom Wetterservice holen
2. Windgeschwindigkeit prüfen
3. Wetterbericht absenden

Der Wetterservice an sich ist schon gut abstrahiert, sogar mit Schnittstelle. Da es sich hier um eine externe Bibliothek
handelt, gibt es aktuell nichts zu tun.

Schauen wir als nächstes auf die Prüfung, ob eine Wetterwarnung ob des Windes angebracht ist oder nicht. Die Prüfung
soll natürlich aus dem Sentinel verschwinden und extern aufgehängt werden, nämlich in der Klasse `WindCheck`. Diese
bietet die Methode `String checkWind(int wind)` an, die eine Windgeschwindigkeit entgegennimmt und eine Meldung
zurückliefert. Der `WindCheck` kümmert sich also allein um die Prüfung auf eine Windwarnung.  
Mit dem `WindCheck` erfährt das Gesamtkonstrukt gleichzeitig noch eine Feature-Erweiterung, sodass sie nicht nur bei
einer Windwarnung eine Meldung zurückliefert, sondern auch, wenn sich der Wind im Rahmen befindet. Das ist ein Detail,
um die weitere Verarbeitung im UI zu vereinheitlichen. Man hätte alternativ auch einfach `null` zurückliefern oder mit
Exceptions arbeiten können, wenn man keine OK-Meldung zurückliefern möchte.

```java
public class WindCheck {
  private static final Logger log = Logger.getLogger( MethodHandles.lookup( ).lookupClass( ).getName( ) );

  public String checkWind( int wind ) {
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

Bleibt noch die Warnung selbst, das Modul, dass die Meldung an den Benutzer übermittelt. Diese erfolgt bereits über ein
`TrayIcon` und das Meldungssystem des Betriebssystems. Die Instanziierung erfolgt mitten im Prozess, eine Meldung wird
nur nach erfolgreicher Prüfung auf eine Windwarnung ausgegeben (über `trayIcon#displayMessage(...)`). Dies sollte
dringend extrahiert werden, und zwar in eine neue Klasse `BalloonWarning`. Positiver Nebeneffekt: Da die Meldung auf dem
AWT basiert, wird durch die Dekomposition das UI-Framework vom Rest der Anwendung isoliert. Änderungen an der grafischen
Darstellung betreffen somit nur noch diese eine Klasse, was bedeutet, dass auch das ganze Grafikframework ausgetauscht
werden kann, ohne andere Klassen anfassen zu müssen (zumindest theoretisch, einige Frameworks erfordern aber mehr
Anpassungen, JavaFX zum Beispiel).  
Die Erstellung des `TrayIcons` kann bereits im Konstruktor erfolgen. Kann das `TrayIcon` nicht erstellt werden, wird
eine `AWTException` geloggt und als `RuntimeException` propagiert, was zum Programmabbruch führen wird – ein hier
gewolltes Verhalten, da das gesamte Programm an dem `TrayIcon` hängt (zumindest noch). Zentral ist die Methode
`issue(String message)`, die eine Meldung ans System sendet.

```java
public class BalloonWarning {
  private static final Logger log = Logger.getLogger( MethodHandles.lookup( ).lookupClass( ).getName( ) );
  private final TrayIcon trayIcon;

  public BalloonWarning( ) {
    try {
      log.info( "preparing tray for balloon message display" );
      trayIcon = makeTrayIcon( );
      SystemTray.getSystemTray( ).add( trayIcon );
    } catch( AWTException e ) {
      log.severe( "Error creating tray for balloon message display" );
      throw new RuntimeException( e );
    }
  }

  public void issue( String message ) {
    log.info( "issuing balloon warning message" );
    log.info( "message: " + message );
    trayIcon.displayMessage( "Weather Report", message, TrayIcon.MessageType.INFO );
  }

  private TrayIcon makeTrayIcon( ) {
    final Image image = loadTrayIcon( );
    final TrayIcon trayIcon = new TrayIcon( image, "AWSentinel" );
    trayIcon.setImageAutoSize( true );
    return trayIcon;
  }

  @SuppressWarnings( "DataFlowIssue" )
  private Image loadTrayIcon( ) {
    URL imageURL = Sentinel.class.getResource( "/AWSentinelLogo.png" );
    if( imageURL == null )
      log.warning( "Unable to load AWSentinelLogo.png" );
    Image image = new ImageIcon( imageURL ).getImage( );
    return image;
  }
}
```

Der Sentinel selbst führt nur noch den Prozess über seine Methode `run()` aus. Die Abhängigkeiten erzeugt er nicht mehr
während der Prozessausführung, sondern zur Konstruktionszeit im Konstruktor (was ein neues Problem darstellt, das wir
später
lösen werden).

```java
public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherService;
  private final BalloonWarning balloonWarning;
  private final WindCheck windCheck;

  public Sentinel( ) {
    weatherService = new WeatherOracleFactoryProduction( ).get( );
    balloonWarning = new BalloonWarning( );
    windCheck = new WindCheck( );
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var wind = weatherService.getWind( );
    String message = windCheck.checkWind( wind );
    balloonWarning.issue( message );
  }
}
```

Auffällig ist, dass die `main`-Methode fehlt. Diese wurde in die Klasse `Main` ausgelagert. Aktuell macht diese Klasse
noch nichts anderes, als den Sentinel laufen zu lassen.

```java
public class Main {
  private static final Logger log = Logger.getLogger( MethodHandles.lookup( ).lookupClass( ).getName( ) );

  static void main( ) {
    log.info( "Starting Amazing Weather Sentinel" );
    var sentinel = new Sentinel( );
    sentinel.run( );
  }
}
```

Im Endergebnis sind die Verantwortlichkeiten nun klar getrennt (man beachte: kein „und“, weder im Namen noch in der
Beschreibung).

| Klasse                            | Verantwortung                               |
|:----------------------------------|:--------------------------------------------|
| Main                              | Einstieg                                    |
| Sentinel                          | Prozessierung                               |
| WeatherOracle (externer Anbieter) | Kommunikation mit dem WeatherOracle-Service |
| WindCheck                         | Prüfung auf Windwarnung                     |
| BalloonWarning                    | Meldungsausgabe                             |

Die Musterlösung finden Sie im Modul `version2`.

Schauen Sie auf das Ergebnis. Sie werden wahrscheinlich kritisieren, dass wir jetzt statt einer Klasse und 31 Zeilen
Code ganze fünf Klassen und sage und schreibe 73 Codezeilen haben. Unsere Codebasis hat sich also mehr als Verdoppelt.
Warum ist diese Änderung trotzdem vorteilhaft?

Es wirkt paradox, ist es aber nicht: Ein Teil der Codezeilen ist trivialer Boilerplate-Code, wie Klassen- und
Methodendefinitionen. Zudem ist die Codebase aktuell noch sehr klein. In einem echten Projekt wäre der Effekt prozentual
viel geringer, da das Verhältnis von funktionalem Code zu Boilerplate wächst.

Tatsächlich ist eine größere Codebase jedoch nicht schwerer zu pflegen. Die Struktur, die wir erzeugt haben, vereinfacht
sowohl Wartung als auch Neuentwicklungen. Wo vorher 31 Zeilen im Zusammenhang verstanden werden mussten, muss bei der
nächsten Änderung wahrscheinlich nur noch ein kleiner, isolierter Teil untersucht werden. Das führt zu einer deutlichen
kognitiven Entlastung der Entwickler. Weiterhin sinkt die Chance auf ungewollte Seiteneffekte und Regressionen, da
Änderungen in der Regel eindeutig lokalisierbar sind.

Das wichtigste Argument ist jedoch die Testbarkeit. Allein durch die Neuordnung der Verantwortungsbereiche ist es nun
möglich, einfache, fokussierte Tests für die einzelnen Klassen zu schreiben. Vorher konnte nur der gesamte Prozess
getestet werden. Auf diese Unittests werden wir hier jedoch nicht im Detail eingehen. Die Tests, die Sie jetzt schreiben
könnten, würden zudem noch nicht in allen Fällen der Definition von Unittests genügen, da zumindest der `Sentinel` noch
starke Abhängigkeiten aufweist, die mit unserer Codeänderung mitnichten aufgelöst sind. Was damit gemeint ist und wie
wir dieses Problem lösen, besprechen wir im nächsten Kapitel.

[Inhalt](../script.md) | [Nächstes Kapitel](60_dip.md)