# Graf database

## Datasett

Datagrunnlaget består av en CSV fil med en linje per flyvning. Hver linje er igjen delt inn i flere felt. Av datasettet er det forholdsvis lite som skal brukes ettersom da bare er interessant når flyene gikk, og ikke noe om flyene. Derfor trengs egentlig bare flyplass flyet fløy fra og til, og dato. I tillegg er det en annen fil som brukes for å lage nodene til datasettet. Det er en fil som inneholde landskodene for hvert land som er med i datasettet. Det er eventuelt i denne filen det skulle vært mer data for å legge til info på nodene.

Datasettet med kantene endres av et Java program, der flyplasskoden blir byttet til landskoden for landet flyplassen er i. Det er fordi De andre datasetta har data på landsnivå, og det ville derfor vært rart om dette datasettet var mer detaljert.


### Vasking av data
Ettersom de andre datasetta ikke har noen forhold til navn på flyplasser vil det gi lite mening å bruke navnene på flyplassene. Derfor byttes navnet på flyplassen ut med landet flyplassen er i. Dermed vil flyplasser i et land ikke skilles fra hverandre. For å få til det brukes en oversikt med flyplasskoder og landsnavn.

Når filen allerede behandles fjernes også oppføringer som ikke kan brukes fordi de mangler verdier. Det gjør at man ikke trenger å tenke på det når man holder på med databasen.

## Funksjonalitet
Nettsiden skal kunne se hvordan folk har beveget seg fra valgte land. Det er for at man skal kunne se om man har kunnet reise fra land med mye smitte til land med lav smitte. Ettersom smitten har variert gjennom pandemien vil ikke smitttetal være med i databasen.

Det vil si at man skal kunne velge en node, og deretter få en liste over alle andre noder det har gått fly til den siste tiden.

Det motsatte skal også gå å gjøre, altså at man velger en node og så skal få en liste med alle land det har kommet fly fra den siste tiden.

Tiden det har gått skal også kunne velges av brukeren, og avhengig av hvor langt fram eller tilbake i tid man går vil man få flere ellre færre relasjoner.


## Oppbygning

### Noder
Hvert land som er representert i datasettet skal være en node i databasen. Selve noden trenger bare data for hvilket land den er for. I tillegg kunne den hatt data om landet, men vil ikke være relevant i dette tilfellet.

### Relasjoner
Hver flyreise skal representeres som en relasjon mellom 2 noder. Denne relasjonen skal ha dato for når flyreisen ble gjennomført.

Siden alle flyplasser i et land blir slått sammen til en node vil det bli en del flyreiser som går til og fra samme node. De vil ikke ha noen nytte for datasettet, og skal derfor ikke tas med.

## Nye data
Når det kommer nye data til databasen vil det da dreie seg om en ny relasjon mellom to land. Grunnen til dette er at hvert land allerede har en node, og vil derfor allerede være representert i databasen.

For å legge til en ny relasjon vil man måtte legge inn landet flyet fløy fra og til. I tillegg må man ha med dato og tilhørende verdier. Deretter vil det være en enkel insert til databasen.

### Oppdatert datagrunnlag
Når datakilden oppdateres forutsetter det for databasen at ingen tidligere data endres, og at det bare legges til nye data. Den største grunnen til dette er at dette er historiske data, altså data om ting som har skjedd. Derfor vil oppdatering av datakilden fungere på samme måte som når en bruker legger inn data, bare at det er flere innsettninger.

# Kode
// Sette inn nodene
LOAD CSV FROM "file:///country.csv" AS countries CREATE (:Country {code: countries[0]})

// Lager index
CREATE INDEX ON :Country(code)

// Setter inn selve reisene
LOAD CSV FROM "file:///out.csv" AS flights MATCH (departure:Country {code:flights[5]}) MATCH (arriving:Country {code:flights[6]}) MERGE (departure)-[:Flight {date:flights[7]}]->(arriving)


// Hente data (skal i tillegg kunne filtreres på dato)
MATCH (:Country {code:"US"})-[flight:Was]->(country:Country) RETURN country.code, flight.date