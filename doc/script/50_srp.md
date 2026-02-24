Eine Klasse soll nur einen Grund haben, sich zu ändern. Die AWSentinel bildet den kompletten Workflow ab. Das muss aufgeteilt werden, weil es mehrere Gründe für eine Änderung gibt: Anderer Service, andere Regeln, andere Benachrichtigung.
1.	Service rufen (ServiceCall)
Erstellt den Service, ruft ihn auf und gibt den wind zurück.
2.	Wind prüfen (WindCheck)
Nimmt wind entgegen; macht die prüfung; wirft exception mit meldung;
3.	Nachricht an Verantwortliche (Notifier)
Nimmt Meldung entgegen und sendet mail (fake)
4.	Die AWSentinel kümmert sich um den Prozessablauf (mache servicecall, rufe WindCheck, falls Exception rufe Notifier).
Damit haben alle Klassen nur noch einen Grund für eine Änderung, nämlich genau dann, falls sich an ihren Domänen etwas ändert.
