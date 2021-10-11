# Dokumentdatabase

## Datasett
Til denne databasen er det tenkt å bruke datasettet fra google om bevegelsesmønster. Datasettene består først med en del felt for å vise lokasjonen det gjelder for. Etter det kommer datoen for når oppføringen er for. Deretter kommer dataene, altså tallene, for den aktuelle datoen på det valgte stedet.

### Bruk
Datasettet handler om hvordan man beveger seg. For nettsiden vil det i hovedsak gi data til hvordan folk beveger seg på valgte datoer i valgte regioner.

I tillegg skal man kunne bruke datasettet til å kunne lete etter når folk endret seg mest. Det vil si at man skal kunne bruke datasettet til å kunne finne de største og minste verdiene, altså størst og minst endring fra normalverdiene. I tillegg må man da ha endringer fra dagen før, for å vite hvordan aktivitetsmønsteret endres fra dag til dag.

For tidsperioder skal det også være mulig å visualisere dataene i en graf. For å få til det vil man trenge flere sett bestående av dato og alle tallene for feltene som skal være med.

### Spørring etter data
Til det første punktet vil det eneste kravet være at man kan spørre databasen etter en region og dato. Det gjør man da med å spørre etter navnet på regionen/landet og datoen. For å få data for flere datoen utføres det da flere spørringer med forskjellige datoer.

For å kunne finne data for når det er størst endring må databasen kunne finne minste og størst verdi i objektene. I tillegg skal man kunne velge et område, på hvilket som helst nivå, som vil si at databasen i tillegg må kunne sammenligne 2 datoer.


## Objekter
Objektene skal bestå direkte av data fra datasettet

[Link til datasett](https://www.google.com/covid19/mobility/)

### Vurder konsistens / ytelse

* antall kall / tyngde på kall  [mer enn bra nok?!?]
* må det være kjapt ?
* må det være oppdatert alltid ?