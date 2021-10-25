# Dokumentdatabase

## Datasett
Til denne databasen er det tenkt å bruke datasettet fra google om bevegelsesmønster. Datasettene består først med en del felt for å vise lokasjonen det gjelder for. Etter det kommer datoen for når oppføringen er for. Deretter kommer dataene, altså tallene, for den aktuelle datoen på det valgte stedet.
[Link til datasett](https://www.google.com/covid19/mobility/)

### Bruk
Datasettet handler om hvordan man beveger seg. For nettsiden vil det i hovedsak gi data til hvordan folk beveger seg på valgte datoer i valgte regioner.

I tillegg skal man kunne bruke datasettet til å kunne lete etter når folk endret seg mest. Det vil si at man skal kunne bruke datasettet til å kunne finne de største og minste verdiene, altså størst og minst endring fra normalverdiene. I tillegg må man da ha endringer fra dagen før, for å vite hvordan aktivitetsmønsteret endres fra dag til dag.

For tidsperioder skal det også være mulig å visualisere dataene i en graf. For å få til det vil man trenge flere sett bestående av dato og alle tallene for feltene som skal være med.

## Spørring etter data
Til det første punktet vil det eneste kravet være at man kan spørre databasen etter en region og dato. Det gjør man da med å spørre etter navnet på regionen/landet og datoen. For å få data for flere datoen utføres det da flere spørringer med forskjellige datoer.

For å kunne finne data for når det er størst endring må databasen kunne finne minste og størst verdi i objektene. I tillegg skal man kunne velge et område, på hvilket som helst nivå, som vil si at databasen i tillegg må kunne sammenligne 2 datoer.

### Indeksering
For å effektivisere spørringene er det viktig at dokumentene i dokumentdatabasen indekseres. For eksempel med størst endring trenger databsen indeksering for å kunne ha en oversikt over hvor man kan finne feltet med størst endring. På den måten kan databsen direkte finne dokumentet istedenfor å måtte åpne alle dokumenter for å sammenligne vediene.

Indekseringen skal gjøres med hensyn på hvilket område dokumentet gjelder for. Databasen skal altså være effektiv på å finne oppføringer for et land. Siden hvert land har flere oppføringer skal disse igjen indekseres på dato. Dette er for å effektivisere å hente ut flere oppføringer for et område.

Det skal også være en indeksering på verdiene i hver oppføring. Den skal brukes for å finne laveste og største verdi for hvert felt. Ettersom det er mange felt i hver oppføring brukes det bare gjennomsnittet av alle de andre verdiene. I tillegg kunne det ha vært en indeks per felt, men ettersom det hadde blitt likt å sette opp og ikke byr på særlig mye mer funksjonalitet er ikke det hensiktsmessig.

## Dokumentene
Hvert dokument skal bestå av navnet på området, som vil bestå av varierende antall felt. I tillegg skal datoen være med i hvert dokument. Deretter skal alle verdiene legges inn i dokumentene.

### Aggregering
Utover verdiene som kommer fra datasettet skal det også være et gjennomsnitt av de andre verdiene i hvert dokument. Det er for at databasen skal få kunne lese gjennomsnittet direkte istedenfor at et program skal måtte regne det ut hver gang. I tillegg skal feltet brukes til å indeksere dokumentene, så det må derfor være med. 

Utover det igjen vil databasen brukes mye mer til å hente ut data enn å legge inn og oppdatere data, som vil si at det ikke er noen store ulemper med å bruke tid på å regne det ut og legge inn i indeksen. Tiden man sparer på å ha med feltet vil veie opp mot tiden det tar å regne ut verdien og indeksere.

## Vurderinger
### Ytelse
Ettersom databasen kan bruke indeksene når den skal leite etter rett dokumenter vil kallene bli effektive og raske å gjennomføre. Det går i stor grad ut på at databasen allerede veit ca hvor elementet den leter etter er, så det blir færre steder å leite.

Kall der man leiter etter størst/minst endring for et område kan bli tunge ettersom indeksen bare gjelder for endringen. Dermed har databasen et utgangspunkt til hvor den skal starte å leite, men vil måtte sjekke fram til den finner rett land. Dermed kan man ha uflaks og måtte leite gjennom store deler av databasen, som kan gjøre kall tunge.

Disse tunge kallene kunne man ha unngått med indekser med land, og så verdien. Ettersom det er noe som kunne vært på flere felt, og ikke bare gjennomsnittet, vil det derfor bli mange indekser og gjøre det veldig treigt å lage indeksene.

### Konsistens
Datasettet oppdateres flere ganger i uken, som gjør at nye data vil komme i små mengder. Derfor vil de gå ganske fort å sette inn, så man unngår på den måten at data mangler.

Dette er data som i utgangspunktet aldri må trenge å endres, ettersom de er historiske data over bevegelsesmønster. Derfor vil man ha som utgangspunkt at dataene stemmer når de blir satt inn og deretter ikke skal endres.

Ettersom databasen i stor grad alltid vil ha de samme dataen er det derfor automagisk høy konsistens. Det er derfor viktigere i denne sammenhengen med tilgjengelighet, så man får lest ut data dersom man har data.
