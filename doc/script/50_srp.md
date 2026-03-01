im ersten schritt werden die funktionen nach Aufgabenfeld separiert
- Service als Proxy für den Wetterservice
- Output als Proxy für den Output
- Temperaturcheck

- Mainklasse als Starter/Orchestrator (später)

## Das Single Responsibility Principle.

Robert C. Martin definiert das SRP nicht einfach über die Anzahl der Aufgaben, sondern über die Zuständigkeit:

    „Eine Klasse sollte nur einen einzigen Grund haben, sich zu ändern.“

Ein „Grund zur Änderung“ lässt sich am besten als Akteur oder Nutzergruppe identifizieren. Wenn eine Klasse
Anforderungen von verschiedenen Abteilungen (z. B. Buchhaltung, Logistik und UI-Design) gleichzeitig bedient, hat sie zu
viele Verantwortlichkeiten. Ändert die Buchhaltung ihre Logik, riskierst du, ungewollt die Logistik-Funktionen zu
korrumpieren.
Kernmerkmale des SRP

    Hohe Kohäsion: Alle Methoden und Felder innerhalb der Klasse gehören fachlich eng zusammen. Sie arbeiten an demselben Ziel.

    Lose Kopplung: Da die Klasse nur eine Sache tut, hängen andere Systemteile nur von dieser spezifischen Funktionalität ab, nicht von einem „Schweizer Taschenmesser“ an Funktionen.

    Klarer Fokus: Der Name der Klasse sollte präzise beschreiben, was sie tut. Wenn du ein „Und“ im Klassennamen brauchst (z. B. OrderProcessorAndEmailSender), ist das SRP bereits verletzt.

Symptome für eine Verletzung (Code Smells)

    Die "Gottklasse": Eine Datei mit tausenden Zeilen Code, die alles von der Datenbankverbindung bis zur PDF-Generierung regelt.

    Häufige Merge-Konflikte: Wenn mehrere Entwickler ständig an derselben Klasse arbeiten müssen, obwohl sie an völlig unterschiedlichen Features bauen.

    Schwierige Testbarkeit: Wenn du für einen einfachen Unit-Test einer Logik-Methode eine Datenbank, einen Mail-Server und ein File-System mocken musst, weil die Klasse untrennbar damit verwoben ist.

Praktische Anwendung: Separation of Concerns

Um das SRP umzusetzen, nutzt man die Separation of Concerns (SoC). Man identifiziert verschiedene
Verantwortungsbereiche:

    Business Logic: Die Kernregeln deiner Anwendung (z. B. Preisberechnung).

    Persistence: Wie und wo Daten gespeichert werden (Datenbank, Dateisystem).

    Presentation: Wie Daten angezeigt werden (JSON-Response, HTML, Konsolenausgabe).

    Infrastructure: Externe Dienste (E-Mail-Versand, Logging, API-Clients).

Vorteile in der Entwicklung

    Wartbarkeit: Wenn sich eine Anforderung ändert, weißt du exakt, in welcher kleinen Klasse du suchen musst.

    Wiederverwendbarkeit: Eine reine EmailService-Klasse kannst du in zehn verschiedenen Projekten nutzen. Eine UserRegistrationWithEmail-Klasse nur in einem.

    Fehlerresistenz: Änderungen an einer kleinen, isolierten Klasse haben eine viel geringere Wahrscheinlichkeit, Seiteneffekte in anderen Systemteilen auszulösen.

Eine Klasse soll nur einen Grund haben, sich zu ändern. Die AWSentinel bildet den kompletten Workflow ab. Das muss
aufgeteilt werden, weil es mehrere Gründe für eine Änderung gibt: Anderer Service, andere Regeln, andere
Benachrichtigung.

1. Service rufen (ServiceCall)
   Erstellt den Service, ruft ihn auf und gibt den wind zurück.
2. Wind prüfen (WindCheck)
   Nimmt wind entgegen; macht die prüfung; wirft exception mit meldung;
3. Nachricht an Verantwortliche (Notifier)
   Nimmt Meldung entgegen und sendet mail (fake)
4. Die AWSentinel kümmert sich um den Prozessablauf (mache servicecall, rufe WindCheck, falls Exception rufe Notifier).
   Damit haben alle Klassen nur noch einen Grund für eine Änderung, nämlich genau dann, falls sich an ihren Domänen
   etwas ändert.
