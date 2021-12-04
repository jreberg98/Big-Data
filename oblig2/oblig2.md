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
Siden nøkkelen består av et land og en dato skal hvert objekt holde på dataene for den ene dagen i det landet. Objektet vil da ha et felt for hvert felt i datasettet. Altså 4 felt med tall for den aktuelle dagen, 3 felter med totaler for det landet frem til valgt dato, og så et felt med hvilken region som er med.

### Eksempel
For å illustrere hvordan et objekt kan se ut lager jeg et eksempel i JSON. Det er for å gjøre det lettere å se for seg istendenfor å skjønne ut fra teksten.

```json
"Afganistan:2020-01-22": {
	date: 2020-01-22,
	country: "Afganistan",
	confirmed: 0
	deaths: 0
	recovered: 0
	active: 0
	newCases: 0
	newDeaths: 0
	newRecoveries: 0
	region: "Eastern Mediterranean"
}
```

Eksempelet over er for landet Afganistan for datoen 22/01-2020, og inneholder alle datafeltene for oppføringen. Til svarende er eksempelet under, da bare for en region istedenfor. Verdiene i objektene er 0 ettersom dette er tidlig i pandemien før man smitten hadde spred seg ut av Asia.

```json
"Europe:2020-01-22": {
	date: 2020-01-22,
	region: "Europe",
	confirmed: 0
	deaths: 0
	recovered: 0
	active: 0
	newCases: 0
	newDeaths: 0
	newRecoveries: 0
}
```

### Aggregering
Aggregeringene man har bruk for i objektene er summering av tallene i det tidligere oppføringene. Denne summeringa er allerede med i filen, så databasen trenger strengt tatt da ikke å aggregere noe. Denne aggregeringen er forholdsvis lett å gjennomføre, ettersom det bare å legge til de aggregerte verdiene fra forrige objekt og legge til de som er på objektet man leser inn. Dette kunne man eventuelt gjort for å dobbeltsjekke at dataen stemmer.

Derimot trenger man aggregeriner på større regioner, for å dekke flere land. Det gjøres ved å lage egne objekter per region, per dato. På hver rad står det oppført hvilken region det tilhører, som brukes til å oppdatere en verdi i det aggregerte objektet. Det aggregerte objektet får da alle feltene objektene ellers har, untatt region. Det gjøres da ved å legge sammen alle verdiene i landene i regionen.

De aggregerte objektene opprettes mens man oppretter objektene for land. Dermed har man allerede dataen man skal legge inn, så man slipper å hente det ut av databasen. I de aggregerte objektene må man da legge til verdiene for landet, som vil si at hver regions objekt må oppdateres en gang per land.

### Riak vs etcd
Til dette datasettet trenger man ikke sterk konsistens, men heller eventuel konsistens. En grunn til dette er at dataene for en oppføring kan mangle, og da er det bedre å være klar over det enn å forvente at data alltid skal finnes. I tillegg vil data for land bare oppdateres når de er registrert feil, som bør være sjeldent. Dette er da er fordel for Riak.

En ulempe med Riak er at det er mer funksjonalitet enn det man trenger til dette datasettet. For eksempel vil man ikke trenge å ha filer i objektene, så derfor er det en fordel med etcd som ikke tillater det uansett.

En annen fordel med Riak er at Riak støtter bøtter. Det gjør at man kan samle land i en bøtte mens man har regioner i en annen. På den måten blir det mer separert, som igjen gjør dataen mer oversiktlig. På grunn av dette ville jeg derfor gått for å bruke Riak.

### CAP
Ettersom objektene kommer til å være veldig små vil det ikke gå utover muligheten til å partisjonere objektene. Det gjør at dataen kan fordeles utover på flere noder, som gjør at dataen kan partisjoneres.

Ifølge CAP teoremet må man velge bort enten konsistens, tilgjengelighet eller partisjonering. Ettersom dette er en distribuert databasemotor som bruker flere noder vil ikke det kunne velges bort. Det betyr at valget står mellom konsistens og tilgjengelighet.

For dette datasettet er det viktigere med tilgjengelighet enn konsistens. Grunen til dette er at en større tilgjengelighet gjør at man får svar på spørringer fortere, men det går utover konsistensen. Konstistens går ut på hvor sikker man er på at dataen man får tilbake er den som er nyligst lagt til. Grunnen til at konsistens nedprioriteres i forhold til tilgjengelighet er at dette er data som sjeldent oppdateres, så det vil dermed naturlig skje sjeldent at man får utdatert data.

## C
Selve det å sette nye data inn i databasen vil fungere som en "upsert" funksjon, altså enten update eller insert avhengig om dataen allerede finnes. Ettersom ETCD har muligheten til å overskrive data som allerede ligger lagret vil det dermed holde å bare legge inn data.

Derimot vil det skape problemer for aggregeringene, ettersom de ikke blir oppdatert. For å løse dette må man først sjekke om dataen er lagret i databasen. Prosessen blir da som i listen under.

1. Hente inn de nye dataene
2. Lage en nøkkel av dataene man får.
3. Prøve å hente data fra databasen
   * Dersom data finnes fra før
     1. Finne forskjellen på de ulike feltene på ny og gammel data
     2. Lage nøkkel for det aggregerte objektet
     3. Oppdatere feltene i det aggregerte objektet
   * Dersom data ikke finnes fra før
     1. Lage nøkkel for det aggregerte objektet
     2. Hente det aggregerte objektet
     3. Dersom det finnes et aggregert objekt, oppdater verdiene med det nye objektet
4. Sette inn det oppdaterte aggregerte objektet
5. Sette inn det nye objektet

## D
Dersom dataene hadde vært mine ville jeg gått for sterkere konsistens i databasen. Grunnen til dette er at man da er mer sikker på at aggregeringene blir riktige, og at dataen da blir mer korrekt.

I tillegg hadde jeg valgt etcd istedenfor Riak. Fordelen etcd har over Riak i denne sammenhengen er at etcd har replikasjonsgrupper som sikrer at alle nodene i en klynge har samme data. På den måte vil man da alltid ha minst en backup av dataen, og på den måten skal det mye mer til for at man mister data ved maskinfeil.