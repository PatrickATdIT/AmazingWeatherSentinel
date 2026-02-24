## Vorbereitung

### IntelliJ
Die Übung wird mit der IDE IntelliJ Idea durchgeführt. Es wird empfohlen, dass Sie diese ebenfalls
nutzen. Installieren Sie daher bitte IntelliJ. Die Community-Edition ist für diese Übung völlig ausreichend. Die
Ultimate-Version kann dank
Ihres Studierendenstatus kostenlos erworben werden.

[Download](https://www.jetbrains.com/idea/)

### Java 25
Stellen Sie sicher, dass Sie über eine Java 25-Version verfügen. Sollte dies nicht der Fall sein, laden Sie diese
vorzugsweise direkt über IntelliJ herunter oder direkt über die Internetpräsenz Ihres bevorzugten Anbieters. Die
Beispiele in dieser Übun wurden mit der Temurin-Distribution erstellt.

[Download](https://adoptium.net/de/temurin/releases)

### Wetterdienste Vorbereiten
Die Wetterdienste werden über Maven eingebunden, jedoch werden sie für diese Übung nicht im Zentralrepository
veröffentlicht. Stattdessen werden Sie ins lokale Repository installiert. Um dies zu erreichen, gehen Sie wie folgt vor:

1. Klonen Sie die unten aufgeführten Repositories von in IntelliJ:
   - https://github.com/PatrickATdIT/WeatherOracle
   - https://github.com/PatrickATdIT/PalantAir
2. Führen Sie das Maven-Install-Goal für beide Projekt aus.

### Der AmazingWeatherSentinel
Sie finden das Herzstück der Übung auf GitHub: https://github.com/PatrickATdIT/AmazingWeatherSentinel

Klonen Sie das Projekt.

Das Projekt ist in mehrere Submodule unterteilt, um die Evolution des Programms von Elon Bezos Ursprungsversion auf
die SOLID-konforme Lösung abzubilden. Jedes Modul beinhaltet einen Evolutionsschritt. Version1 ist die
Ursprungsversion. Machen Sie sich mit dieser vertraut.

Im nächsten Kapitel wird eine Bestandsaufnahme des Projektes durchgeführt.

[Inhalt](../script.md) | [Nächstes Kapitel](30_source_code_sichtung.md)
