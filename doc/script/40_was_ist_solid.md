## Was ist SOLID?

In der Softwareentwicklung führt unstrukturierter Code unweigerlich zu einer Softwareerosion: Das System wird zunehmend
starrer, Änderungen und Erweiterungen fallen schwerer und nehmen mehr Zeit in Anspruch, die Testbarkeit nimmt ab und
Regressionsfehler häufen sich. Dabei findet der Verfall oft schleichend statt.

Um dem entgegenzuwirken, wurden Anfang der 2000er Jahre fünf grundlegende Designprinzipien unter dem Akronym SOLID
zusammengefasst. Die Konzepte wurden maßgeblich von Robert C. Martin formuliert
(s.
[Design Principles and Design Patterns](https://staff.cs.utu.fi/~jounsmed/doos_06/material/DesignPrinciplesAndPatterns.pdf)).
Die Zusammenfassung der Prinzipien unter dem einprägsamen Akronym SOLID geht auf Michael Feathers zurück.

Das Akronym ist besonders sprechend, denn es dient nicht nur als Eselsbrücke für die fünf enthaltenen Prinzipien,
sondern drückt auch die Intention ihrer Anwendung aus, nämlich die Eindämmung der Softwareerosion. Software wird
insgesamt robuster, wartbarer und resilient gegenüber Regressionen. Statt eines unflexiblen, fragilen Konstrukts, das
bei jeder nichttrivialen Änderung Gefahr läuft, einzustürzen, entsteht eine stabile, sprich SOLIDe Anwendung.

**Wie lauten nun die fünf Prinzipien?**

**S – Single Responsibility Principle**  
Eine Klasse sollte nur einen einzigen Grund haben, sich zu ändern. Im Allgemeinen kann man daraus ableiten, dass eine
Klasse nur eine klar abgegrenzte Aufgabe erfüllen sollte. Dies fördert eine hohe Kohäsion, da jede Klasse nur noch
fachlich eng zusammengehörige Aufgaben hat, womit die Gefahr für ungewollte Seiteneffekte reduziert wird.

**O – Open/Closed Principle**  
Softwareeinheiten sollten offen für Erweiterungen, aber geschlossen für Veränderungen
sein. Das Ziel ist es, das Risiko von Regressionen zu senken, indem neues Verhalten hinzugefügt werden kann, ohne den
bereits vorhandenen (und hoffentlich getesteten und folglich stabilen) Bestandscode verändern zu müssen.

**L – Liskov Substitution Principle**  
Benannt nach Barbara Liskov bedingt das LSP vereinfacht ausgedrückt, dass
abgeleitete Klassen sich so verhalten müssen, dass sie ihre Basisklassen ohne Einschränkung ersetzen können. Selbiges
gilt auch für Schnittstellenimplementierungen. Dies garantiert die Verlässlichkeit von Abstraktionen und verhindert
unerwartetes Verhalten oder gar Laufzeitfehler, wenn Implementierungen innerhalb eines Systems ausgetauscht werden.

**I – Interface Segregation Principle**  
Schnittstellen sollten so klein und spezifisch wie möglich sein, statt als große
Universalschnittstellen zu fungieren. Dadurch wird verhindert, dass Klassen von Methoden abhängig werden, die sie für
ihre eigentliche Aufgabe gar nicht benötigen. Das Prinzip sorgt nicht nur für einen schlankeren und übersichtlicheren
Code, sondern fördert die Kohäsion, verdeutlicht Abhängigkeiten und verhindert ungewünschte Seiteneffekte.

**D – Dependency Inversion Principle**  
Module sollten nicht von konkreten Implementierungen abhängen, sondern allein von
Abstraktionen. Dadurch löst sich die starre Bindung zwischen Komponenten und verbessert deren Austauschbarkeit, was
wiederum die Testbarkeit und Stabilität fördert.

In den nachfolgenden Kapiteln werden wir auf die einzelnen Prinzipien eingehen und Elon Bezos *Amazing Weather Sentinel*
schrittweise von einer unstrukturierten, fragilen in eine strukturierte, stabile Architektur überführen.

[Inhalt](../script.md) | [Nächstes Kapitel](50_srp.md)