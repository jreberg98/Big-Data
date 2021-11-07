# Kolonnefamilie

## Beskrivelse av datasett
Datasettet starter med 2 felt for å identifisere landet, først med navnet på landet og deretter landskoden på 3 bokstaver. Detertter kommer datoen for nå oppføringen gjelder. Med en av de to første feltene og det tredje feltet vil oppføringen være unik, og vil derfor i kombinasjon være nøkkelen.

Etter det kommer det i datasettet flere felt med verdier for vaksiner. Blant annet totalen for antall vaksiner, nye vaksiner den dagen og andell av befolkningen i landet som er vaksinert.

I tillegg er det oppført hvem som har produsert vaksinen, og hvor dataen kommer fra. Dette er data som ikke skal brukes av databasen. I datasettet er det en del oppføringer som mangler verdier. Derfor er det en del null oppføringer, som må tas høyde for når data legges inn i databasen.

### Funksjonalitet
Datasettet brukes til å levere data for vaksinering til nettsiden. Det skal gå å velge et land og en dato, og deretter få dataen som hører til. En bruker skal også kunne få flere oppføringer som hører til samme land, for å kunne velge et tidsrom. Det vil hånderes ved at det gjøres flere kall mot databasen, så vil fungere likt. 

## Design

### Objekter
Objektene i databasen skal ha en nøkkel bestående av land og dato, for eksempel "Norway:2021-04-13". Siden brukeren selv velger hvilken dato og hvilket land man vil ha data for, vil brukes for å finne dataene.

Verdiene i objektet vil holde på dataene for oppføringen, altså tall for vaksinene. Ettersom datasettet mangler verdier på enkelte felt vil det variere fra objekt til objekt hvilke verdier man kan ha med. Det løses med at man har med tilsvarende kolonner i objektet, og ikke tar med de som hadde hatt verdien null.


### Aggregering
Totalen som er vaksinert og antall vaksinedoser som er satt vil være aggregerte verdier. Det er fordi det er totalen av alle enkeltdoser tidligere, summert opp.

Andelen av befolkningen som er vaksinert ville ikke vært med i en SQL database heller, ettersom det også er en beregnet verdi av vaksiner og befolkning. Derimot er den med her ettersom det er en verdi som kan være interresant å bruke.

# Kode
// Lage tabell

CREATE KEYSPACE prosjekt WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE prosjekt;

DROP TABLE vaccine;
CREATE TABLE vaccine (
    country TEXT,
    countryISO TEXT,
    date TEXT,
    totalVaccinations float,
    totalPeopleVaccinated float,
    totalPeopleFullyVaccinated float,
    dailyVaccinatedRaw float,
    dailyVaccinated float,
    totalDosesPercent float,
    totalVaccinatedPercent float,
    totalFullyVaccinatedPercent float,
    vaccinesPercent float,
    vaccinesPerDayPerMilion TEXT,
    source TEXT,
    sourceWeb TEXT,
    PRIMARY KEY (country,date)
);
DESCRIBE vaccine; // Viser fram tabellen, for å dobbeltsjekke. Ingen grunn til å gjøre ellers



// Lese inn data
// Filen med datagrunnlaget må da være plasset på rett lokasjon

COPY vaccine ( country, countryISO, date, totalVaccinations, totalPeopleVaccinated, totalPeopleFullyVaccinated, dailyVaccinatedRaw, dailyVaccinated, totalDosesPercent, totalVaccinatedPercent, totalFullyVaccinatedPercent, vaccinesPercent, vaccinesPerDayPerMilion, source, sourceWeb)
FROM 'prosjekt/kode/datasett/country_vaccinations.csv'
WITH HEADER = TRUE;

// Hente ut data

// Hente alle oppføringer for et land
SELECT * FROM vaccine WHERE country = 'Norway';

// Hente alle oppføringer i et tidsintervall
SELECT * FROM vaccine WHERE country = 'Norway' AND date > '2021-05-07' AND date < '2021-07-01';

// Verdier i '' byttes ut til det brukeren hadde gitt som input