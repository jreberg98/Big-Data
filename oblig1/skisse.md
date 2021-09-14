# Oblig 1

## Skisse
![Skisse over nettside](./skisse.png)

## Forklaring
En nettside der man kan sammenligne tall for ulike land og reginoner. En filter meny brukes til å begrense dataen man får tilbake. Avhengig av hva man velger kommer det ulikt hovedinnhold. For eksempel kommer oversikten over et land opp når man velger et land, men DERSOM man ikke spesefiserer et land blir en region brukt.


### Filter
Mulighet til å filtrere etter visse verdier. Det gjør at man da ikke trenger å bruke alt fra datasettene når det ikke er interessant. Filter vil da være med i alle tilfeller, i tillegg til en av de 3 andre seksjonene.

### Et land (#1)
Viser litt statistikk for et land, smitte og hvor mye innbyggere beveger på seg. Det kombinerer alle datasetta, og kobler datoene mot hverandre der det er datoer.

### Større region (#2)
Viser statistikk for et større område, for eksempel et kontinent. Blant annet hvilke land som først fikk smitte, og hvordan smitten KAN ha spred seg. I tillegg skal det kunne gå å sammenligne land mot hverandre for å lage statistikk.


### Alkohol vs smitte (#3)
Viser sammenhengen mellom smitte og alkoholforbruk, dersom man finner det. I tillegg vises hvordan reiser til parker og resturanter har utviklet seg, som begge er steder der folk kan finne på å drikke. 

I tillegg er det et område for smitte etter fest- og helligdager. Her i Norge kunne det for eksempel vært 17. mai, 1. mai og romjula. Dette er dager mange samles og det kan derfor gi utslag i datasettene.

## Valg av datasett
Datasettene om corona, altså smitte og vaksiner, passer bra sammen siden de har datoer og en tydelig relasjon. Google sitt datasett om hvordan befolkninger beveger seg blir mer spennende å se om kommer til å ha noen tydelig sammenheng.

I tillegg var tanken var egentlig å bruke private størmforbruk og sette det i forhold til smitte, for å få et estimat på hvor mange som hadde hjemmekontor. Dette fant jeg ikke noen datasett for, så derfor ble det alkohol per land istedenfor.

### Linker til datasett

https://www.kaggle.com/rishidamarla/alcohol-life-expectancy?select=drinks.csv

https://www.kaggle.com/jcsantiago/covid19-by-country-with-government-response/version/307


### Beskrivelse av google activety
https://www.google.com/covid19/mobility/

Datasettet inneholder informasjon om hvordan og hvor folk beveger seg, for eksempel til butikker og appotek. Datasettet består av 270 filer, altså en fil per land. I hver fil er det en rad per region per dato, så man kan se på en region i et land på en spesiel dato. De første 10 feltene i hver fil går med til det region og dato, i tillegg til litt ekstra info om hver region. Blant disse feltene er det en del null verdier, men siden det ikke er her verdien i dataene ligger bør ikke det skape problemer.

Deretter kommer de interessante delene i datasettet, nemmelig tallene for hvor folk reiser. Her er det 6 felt som viser endringer i prosent for hvor folk er. Som regel vil de fleste tallene være negative siden folk er mindre på butikker og slikt sammenlignet med før, mens feltet for hvor mange som er hjemme vil være positivt.

Dette datasettet passer bra fror dokumentdatabaser, siden det allerede er dokumenter. I tillegg er det allerede delvvis stukturert, siden det er CSV filer, så det bør gå ganske greit å oversette det til JSON. 
Alternativt kan datasettet passe til kolonnefamilie databaser for å "skjule" verdiene som ikke er i datasettet, men da må eventuelt alle filene slås sammen.

### Beskrivelse av vaksine datasett
https://www.kaggle.com/gpreda/covid-world-vaccination-progress

Vakisnedatasettet består av en fil, med en rad per land og dato. I settet er det 222 land, med datoer fra 1 desember. Settet blir fortsatt oppdatert, så man kan få nye oppføringer dersom man ønsker det.

Settet startet med land og landkode. Deretter kommer datoen for oppføringen, som sammen med land gjør hver rad unik fra de andre. Deretter kommer tall for vaksinasjoner. Først totalen vaksiner og fullvaksinert, så daglige vaksinasjoner og deretter prosent. I starten for hvert land vil det da være en del null verdier, siden det tok litt tid med vaksinering.

Dette datasettet passer best til kolonnefamilie databaser, og da aller helst med superkolonner. På den måten kan man ha en en oversikt over land og deretter over datoer igjen. Dette passer også like bra til key-value databaser, da med en key til en liste med andre key-value sett.