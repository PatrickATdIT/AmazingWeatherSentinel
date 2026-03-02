## Das Dependency Inversion Principle

Im Vorangegangenen Kapitel haben Sie den Amazing Weather Sentinel so refaktorisiert, dass seine Funktionalität
entspreched dem Single Responsibility-Prinzips auf mehrere, verantwortlichkeitsspezifische Klasse verteilt wird. Neben
der kognitiven Erleichterung für die Entwickler wurde vor allem die erhöhte Testbarkeit als positiver Effekt
herausgestellt, da nun fokusierte Unittests geschrieben werden können. Gleichzeitig wurde aber auch kritisiert, dass es
noch immer starre Abhängigkeiten gibt, die nicht aufgelöst werden können, weswegen Unittesting noch immer nicht
durchgehend möglich ist. Was bedeutet das eigentlich?

Versuchen wir zunächst, einen Unittest für den Sentinel zu schreiben. Schauen wir uns dazu die Klasse noch einmal an:

````java
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
````

Was kann hier überhaupt getestet werden? Der Test des Konstruktors ergibt nur bedingt Sinn: Man könnte nur auf die
Instanziierung der Objekte testen. Die sind aber faktisch statisch, da die Instanziierung
nicht fehlschlagen kann. Weiterhin "leben" die Objekte nur innerhalb der Klasse und werden damit zu
Implementierungsdetails. Unittests solltet aber stets gegen die Schnittstelle (anders ausgedrückt, gegen die
versprochene Funktionalität), aber nicht gegen Implementierungsdetails testen, da sich diese ändern können, was den
Unittest obsolet machen würde. Die Tatsache, dass die Objekte nur über Reflexion erreichbar wären, lassen soll hier
außer Acht gelassen werden. Hier kommen wir schon zu dem zentralen Kern des Dependecy Inversion-Prinzips: *Sei nicht
abhängig von Konkretisierungen, sondern von Abstraktionen*.

Bleibt also die `run( )`-Methode. Aber die hat keinen Rückgabewert, und ist damit nicht auf klassischen Weg testbar
(Annahme: Input A führt zu Output B, also kann auf B geprüft werden). Der richtige Ansatz wäre es, den Test gegen den
Prozess durchführen zu lassen. Was ist denn die Erwartung? Was soll von `run( )` machen?

1. Den Wind vom Service bestimmen
2. Den Wind-Wert durch die Wind-Prüfung laufen lassen und eine Nachricht empfangen (Windwarnung bei > 40 km/h, sonst
   alles OK)
3. Die Nachricht über `BalloonWarning#issue( message )` absetzen

Und wann würde der Prozess funktioniueren, wie er soll?

1. Wenn der `WeatherService` weniger als oder exakt 40 km/h meldet, wird `WindCheck` *Wind within limits: %d km/h*
   melden - und das empfängt `BalloonWarning` über die Methode `#issue( message )` .
2. Wenn der `WeatherService` mehr als 40 km/h meldet, wird `WindCheck` *Wind warning: %d km/h*
   melden, was von `BalloonWarning` über die Methode `#issue( message )` empfangen wird.

Folglich könnte man testen, was `BalloonWarning#issue( message )` für eine `message` empfängt. Dazu müsste man aber
BalloonWarning dazu bringen, sich anders zu verhalten, damit es die vom Unittest nötige Assertion durchführen kann. Das
ginge prinzipiell, würde den Code aber testspezifisch aufblasen und das Unittestframework als Abhängigkeit in den
Produktivcode ziehen, also eine schlechte Idee. Darüber hinaus müsste `WeatherService` deterministisch sein, damit er
das zum Testzeitpunkt gewünschte Ergebnis liefert, oder `WindCheck` müsste eine Verhaltensänderung eingeimpft bekommen,
damit es entweder die Warnung oder "Alles OK" zurückgibt, unabhängig vom Wind. All das ist aber aktuell nicht möglich,
da alle drei Abhängigkeiten bereits konkrete Implementierungen sind. Der Sentinel ist komplett abhängig von diesen
Konkretisierungen.

Der Code muss entsprechend des Dependency Inversion-Prinzips umgekehrt werden. Bevor wir das machen, kommen wir zur
Definition: Robert C. Martin hat auch dieses Prinzip definiert.
Die klassische Definition nach Robert C. Martin:

**1. Module höherer Ebene sollten nicht von Modulen niedrigerer Ebene abhängen. Beide sollten von Abstraktionen
abhängen.**  
Die höhere Ebene ist als Hierarchie zu verstehen. Der Sentinel oder der Prozess ist dabei Tonangebend,
nicht aber dier Wetterdienst, die WindPrüfung oder die Ausgabe. Aktuell hängt der Sentinel wie oben gezeigt von allen
drei Abhängigkeiten ab. Ändert sich eine davon, muss der Sentinel geändert werden. Das ist nicht wünschenswert, da es
die Struktur des Codes sehr starr macht und ein Austauschen von Modulen verhindert.
Die Lösung ist eine Schnittstelle einzufügen. Der Sentinel soll von einer Schnittstelle Wetterdienst abhängig sein. Der
konkrete Wetterdienst soll diese Schnittstelle implementieren oder er kann nicht genutzt werden. Damit hängen beide
Module von einer Abstraktion ab.

**2. Abstraktionen sollten nicht von Details abhängen. Details sollten von Abstraktionen abhängen.**  
Der `WindCheck` erwartet aktuell eine Ganzzahl als Windgeschwindigkeit. Das stellt aktuell kein Problem dar. Würde aber
ein anderer Wetterservice die Windgeschwindigkeit als Gleitpunktzahl (float oder duouble) liefern, um Kommastellen
abzubilden, müsste der Prozess das wissen und eine Konvertierung durchführen (Ganzzahlen sind in der Menge der
Gleitpunktzahlen enthalten, aber nicht umgekehrt; da müsste gerundet oder abgeschnitten werden).
Wenn also WindCheck in eine Schnittstelle abstrahiert werden sollte, etwa über die Schnittstelle `Check` (was wir machen
werden), sollte die Methode `Check#check( ... )` einen hierarchisch höherstehenderen Typ erwarten, etwa `Number`, denn
sowohl Ganzzahlen als auch Gleitpunktzahlen sind vom Typ `Number`.

Wenn diese Prinzipien umgesetzt werden, können die Abhängigkeiten beliebig ausgetauscht werden - natürlich nur, sofern
eine entsprechende Öffnung der Klassen eine Dependency Injection erlaubt. Die verfügbaren Methoden sind:

1. Constructor Injection: Die Abhängigkeit wird über einen Parameter im Konstruktor des führenden Objekts übergeben. Das
   ist der empfohlene Standard.
2. Setter Injection: Die Abhängigkeit wird über eine Setter-Methode nach der Konstruktion führenden Objekts
   nachgereicht. Hier besteht die Gefahr der Doppelinstanziierung (erst instanziiert der Constructor eine
   Default-Instanz, dann wird diese per Setter überschrieben).
3. Field Injection: Die Abhängigkeit wird per Annotation (z. B. @Inject) direkt in das Feld injiziert. Das erfordert
   Reflexion. Dependency Injection-Frameworks arbeiten in aller Regel so.

## Aufgabe

Modifizieren Sie Elon Bezos Programm so, dass die Grundsätze des Dependency Inversion-Prinzips umgesetzt werden.
Schreiben Sie anschließend einen Unittest für den Sentinel.

## Lösungsvorschlag

Beginnen wir mit dem Wetterservice. Dieser ist prinzipiell schon sehr gut abstrahiert über die Schnittstelle
`WeatherOracle`. Daher ist hier erneut nichts zu tun.

Der WindCheck sollte über eine Schnittstelle abstrahiert werden. Wie bereits oben beschrieben, sollte diese Abstraktion
nicht von Details abhängen, nämlich dem Integer in der Methode `checkWind( int wind )`. Stattdessen nehmen wir Number.
Auch der Methodenname ist ungünstig für eine Abstrahierung, da *checkWind* impliziert, dass eine Windgeschwindigkeit
betrachtet werden soll, aber kein anderes Wetterdatum. Der Name des Parameters *wind* ist ebenso ungünstig und wird
angepasst. Das heißt, wir erstellen eine Schnittstelle `WeatherCheck` mit der einzigen Methode `String check( Number
characteristic )`.

````java
public interface WeatherCheck {
  String check( Number characteristic );
}
````

Eine Anmerkung dazu: Prinzipiell lautet die Empfehlung, dass Klassennamen aus Substantiven bestehen und Schnittstellen
aus Adjektiven. Von dieser Regelung halte ich aber ... nun ja ... nichts. Nennen Sie Ihre Schnittstellen, wie Sie
wollen, hauptsache die Namen sprechend im Sinne des Clean Code.

Die Klasse `WindCheck` implementiert nun diese Schnittstelle. Dabei stellen wir fest, dass die Schnittstellenmethode
nicht auf `checkWind( int wind )` abgebildet werden kann. Unsere Möglichkeiten sind delegation (check( ... ) kann
checkWind( ... ) rufen ) oder ein Ändern der checkWind-Methode. Die Empfehlung ist immer ersteres, wenn unklar ist, ob
es verwender der Klasse gibt, denn diese wären ja von der Methode `checkWind( int wind )` abhängig. Man könnte
argumentieren "Selbst schuld, hättest du dich mal ans DIP gehalten", aber man sollte sich so eine Entscheidung gut
überlegen, da man seiner Kunden- oder Nutzerbasis gewaltig vor den Kopf stößt. Es ist gängige Praxis, dass einmal
veröffentlichte SChnittstellen (öffentliche Methode von Klassen zählen dazu), nicht plötzlich geändert oder entfernt
werden, sondern dies stets mit einer Deprecation-Nachricht im Voraus geschieht.  
Wir wissen, es gibt keinen Verwender außer uns selbst, daher kann die Klasse bedenkenlos verändert werden. Wir behalten
die Methode `checkWind( int wind )` aber trotzdem, da sie bereits genau macht, was wir wollen. Lediglich die
Sichtbarkeit wird eingeschränkt und die Schnittstellenmethode delegiert ihre Aufgabe dorthin.  
Warum nutzen wir Delegation? Der Grund ist nicht, dass die Methode schon da ist, sondern dass sie, wenn sie schon da
ist, wahrscheinlich auch stabil ist. Bei Delegation vermeiden wir Regressionen.

````java
public class WindCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( Number characteristic ) {
    return checkWind( characteristic.intValue( ) );
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
````

Die Warnung `BalloonWarning` hat eine ausreichend gute öffentliche Schnittstelle (die öffentliche Methode
`ìssue( String message )`. Man könnte Sie mittels Vererbung überschreiben. Da aber nie klar ist, ob eine Vererbungslinie
immer zur Verfügung stehen wird (man kann nur einmal Erben, außerdem kann die Ursprungsklasse final sein), sollte
Komposition das Mittel der Wahl sein. Das heißt, eine Schnittstelle `WeatherReport` soll erstellt werden, der die
Methode `report( String message )` hat.

```java
public interface WeatherReport {
  void report( String message );
}
```

Die `BalloonWarning` muss diese Schnittstelle noch implementieren. Wir gehen hier ähnlich vor, wie beim `WindCheck` und
ändern die öffentlichen Methoden und nutzen Delegation. Im selben Zug benennen wir die Klasse in `TrayReport` um, da
dies eindeutiger scheint.

```java
public class TrayReport implements WeatherReport {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final TrayIcon trayIcon;

  public TrayReport( ) {
    try {
      log.debug( "preparing tray for balloon message display" );
      trayIcon = makeTrayIcon( );
      SystemTray.getSystemTray( ).add( trayIcon );
    } catch( AWTException e ) {
      log.error( "Error creating tray for balloon message display", e );
      throw new RuntimeException( e );
    }
  }

  @Override
  public void report( String message ) {
    issue( message );
  }

  private void issue( String message ) {
    log.debug( "issuing balloon warning message" );
    log.debug( "message: {}", message );
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
      log.warn( "Unable to load AWSentinelLogo.png" );
    Image image = new ImageIcon( imageURL ).getImage( );
    return image;
  }
}
```

Bleibt noch der `Setinel`. Eine Abstraktion können wir uns sparen, da sie für das Verständnis des Prinzips und hier im
Speziellen überhaupt nicht nötig ist. Meine Empfehlung lautet aber: Keine Klasse ohne Schnittstelle!  
Der Sentinel muss dennoch die Abhängigkeiten injiziert bekommen können. Hierfür benutzen wir Constructor Injection, das
heißt, der Constructor erhält entsprechende Parameter; die Klasse erstellt keine Abhängigkeit mehr selbst. Weiterhin
passen wir die Schnittstellenänderungen in der Methode `run( )` an.

````java
public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherService;
  private final WeatherReport weatherReport;
  private final WeatherCheck weatherCheck;

  public Sentinel( WeatherOracle weatherService, WeatherReport weatherReport, WeatherCheck weatherCheck ) {
    this.weatherService = weatherService;
    this.weatherReport = weatherReport;
    this.weatherCheck = weatherCheck;
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var wind = weatherService.getWind( );
    String message = weatherCheck.check( wind );
    weatherReport.report( message );
  }
}
````

Man sieht im Ergebnis, dass der Sentinel nun vollkommen unabhängig von irgendwelchen Konkretisierungen ist, sondern nur
noch Abhängigkeiten zu Abstraktionen, nämlich Schnittstellen aufweist. Das Dependency Inversion-Prinzip wurde also
umgesetzt.

Bleibt nur noch die Frage, wo die konkreten Klassen ins Spiel kommen.

Als Entwickler von objektorientierten, SOLID-konformen Applikationen wollen Sie sich alle möglichkeiten offen halten.
Sie wollen Sie möglichst gar nicht entscheiden, aber einmal muss man konkret werden. Üblicherweise geschieht dies im
sogenannten Composition Root, das ist die Komponente, die die Entscheidungen über die Abhängigkeiten trifft und in die
Applikation gibt bzw. injiziert. Im Amazing Weather Sentinel ist das die Main-Klasse. Sie orchestriert das Programm,
bevor es die Prozessierung startet (nein, das ist kein Verstoß gegen das SRP, denn die Orchestrierung der Module und der
Prozessstart sind eng aneinander geknüpft und arbeiten auf dasselbe Ziel, den Programmstart, hin).
Die Main muss also die Abhängigkeiten instanziieren und an den Konstruktor des Sentinel übergeben.

```java
public class Main {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  static void main( ) {
    log.info( "Starting Amazing Weather Sentinel" );
    var sentinel = new Sentinel(
      new WeatherOracleFactoryProduction( ).get( ),
      new TrayReport( ),
      new WindCheck( ) );
    sentinel.run( );
  }
}
```

Und damit hat die Software Elon Bezos wieder an Qualität gewonnen. Denn nunmehr können wir alles unittesten. Lediglich
müssen die Abhängigkeiten für den Unittest ausgetauscht werden durch sogenannte Mocks, die genau das tun, was wir für
den Unittest brauchen. Der Unittest übernimmt dann die Aufgabe des Composition Root.

Wie sieht der Test nun aus? Wie Eingangs beschrieben, muss er den Prozess selbst prüfen, den der Sentinel implementiert.
Und da dieser nun sehr abstrahiert wurde, gibt es nur noch einen nötigen Test:

1. Das WeatherOracle liefert einen von uns definierten Wert für die Windgeschwindigkeit.
2. Dieser Wert muss im WeatherCheck ankommen, sonst schlägt der Test fehl.
3. Der WeatherCheck gibt eine vorgegbene Meldung zurück.
4. Der WeatherReport muss genau diese Meldung empfangen, sonst schlägt der Test fehl.
5. Wenn kein Fehlschlag festgestellt wurde, ist der Test bestanden.

Noch zwei Anmerkungen zum Test:

1. Die Empfehlung lautet, nur ein Assert pro Test. Im Beispiel gäbe es zwei Asserts (an Stelle 2. und
   4.) Verstehen Sie das nicht falsch: Es geht nicht um die Anzahl der Assertions, sondern um die Anzahl der logischen
   Gründe, warum ein Test fehlschlagen kann. Der Test testet den Prozessablauf und das Kriterium für den Erfolg ist nun
   einmal, dass der Prozess korrekt durchlaufen wird, dass der Datenfluss stimmt: Die Windgeschwindigkeit muss vom
   `WeatherOracle` zum `WeatherCheck` gehen und das daraus resultierende Checkergebnis zum `WeatherReport`.
   Man könnte zwei Tests erstellen, aber das wäre im Großen und Ganzen eine Codeduplizierung und schlichtweg unnötig.
2. Man könnte argumentieren, dass der Test interna, also Implementierungsdetails testet. Das ist auch nicht direkt von
   der Hand zu weißen, da sich der Test anpassen müsste, wenn sich der Prozess ändert. Er ist strukturgebunden.
   Allerdings testet der Test gegen das Protokoll, das heißt gegen das Verhalten des Sentinels, und das ist ja gerade
   das Versprechen, das der Sentinel gibt. Er deligiert Arbeiten, um zu einem bestimmten Ergebnis zu gelangen. Dies gilt
   es, abzusichern. Und das tut der Test.