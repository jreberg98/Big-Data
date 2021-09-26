# Key-Value datasett

## A - Nye data
### Ny data
Nye data som blir opprettet vil bli en ny linje i filen datasettet er bygget opp av. Siden fila er sortert etter land vil ny data ikke ligges på enden av fila, men innimellom andre linjer. Dataene som blir lagt til vil også bli aggregert av seg selv, så det trenger databasen ikke å forholde seg til. I tillegg er det veldig lite aggregering som trengs, ettersom det bare er å regne ut summer av andre felt.

### Oppdatere data
Dersom tidligere data blir oppdatert, altså at tidligere tall endres, vil kunne by på problemer. Dette er fordi en endring vil kunne påvirke alle aggregeringer fram over, og alle oppføringer som har kommet i etterkant vil da måtte oppdateres.

## B - Dataobjekt
### Nøkkel
Kallene til databasen vil bestå av et eller flere land, og enten på en dato eller en tidsperiode. Siden en Key-Value database er kjapp på å finne oppføringer er det ikke noe problem å ha mange oppføringer og gjøre dette til potensielt mange kall. Derfor er tanken at nøkkelen til hvert objekt består av landet og datoen, for eksempel "Afganistan2020-05-02".

Alternativt kunne man hatt en nøkkel med kun navn på landet, og så en liste der alle datoer er oppført. I dette tilfellet hadde det blitt veldig store objekter med lister som må ittereres, noe Key-Value databaser ikke er designet for å gjøre.

### Objekt
Siden nøkkelen består av et land og en dato skal hvert objekt holde på dataene for den ene dagen i det landet. <span style="color:red">Legge til navn, ettersom hvem datasett jeg bruker</span>

### Aggregering
Aggregeringene man har bruk for i objektene er summering av tallene i det tidligere oppføringene. Denne summeringa er allerede med i filen, så databasen trenger strengt tatt da ikke å aggregere noe. Denne aggregeringen er forholdsvis lett å gjennomføre, ettersom det bare å legge til de aggregerte verdiene fra forrige objekt og legge til de som er på objektet man leser inn. Dette kunne man eventuelt gjort for å dobbeltsjekke at dataen stemmer.

## C

## D