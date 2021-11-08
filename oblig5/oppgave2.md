# Oppgave 2
## Forberedelser
start-dfs.sh for å starte hadoop
import org.apache.spark.sql.types._ i scala for å importere datatyper til schema
## Deloppgave 1
hadoop fs -copyFromLocal keyValue.parquet /keyValue.parquet

hadoop fs -copyFromLocal dokument.parquet /dokument.parquet

hadoop fs -copyFromLocal graphNodes.parquet /graphNodes.parquet

hadoop fs -copyFromLocal graphRelations.parquet /graphRelations.parquet

hadoop fs -copyFromLocal kolonnefamilie.parquet /kolonnefamilie.parquet



## Deloppgave 2
### Key Value
val keyValueDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/keyValue.parquet")

keyValueDF.write.format("csv").mode("errorIfExists").save("out/keyValue.csv")

keyValueDF.groupBy("WHORegion", "Date").agg(
    sum("Confirmed"),
    sum("Deaths"),
    sum("Recovered"),
    sum("Active"),
    sum("NewCases"),
    sum("NewDeaths"),
    sum("NewRecovered")    
).write.format("csv").mode("errorIfExists").save("out/keyValueAggregated.csv")

### Dokument
val documentDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/dokument.parquet")

documentDF.select(
    documentDF("country_region_code"),
    documentDF("country_region"),
    documentDF("sub_region_1"),
    documentDF("date"),
    documentDF("retail_and_recreation_percent_change_from_baseline"),
    documentDF("grocery_and_pharmacy_percent_change_from_baseline"),
    documentDF("parks_percent_change_from_baseline"),
    documentDF("transit_stations_percent_change_from_baseline"),
    documentDF("workplaces_percent_change_from_baseline"),
    documentDF("residential_percent_change_from_baseline")
).write.format("csv").mode("errorIfExists").save("out/document.csv")


### Kolonnefamilie
val columnDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/kolonnefamilie.parquet")

columnDF.write.format("csv").mode("errorIfExists").save("out/kolonnefamilie.csv")

### Graph
#### Nodene
val graphNodesDF = spark.read.format("parquet").option("header", false).load("hdfs://localhost:9000/graphNodes.parquet")

graphNodesDF.write.format("csv").mode("errorIfExists").save("out/graphNodes.csv")

#### Relasjonene
val graphRelationsDF = spark.read.format("parquet").option("header", false).load("hdfs://localhost:9000/graphRelations.parquet")

graphRelationsDF.write.format("csv").mode("errorIfExists").save("out/graphRelations.csv")

## Deloppgave 3
Ettersom KeyValue datasettet er der det skjer mest velger jeg å ta utgangspunkt i den. Koden er ment til å generere en CSV fil med alle oppføringer, og en CSV fil som er gruppert på land og dato. Begge delene skal komme fra det samme datagrunnlaget, som da er filen plassert på "hdfs://localhost:9000/keyValue.parquet".

Ettersom dataen skal brukes flere ganger lagres dataen som et dataframe, i første kommando. Filen blir da lest inn, på en node. Siden det er en parquet fil har den mulighet til å bli delt opp, og siden den ligger på et distribuert filsystem så kan ulike noder eventuelt bruke ulike deler. Siden dette kjøres på min maskin er det likevell bare en node som brukes ettersom den bare er en. I tillegg er filen forholdsvis liten, og er derfor ikke delt opp til å begynne med. I tillegg forteller option("header",true) at første linje skal hoppes over, som skjer før selve lesingen.

Den neste linjen er å skrive dataframen til disk som en CSV fil. Jeg har lagt på en mode("errorIfExists",true) som gjør at det kastes en feil hvis filen allerede finnes. Tanken er at når kommandoen kjøres så skal datagrunnlaget oppdateres og man gjør da en tydligere handling. På den måten unngår man uhell. Denne sjekken gjøres før selve skrivinga av filen, ettersom det uansett må gjøres og gjør at datasettet eventuelt ikke trenger å skrives. Resten av kommandoen er for å skrive filen til CSV.

Den neste delen er en lengre kommando som gjør aggregeringen av datasettet. Denne komandoen gjør 2 ting, den starter med å lage et nytt dataframe som den deretter skriver i del 2. Del 2 av kommandoen er tilsvarende kommandoen over, så jeg går ikke gjennom den igjen. Aggregeringen starter med å definere hva som grupperes sammen, i dette tilfellet oppføringer med samme region og dato. I tillegg står det i kommandoen hva som skal gjøres med resten av feltene i oppføringen. I dette tilfellet skal alle tallene summeres sammen. Når det er gjort kan man starte med å gå gjennom dataframen. Det som skjer da er at det lages en ny middlertidig dataframe som holder på verdiene underveis. For hver oppføring i dataframen sjekkes det om region og dato kombinasjonen allerede er i den middlertidige dataframen. Dersom den er der oppdateres verdiene, og hvis ikke legges en ny oppføring til. Etter at det er ferdig skrives resultatene til disk i en CSV fil