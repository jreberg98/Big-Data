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
Siden nøkkelen består av et land og en dato skal hvert objekt holde på dataene for den ene dagen i det landet. Objektet vil da ha felt for hver

### Aggregering
Aggregeringene man har bruk for i objektene er summering av tallene i det tidligere oppføringene. Denne summeringa er allerede med i filen, så databasen trenger strengt tatt da ikke å aggregere noe. Denne aggregeringen er forholdsvis lett å gjennomføre, ettersom det bare å legge til de aggregerte verdiene fra forrige objekt og legge til de som er på objektet man leser inn. Dette kunne man eventuelt gjort for å dobbeltsjekke at dataen stemmer.

Derimot trenger man aggregeriner på større regioner, for å dekke flere land. Det gjøres ved å lage egne objekter per region, per dato. På hver rad står det oppført hvilken region det tilhører, som brukes til å oppdatere en verdi i det aggregerte objektet. Det aggregerte objektet får da alle feltene objektene ellers har, untatt region. Det gjøres da ved å legge sammen alle verdiene i landene i regionen.

De aggregerte objektene opprettes mens man oppretter objektene for land. Dermed har man allerede dataen man skal legge inn, så man slipper å hente det ut av databasen. I de aggregerte objektene må man da legge til verdiene for landet, som vil si at hver regions objekt må oppdateres en gang per land.

### Riak vs etcd
Til dette datasettet trenger man ikke sterk konsistens, men heller eventuel konsistens. En grunn til dette er at dataene for en oppføring kan mangle, og da er det bedre å være klar over det enn å forvente at data alltid skal finnes. I tillegg vil data for land bare oppdateres når de er registrert feil, som bør være sjeldent. Dette er da er fordel for Riak.

En ulempe med Riak er at det er mer funksjonalitet enn det man trenger til dette datasettet. For eksempel vil man ikke trenge å ha filer i objektene, så derfor er det en fordel med etcd som ikke tillater det uansett.

En annen fordel med Riak er at Riak støtter bøtter. Det gjør at man kan samle land i en bøtte mens man har regioner i en annen. På den måten blir det mer separert, som igjen gjør dataen mer oversiktlig. På grunn av dette ville jeg derfor gått for å bruke Riak.


## C
1. Dataen blir validert
2. Data for samme land, men forrige dato blir hentet inn
3. Slår sammen landet og datoen til en ny nøkkel
4. Legger sammen de nye dataene med de aggregerte verdiene fra forrige objekt
5. Legger inn et nytt objekt med den nye nøkkelen, verdiene som kom og de aggregerte verdiene
6. 1. Dersom det er et objekt for regionen for datoen oppdateres den
   1. Dersom det ikke er et objekt for regionen opprettes det med de samme verdiene som landet har

## D
Dersom dataene hadde vært mine ville jeg gått for sterkere konsistens i databasen. Grunnen til dette er at man da er mer sikker på at aggregeringene blir riktige, og at dataen da blir mer korrekt.

I tillegg hadde jeg valgt etcd istedenfor Riak. Fordelen etcd har over Riak i denne sammenhengen er at etcd har replikasjonsgrupper som sikrer at alle nodene i en klynge har samme data. På den måte vil man da alltid ha minst en backup av dataen, og på den måten skal det mye mer til for at man mister data ved maskinfeil.