## Das Single Responsibility Principle

Robert C. Martin definiert das SRP nicht einfach über die Anzahl der Aufgaben, sondern über die Zuständigkeit: Eine
Klasse sollte nur einen einzigen Grund haben, sich zu ändern. Ein Grund kann technisch bedingt sein, etwa abgeleitet aus
den Softwareebenen (z.B. UI, Prozesslogik, Persistenz), oder logisch durch Prozessänderungen. Diese möglichen Gründe
frühzeitig zu erkennen, ist der Schlüssel zu einem guten, dem SRP-konformen Design, in dem Verantwortlichkeiten durch
Dekomposition klar voneinander getrennt sind und eine hohe Kohäsion besteht.

Betrachten Sie noch einmal die Sentinel-Klasse in Version

1. Welche Zuständigkeiten hat sie?

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
Einstiegsklasse auch die Composition Root-Klasse. Folgen Sie weiterhin den Kommentaren in der Klasse, erkennen Sie, dass
die Sentinel-Klasse noch weitere Zuständigkeiten hat. Zusammengefasst:

* Die Klasse agiert als Einstiegspunkt in die Applikation UND
* sie implementiert den Prozess, einen Wetterbericht zu erstellen UND
* sie implementiert Regeln, die zu einer Wetterwarnung führen UND
* sie erzeugt ein Ausgabeformat für die potenzielle Wetterwarnung UND
* sie entscheidet zusätzlich über den zu nutzenden Wetterservice.

Ändert sich also einer dieser Aspekte, bedingt dies eine Änderung der Klasse. Die Klasse hat also mindestens fünf
Gründe, sich zu ändern. Diese könnten sein:

| Aspekt         | Anforderung                               |
|:---------------|:------------------------------------------|
| Einstiegspunkt | Args-Parameter, Dependency Composition    |
| Prozess        | Zusätzliche Benachrichtigungen per E-Mail |
| Regeln         | Temperaturwarnung                         |
| Ausgabe        | Anderes UI-Framework                      |
| Wetterservice  | Ein anderer Service                       |

Die Umsetzung solcher Änderungsgründe kann, bedingt durch die schlechte Trennung der Zuständigkeiten, zu ungewollten
Seiteneffekten führen und das Programm korrumpieren.

Um dies zu vermeiden, müssen die Zuständigkeiten neu geregelt werden. Dies geschieht über die Herausbildung neuer
Klassen, also Dekomposition. Alle Methoden und Felder innerhalb von Klassen sollen fachlich eng zusammengehören (hohe
Kohäsion) und an demselben Ziel arbeiten. Wenn eine Klasse nur für eine Sache zuständig ist, hängen andere Systemteile
auch nur von dieser spezifischen Funktionalität ab. Schauen Sie daher auch von der anderen Seite auf die Klasse: Was
will ein Konsument von der Klasse? Die Konsumentensicht erleichtert auch die Einhaltung der anderen SOLID-Kriterien.
Ein Hilfsmittel beim Refactoring ist es, die Aufgabe der Klasse zu beschreiben. Die Beschreibung sollte unter keinen
Umständen ein "Und" enthalten. Tut sie das, ist das SRP mit hoher Wahrscheinlichkeit verletzt. Vergleichen Sie dazu auch
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

Beginnen wir mit dem Wetterbericht bzw. der Wetterwarnung. Diese erfolgt über ein TrayIcon und das Meldungssystem des
Betriebssystems. Die Instanziierung erfolgt mitten im Prozess, eine Meldung wird nur nach erfolgreicher Prüfung auf eine
Windwarnung ausgegeben (über `trayIcon#displayMessage(...)`). Dies sollte dringend extrahiert werden, und zwar in eine
neue Klasse `BalloonWarning`.
Die Erstellung des TrayIcons kann bereits im Konstruktor erfolgen. Kann das TrayIcon nicht erstellt werden, wird eine
AWTException geloggt und als Runtime-Exception propagiert, was zum Programmabbruch führen wird – ein hier gewolltes
Verhalten, da das ganze Programm an dem TrayIcon hängt (zumindest noch).
Zentral ist die Methode `issue( String message )`, die eine Meldung ans System sendet.

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

Der Wetterservice an sich ist schon gut abstrahiert, sogar mit Schnittstelle, allerdings obliegt es noch dem Sentinel,
die Verbindung herzustellen, das heißt, er entscheidet selbst über die Implementierung. Das könnte man sogar erst einmal
so lassen, um SRP-konform zu sein, das betrachtet aber noch nicht die anderen Prinzipien wie das DIP. In weiser
Voraussicht auf eben jene anderen Prinzipien, insbesondere das Dependency-Inversion-Prinzip, und weil die
Sentinel-Klasse hier als Fabrik für die Erstellung der Serviceverbindung agiert und damit gegen das SRP verstößt, soll
die Wetterserviceanbindung und -kommunikation dennoch in eine neue Klasse `WeatherService` ausgelagert werden. Diese
dient hauptsächlich als Delegator an das WeatherOracle. Die Verbindung wird im Konstruktor hergestellt und die Abfrage
erfolgt durch `getWind()`.

```java
public class WeatherService {
  private static final Logger log = Logger.getLogger( MethodHandles.lookup( ).lookupClass( ).getName( ) );
  private final WeatherOracle weatherOracle;

  public WeatherService( ) {
    log.info( "Connecting to WeatherOracle Service" );
    weatherOracle = new WeatherOracleFactoryProduction( ).get( );
  }

  public int getWind( ) {
    return weatherOracle.getWind( );
  }
}
```

Bleibt noch die Prüfung auf die Windwarnung. Auch diese soll extern aufgehängt werden, nämlich in der Klasse
`WindCheck`. Diese soll die Methode `String checkWind( int wind )` anbieten, die eine Windgeschwindigkeit entgegennimmt
und eine Meldung zurückliefert. Der `WindCheck` kümmert sich also allein um die Prüfung auf eine Windwarnung. Die
Methode `checkWind( int wind )` erfährt gleichzeitig noch eine Featureerweiterung, sodass sie nicht nur bei einer
Windwarnung eine Meldung zurückliefert, sondern auch, wenn sich der Wind im Rahmen befindet. Das ist ein Detail, zu dem
ich mich spontan entschieden habe; man hätte alternativ auch null zurückliefern können, falls keine Warnung besteht,
oder mit Ausnahmen arbeiten können.

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

Der Sentinel selbst führt nur noch den Prozess aus über seine Methode `run()`. Die Abhängigkeiten erzeugt er nicht mehr
während des Prozesses, sondern zur Konstruktionszeit im Konstruktor (was ein neues Problem darstellt, das wir später
lösen werden).

```java
public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherService weatherService;
  private final BalloonWarning balloonWarning;
  private final WindCheck windCheck;

  public Sentinel( ) {
    weatherService = new WeatherService( );
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

Auffällig ist, dass die Main-Methode fehlt. Diese wurde in die Klasse `Main` ausgelagert. Aktuell macht diese Klasse
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

Im Endergebnis sind die Verantwortlichkeiten nun klar getrennt (man beachte: kein "Und", weder im Namen noch in der
Beschreibung).

| Klasse         | Verantwortung                               |
|:---------------|:--------------------------------------------|
| Main           | Einstieg                                    |
| Sentinel       | Prozessierung                               |
| WeatherService | Kommunikation mit dem WeatherOracle-Service |
| WindCheck      | Prüfung auf Windwarnung                     |
| BalloonWarning | Meldungsausgabe                             |

Die Musterlösung finden Sie im Modul *version2*.

Allein mit dieser Trennung ist es nun einfach möglich, einfache und fokussierte Tests für die einzelnen Klassen zu
schreiben. Dies soll hier aber nicht geschehen.
Die Tests würden auch noch nicht in allen Fällen der Definition von Unittests genügen, da zumindest der Sentinel und der
WeatherService starke Abhängigkeiten aufweisen, die auch mit unserer Codeänderung nicht aufgelöst werden können. Was
damit gemeint ist und wie wir dieses Problem lösen, besprechen wir im nächsten Kapitel.

[Inhalt](../script.md) | [Nächstes Kapitel](60_dip.md)