Wir wollen einen eigenen Service implementieren. Auch hier fällt auf, dass die zwei Extramethoden sehr nerven, vor
allem, weil sie für uns nicht von belang sind, aber sie auch einen Verstoß gegen Liskov darstellen. Die Lösung ist, dass
wir die Schnittstelle erweitern: Der Service enthält nur noch temperatur und wind. Dazu gibt es eine weitere
schnittstelle: ServiceWithHumidity, der die anderen Methoden bereitstellt und von Service abgeleitet wird.
Übrigens: Sollte ein Check auf die Feuchtigkeit nötig sein, ist PalantAir und der neue Service nicht mehr möglich.
