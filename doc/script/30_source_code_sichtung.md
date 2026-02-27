## Der Ausgangspunkt

Nach dem Klonen des AmazingWeatherSentinels betrachten Sie das Modul *version1* und stellen fest, dass im
Quellcodeordner lediglich eine Klasse zu finden ist, nämlich *Sentinel*.

```java
public class Sentinel {

  private static final Logger log = LoggerFactory.getLogger( Sentinel.class );

  public static void main( String[] args ) throws AWTException {
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

Auf den ersten Blick ist zu erkennen, dass die Klasse lediglich aus der Main-Methode besteht. Der Code wirkt
übersichtlich und ist leicht verständlich. Grundsätzlich lassen sich folgende Programmschritte erkennen:

- Herstellen einer Verbindung zum Wetterdienst *WeatherOracle*
- Erstellen eines TrayIcons für die Ausgabe der festgehaltenen Warnungen als Balloon-Message
- Prüfung auf potenzielle Windwarnung und ggf. Ausgabe selbiger

Oberflächlich betrachtet scheint der Code und das Design der Methode (und des ganzen Programms) vollkommen in Ordnung zu
sein. Bei einer genaueren Analyse stellt man jedoch fest, dass Elon Bezos bzw. Claus grundlegende architektonische
Konzepte nicht beachtet hat. Dies stellt eine Gefahr für die Funktionalität dar. Bevor diese analysiert werden sollen,
überlegen Sie bitte selbst, welche Fehler gemacht worden sind.

**Welche Probleme haben Sie erkannt?**

Das mangelhafte Benutzererlebnis aufgrund fehlender Übersetzung der Warnmeldungen und die mögliche
NullPointerException beim Laden des TrayIcons, auf die die IDE sogar hingewiesen, der Entwickler die Warnung aber
unterdrückt hat, sind korrekte Antworten, aber tatsächlich nur Nebenschauplätze. Dasselbe gilt auch für die Tatsache,
dass der Wetterservice für die Windgeschwindigkeit insgesamt dreimal gerufen wird, auch wenn dies ein eher
offensichtlicher Bug ist, da sich die Windgeschwindigkeitsvorhersage zwischen den Aufrufen ändern könnte. Sollten Sie
diesen erkannt haben: Sehr gut.  
In Bezug auf die Architektur, und damit beschäftigt sich ja dieses Skript, sind andere Fehler weitaus kritischer. Falls
Ihnen die Größe der Methode und die mangelnde Dekomposition des Codes aufgefallen ist oder Sie sich über die fehlende
Trennung von Verarbeitungslogik und Ausgabe wundern bzw. den sprichwörtlichen Spaghetticode kritisieren, sind Sie auf
der richtigen Spur. Erst recht, wenn Sie bemängeln, dass es keine Unittests gibt und der Code sowieso kaum automatisiert
testbar ist. Denn damit haben Sie bewusst oder unbewusst die Abwesenheit grundlegender SOLID-Prinzipien erkannt, auf die
wir im weiteren Verlauf eingehen werden. Übrigens: Die potenzielle NullPointerException und den Bug bei der
Windgeschwindigkeitsabfrage würden von sorgfältig erstellten Unittests erkannt werden.

Aber warum ist das Nichteinhalten dieser Kriterien denn überhaupt ein Problem?

Aufgrund der Größe der Methode geht die Übersicht verloren, was den Code schwer verständlich erscheinen lässt.
Bevor Sie diese Aussage anzweifeln und argumentieren, dass der Code doch sehr wohl übersichtlich und leicht verständlich
sei, bedenken Sie, dass dieses Beispiel trivial ist. Echte Real-World-Examples bestehen aber nicht nur aus 50 Zeilen
Code, sondern viel öfter aus einem Vielfachen davon.  
Die Verschlingung des Codes bedingt durch fehlende Dekomposition und Abstraktion birgt Gefahren bei jeder Änderung am
Code. Die Änderung einer vorhandenen oder die Implementierung einer neuen Anforderung führt hier zwangsweise zu einer
Anpassung vorhandenen und eigentlich (wenn auch nicht nachweislich) stabilen Codes. Und eine solche Änderung birgt die
Gefahr, dass sich ein neuer Fehler unbemerkt in das Programm einschleicht. Dabei ist hier nicht einmal unbedingt ein
direkter Programmierfehler im neuen Code gemeint, sondern vielmehr die unabsichtliche Einflussnahme auf andere,
eigentlich von der Änderung nicht betroffene Funktionen. Die Gefahr, die von diesen sogenannten Seiteneffekten
ausgeht, ist weitaus größer als die von direkten Bugs, weil sie bei der Entwicklung, insbesondere unter Abwesenheit von
Unittests, nicht unbedingt bemerkt werden und dann wahrscheinlich auch nicht über manuelle Tests abgesichert
werden. Wenn eine eigentlich stabile Funktionalität plötzlich einen Fehler aufweist, spricht man von einem
Regressionsfehler. Diese gilt es in jedem Fall zu vermeiden, da sie in aller Regel in Programmteilen auftreten, die
bereits ausgeliefert sind und daher für die gesamte Nutzerbasis eine Gefahr darstellen.  
Durch Dekomposition kann diese Gefahr auf lediglich von der Änderung betroffene Bereiche reduziert werden. Wenn dann
noch Unittests vorhanden sind, kann die Gefahr nahezu gänzlich eliminiert werden. Unittests benötigen aber eine
bestimmte Codestruktur, so muss der Code etwa deterministisch und Programmabläufe und Ergebnisse beliebig wiederholbar
sein. Diese Voraussetzungen sind hier durch die fehlende Abstraktion von Wetterdienst, des Regelwerks für
Wetterwarnungen und der Ausgabe aber nicht gegeben – ganz zu schweigen von der Tatsache, dass die Unittests insgesamt
fehlen.

Die SOLID-Kriterien helfen dabei, besser modularisierten (und damit abstrahierten) Code zu schreiben, der leichter
verständlich ist und Sicherheit gegenüber veränderten Anforderungen bietet.

[Inhalt](../script.md) | [Nächstes Kapitel](40_was_ist_solid.md)